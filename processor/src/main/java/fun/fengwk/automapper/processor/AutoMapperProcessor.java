package fun.fengwk.automapper.processor;

import fun.fengwk.automapper.annotation.AutoMapper;
import fun.fengwk.automapper.annotation.DBType;
import fun.fengwk.automapper.annotation.NamingStyle;
import fun.fengwk.automapper.processor.mapper.GlobalConfig;
import fun.fengwk.automapper.processor.mapper.MapperMethodParser;
import fun.fengwk.automapper.processor.naming.NamingConverter;
import fun.fengwk.automapper.processor.naming.NamingConverterFactory;
import fun.fengwk.automapper.processor.translator.MethodInfo;
import fun.fengwk.automapper.processor.translator.TranslateContext;
import fun.fengwk.automapper.processor.translator.Translator;
import fun.fengwk.automapper.processor.translator.TranslatorFactory;
import fun.fengwk.automapper.processor.util.DOMUtils;
import fun.fengwk.automapper.processor.util.StringUtils;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * {@link AutoMapperProcessor}使用<a href="http://hannesdorfmann.com/annotation-processing/annotationprocessing101/">Annotation Processing</a>
 * 处理携带{@link AutoMapper}注解的接口。
 *
 * @author fengwk
 */
public class AutoMapperProcessor extends AbstractProcessor {

    private Types types;
    private Elements elements;
    private Filer filer;
    private Messager messager;

    private MapperMethodParser mapperMethodParser;
    private GlobalConfig globalConfig;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.types = processingEnv.getTypeUtils();
        this.elements = processingEnv.getElementUtils();
        this.filer = processingEnv.getFiler();
        this.messager = processingEnv.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportedAnnotationTypes = new HashSet<>();
        supportedAnnotationTypes.add(AutoMapper.class.getCanonicalName());
        return supportedAnnotationTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(AutoMapper.class)) {
            if (annotatedElement.getKind() != ElementKind.INTERFACE) {
                error(annotatedElement, "Only interface can be annotated with @%s", AutoMapper.class.getSimpleName());
                break;
            }

            TypeElement mapperElement = (TypeElement) annotatedElement;
            try {
                doProcess(mapperElement);
            } catch (Throwable e) {
                error(mapperElement, e.toString());
            }
        }

        return true;
    }

    private void doProcess(TypeElement mapperElement) {
        AutoMapper autoMapper = mapperElement.getAnnotation(AutoMapper.class);
        AutoMapperInfo autoMapperInfo = new AutoMapperInfo(autoMapper);
        autoMapperInfo.preferGlobalConfig(getGlobalConfig());

        DBType dbType = autoMapperInfo.getDbType();
        String mapperSuffix = autoMapperInfo.getMapperSuffix();
        NamingStyle tableNamingStyle = autoMapperInfo.getTableNamingStyle();
        NamingStyle fieldNamingStyle = autoMapperInfo.getFieldNamingStyle();
        String tableName = autoMapperInfo.getTableName();

        NamingConverter tableNamingConverter = NamingConverterFactory.getInstance(tableNamingStyle);
        NamingConverter fieldNamingConverter = NamingConverterFactory.getInstance(fieldNamingStyle);

        // 命名空间
        String namespace = mapperElement.getQualifiedName().toString();

        // 如果没有指定表名那么通过Mapper名称生成表名
        if (tableName == null || tableName.isEmpty()) {
            String mapperName = mapperElement.getSimpleName().toString();
            tableName = mapperNameToTableName(mapperName, mapperSuffix, tableNamingConverter);
        }

        // 解析Mapper方法列表
        List<MethodInfo> methodInfoList = parseMethodInfoList(mapperElement, fieldNamingConverter);

        // 获取mybatis xml文件路径
        String xmlResourceFile = getMybatisXmlResourcePath(mapperElement);

        Translator translator;
        InputStream xmlInput = openResource(xmlResourceFile);
        try {
            translator = TranslatorFactory.getInstance(dbType, new TranslateContext(namespace, tableName, fieldNamingConverter, xmlInput));
            translateAll(translator, methodInfoList);
        } finally {
            close(xmlInput);
        }

        writeResource(xmlResourceFile, DOMUtils.toString(translator.getDocument()));
    }

    // 获取Mapper方法解析器
    private MapperMethodParser getMapperMethodParser() {
        if (mapperMethodParser == null) {
            mapperMethodParser = new MapperMethodParser(types, elements);
        }
        return mapperMethodParser;
    }

    // 获取全局配置
    private GlobalConfig getGlobalConfig() {
        if (globalConfig == null) {
            globalConfig = new GlobalConfig();
            // 尝试加载全局配置
            InputStream globalConfigInputStream = openResource(GlobalConfig.RESOURCE_PATH);
            if (globalConfigInputStream != null) {
                // 如果存在全局配置却加载失败则直接报告异常
                try {
                    globalConfig.load(globalConfigInputStream);
                } catch (IOException e) {
                    throw new AutoMapperException("Failed to load the global config because %s", e.getMessage());
                }
            }
        }
        return globalConfig;
    }

    // Mapper名称转表名
    private String mapperNameToTableName(String mapperName, String mapperSuffix, NamingConverter tableNamingConverter) {
        String tableName = StringUtils.trimSuffix(mapperName, mapperSuffix);
        tableName = StringUtils.upperCamelToLowerCamel(tableName);
        return tableNamingConverter.convert(tableName);
    }

    private List<MethodInfo> parseMethodInfoList(TypeElement mapperElement, NamingConverter fieldNamingConverter) {
        List<MethodInfo> methodInfoList = getMapperMethodParser().parse(mapperElement, fieldNamingConverter);
        return methodInfoList;
    }

    private String getMybatisXmlResourcePath(TypeElement mapperElement) {
        String pkgName = elements.getPackageOf(mapperElement).getQualifiedName().toString();
        String mapperName = mapperElement.getSimpleName().toString();
        String xmlResourceFile = buildMybatisXmlResourcePath(pkgName, mapperName);
        return xmlResourceFile;
    }

    private String buildMybatisXmlResourcePath(String pkgName, String mapperName) {
        String prefix = pkgName == null || pkgName.isEmpty() ? "" : pkgName.replaceAll("\\.", "/");
        String fileName = mapperName + ".xml";
        return prefix.isEmpty() ? fileName : prefix + '/' + fileName;
    }

    private InputStream openResource(String resourceFile) {
        try {
            FileObject existingFile = filer.getResource(StandardLocation.CLASS_OUTPUT, "", resourceFile);
            InputStream inputStream = existingFile.openInputStream();
            return inputStream;
        } catch (IOException e) {
            return null;
        }
    }

    private void writeResource(String resourceFile, String context) {
        try {
            FileObject fileObject = filer.createResource(StandardLocation.CLASS_OUTPUT, "", resourceFile);
            log("write resource %s", resourceFile);
            try (Writer writer = new OutputStreamWriter(fileObject.openOutputStream(), StandardCharsets.UTF_8)) {
                writer.write(context);
            }
        } catch (IOException e) {
            throw new AutoMapperException(e);
        }
    }

    private void translateAll(Translator translator, List<MethodInfo> methodInfoList) {
        for (MethodInfo methodInfo : methodInfoList) {
            try {
                translator.translate(methodInfo);
            } catch (AutoMapperException e) {
                log(e.toString());
            }
        }
    }

    private void close(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                throw new AutoMapperException(e);
            }
        }
    }

    private void log(String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.NOTE, String.format(String.valueOf(msg), args));
    }

    private void error(Element e, String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.ERROR, String.format(String.valueOf(msg), args), e);
    }

}
