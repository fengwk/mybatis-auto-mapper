package fun.fengwk.automapper.processor.mapper;

import fun.fengwk.automapper.annotation.*;
import fun.fengwk.automapper.processor.naming.NamingConverter;
import fun.fengwk.automapper.processor.translator.BeanField;
import fun.fengwk.automapper.processor.translator.MethodInfo;
import fun.fengwk.automapper.processor.translator.Param;
import fun.fengwk.automapper.processor.translator.Return;
import fun.fengwk.automapper.processor.util.StringUtils;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import javax.lang.model.element.*;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.beans.Introspector;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author fengwk
 */
public class MapperMethodParser {

    private static final Pattern PATTERN_GETTER = Pattern.compile("^(get|is)(.+)$");
    private static final Pattern PATTERN_SETTER = Pattern.compile("^set(.+)$");

    private static final List<Predicate<ExecutableElement>> METHOD_FILTERS = Arrays.asList(
            methodElement -> !methodElement.getModifiers().contains(Modifier.NATIVE),
            methodElement -> !methodElement.getModifiers().contains(Modifier.STATIC),
            methodElement -> !isObjectMethod(methodElement)
    );

    private static boolean isObjectMethod(ExecutableElement methodElement) {
        TypeElement enclosingElement = (TypeElement) methodElement.getEnclosingElement();
        return Object.class.getName().equals(enclosingElement.getQualifiedName().toString());
    }

    private final Types types;
    private final Elements elements;

    public MapperMethodParser(Types types, Elements elements) {
        this.types = types;
        this.elements = elements;
    }

    public List<MethodInfo> parse(TypeElement mapperElement, NamingConverter fieldNamingConverter) {
        List<MethodInfo> methodInfos = collectMethodElements(mapperElement).stream()
            .filter(this::filterMethodElement)
            .map(methodElement -> convert(methodElement, mapperElement, fieldNamingConverter))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        return filterInheritedMethod(methodInfos);
    }

    private List<MethodInfo> filterInheritedMethod(List<MethodInfo> methodInfos) {
        LinkedHashMap<String, MethodInfo> methodInfoMap = new LinkedHashMap<>();
        for (int i = methodInfos.size() - 1; i >= 0; i--) {
            MethodInfo methodInfo = methodInfos.get(i);
            StringBuilder sb = new StringBuilder(methodInfo.getMethodName()).append("(");
            for (int j = 0; j < methodInfo.getParams().size(); j++) {
                Param param = methodInfo.getParams().get(j);
                String type = typeErasure(param.getFullType());
                if (j > 0) {
                    sb.append(", ");
                }
                sb.append(type);
            }
            sb.append(")");
            String signature = sb.toString();
            if (!methodInfoMap.containsKey(signature)) {
                methodInfoMap.put(signature, methodInfo);
            }
        }
        return methodInfoMap.values().stream()
            .filter(m -> !m.isDefault())
            .collect(Collectors.toList());
    }

    private String typeErasure(String fullType) {
        int begin = -1;
        int end = -1;
        for (int i = 0; i < fullType.length(); i++) {
            char c = fullType.charAt(i);
            if (begin == -1 && c == '<') {
                begin = i;
            } else if (c == '>') {
                end = i;
            }
        }
        if (begin >= 0 && end >= 0) {
            return fullType.substring(0, begin) + fullType.substring(end + 1);
        }
        return fullType;
    }

    private List<ExecutableElement> collectMethodElements(TypeElement mapperElement) {
        List<ExecutableElement> methodElementCollector = new ArrayList<>();
        doCollectMethodElements(mapperElement, methodElementCollector);
        return methodElementCollector;
    }

    private void doCollectMethodElements(TypeElement mapperElement, List<ExecutableElement> methodElementCollector) {
        List<? extends TypeMirror> interfaces = mapperElement.getInterfaces();
        for (TypeMirror typeMirror : interfaces) {
            Element superMapperElement = types.asElement(typeMirror);
            doCollectMethodElements((TypeElement) superMapperElement, methodElementCollector);
        }

        List<? extends Element> memberElements = elements.getAllMembers(mapperElement);
        for (Element memberElement : memberElements) {
            if (memberElement.getKind() == ElementKind.METHOD) {
                ExecutableElement methodElement = (ExecutableElement) memberElement;
                methodElementCollector.add(methodElement);
            }
        }
    }

    private boolean filterMethodElement(ExecutableElement executableElement) {
        for (Predicate<ExecutableElement> methodFilter : METHOD_FILTERS) {
            if (!methodFilter.test(executableElement)) {
                return false;
            }
        }
        return true;
    }

    // 将methodElement转换为MethodInfo
    private MethodInfo convert(ExecutableElement methodElement, TypeElement mapperElement, NamingConverter fieldNamingConverter) {
        if (methodElement.getAnnotation(Insert.class) != null
                || methodElement.getAnnotation(Delete.class) != null
                || methodElement.getAnnotation(Update.class) != null
                || methodElement.getAnnotation(Select.class) != null) {
            return null;
        }

        String methodName = methodElement.getSimpleName().toString();
        String methodExpr = methodName;
        MethodExpr methodExprAnno = methodElement.getAnnotation(MethodExpr.class);
        if (methodExprAnno != null && methodExprAnno.value() != null && !methodExprAnno.value().isEmpty()) {
            methodExpr = methodExprAnno.value();
        }

        Set<String> includeFieldNames = getIncludeFieldNames(methodElement);
        Set<String> excludeFieldNames = getExcludeFieldNames(methodElement);
//        List<Anno> annos = parseAnnotations(methodElement);
        List<Param> params = new ArrayList<>();
        List<? extends VariableElement> methodParameters = methodElement.getParameters();
        if (methodParameters != null) {
            for (int i = 0; i < methodParameters.size(); i++) {
                VariableElement methodParameter = methodParameters.get(i);
                TypeDescriptor desc = new TypeDescriptor();
                if (desc.init(mapperElement, methodParameter.asType(), fieldNamingConverter, 0)) {
                    String name = methodParameter.getSimpleName().toString();
                    boolean inferredName = true;
                    org.apache.ibatis.annotations.Param paramAnnotation = methodParameter.getAnnotation(org.apache.ibatis.annotations.Param.class);
                    if (paramAnnotation != null) {
                        name = paramAnnotation.value();
                        inferredName = false;
                    }

                    FieldName fieldNameAnnotation = methodParameter.getAnnotation(FieldName.class);
                    String fieldName = fieldNameAnnotation != null ? fieldNameAnnotation.value()
                            : fieldNamingConverter.convert(StringUtils.upperCamelToLowerCamel(name));
                    boolean inferredFieldName = fieldNameAnnotation == null && inferredName;

                    params.add(new Param(desc.fullType, desc.type, name, fieldName, inferredName, inferredFieldName,
                        desc.isIterable, desc.isJavaBean,
                            getAndFilterBeanFields(desc, includeFieldNames, excludeFieldNames),
                            methodParameter.getAnnotation(Selective.class) != null,
                            methodParameter.getAnnotation(DynamicOrderBy.class) != null));
                }
            }
        }

        TypeDescriptor desc = new TypeDescriptor();
        Return ret = null;
        if (desc.init(mapperElement, methodElement.getReturnType(), fieldNamingConverter, 0)) {
            ret = new Return(desc.type, desc.isJavaBean, desc.beanFields);
        }

        if (ret == null) {// 说明ret不符合规范
            return null;
        }

        return new MethodInfo(methodName, methodExpr, params, ret, methodElement.isDefault());
    }

    private Set<String> getIncludeFieldNames(ExecutableElement methodElement) {
        IncludeField includeField = methodElement.getAnnotation(IncludeField.class);
        IncludeField.List includeFieldList = methodElement.getAnnotation(IncludeField.List.class);
        if (includeField == null && includeFieldList == null) {
            return null;
        }

        Set<String> includeFieldNames = new HashSet<>();
        if (includeField != null && includeField.value() != null) {
            includeFieldNames.add(includeField.value());
        }
        if (includeFieldList != null) {
            IncludeField[] includeFieldInListArray = includeFieldList.value();
            if (includeFieldInListArray != null) {
                for (IncludeField includeFieldInList : includeFieldInListArray) {
                    if (includeFieldInList != null && includeFieldInList.value() != null) {
                        includeFieldNames.add(includeFieldInList.value());
                    }
                }
            }
        }
        return includeFieldNames;
    }

    private Set<String> getExcludeFieldNames(ExecutableElement methodElement) {
        ExcludeField excludeField = methodElement.getAnnotation(ExcludeField.class);
        ExcludeField.List excludeFieldList = methodElement.getAnnotation(ExcludeField.List.class);
        if (excludeField == null && excludeFieldList == null) {
            return null;
        }

        Set<String> excludeFieldNames = new HashSet<>();
        if (excludeField != null && excludeField.value() != null) {
            excludeFieldNames.add(excludeField.value());
        }
        if (excludeFieldList != null) {
            ExcludeField[] excludeFieldInListArray = excludeFieldList.value();
            if (excludeFieldInListArray != null) {
                for (ExcludeField excludeFieldInList : excludeFieldInListArray) {
                    if (excludeFieldInList != null && excludeFieldInList.value() != null) {
                        excludeFieldNames.add(excludeFieldInList.value());
                    }
                }
            }
        }
        return excludeFieldNames;
    }

//    private List<Anno> parseAnnotations(ExecutableElement methodElement) {
//        List<Anno> annos = new ArrayList<>();
//        List<? extends AnnotationMirror> annotationMirrors = methodElement.getAnnotationMirrors();
//        if (annotationMirrors != null) {
//            for (AnnotationMirror annotationMirror : annotationMirrors) {
//                annos.add(new Anno(annotationMirror.getAnnotationType().toString()));
//            }
//        }
//        return annos;
//    }

    private List<BeanField> getAndFilterBeanFields(TypeDescriptor desc, Set<String> includeFieldNames, Set<String> excludeFieldNames) {
        if (!desc.isJavaBean) {
            return null;
        }

        List<BeanField> result = new ArrayList<>();
        for (BeanField bf : desc.beanFields) {
            if (includeFieldNames != null) {
                if (!includeFieldNames.contains(bf.getName())) {
                    continue;
                }
            }
            if (excludeFieldNames != null) {
                if (excludeFieldNames.contains(bf.getName())) {
                    continue;
                }
            }
            result.add(bf);
        }
        return result;
    }

    class TypeDescriptor {

        String fullType;
        String type;
        boolean isIterable;
        boolean isJavaBean;
        List<BeanField> beanFields;

        // 初始化，成功返回true
        public boolean init(TypeElement mapperElement, TypeMirror typeMirror, NamingConverter fieldNamingConverter, int depth) {
            if (depth > 1) {
                return false;
            }
            boolean initRes;
            switch (typeMirror.getKind()) {
                case BOOLEAN:
                case BYTE:
                case SHORT:
                case INT:
                case LONG:
                case CHAR:
                case FLOAT:
                case DOUBLE:
                case VOID:
                    type = typeMirror.toString();
                    fullType = type;
                    return true;
                case ARRAY:
                    ArrayType arrayType = (ArrayType) typeMirror;
                    TypeMirror componentTypeMirror = arrayType.getComponentType();
                    initRes = init(mapperElement, componentTypeMirror, fieldNamingConverter, depth + 1);
                    if (initRes) {
                        fullType = fullType + "[]";
                    }
                    return initRes;
                case DECLARED:
                    if (isIterable(typeMirror)) {
                        isIterable = true;
                        DeclaredType declaredType = (DeclaredType) typeMirror;
                        List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
                        if (!typeArguments.isEmpty()) {
                            initRes = init(mapperElement, typeArguments.get(0), fieldNamingConverter, depth + 1);
                            if (initRes) {
                                TypeElement typeElement = ((TypeElement) types.asElement(typeMirror));
                                fullType = typeElement.getQualifiedName().toString() + "<" + fullType + ">";
                            }
                            return initRes;
                        }
                        return false;
                    } else if (isJavaDeclared(typeMirror)) {
                        TypeElement typeElement = ((TypeElement) types.asElement(typeMirror));
                        type = typeElement.getQualifiedName().toString();
                        fullType = type;
                        return true;
                    } else {
                        TypeElement typeElement = ((TypeElement) types.asElement(typeMirror));
                        type = typeElement.getQualifiedName().toString();
                        fullType = type;
                        isJavaBean = true;
                        parseJavaBean(typeElement, fieldNamingConverter);
                        return true;
                    }
                case TYPEVAR:
                    Set<TypeMirror> supertypes = collectSupertypes(mapperElement.asType());

                    TypeVariable typeVariable = (TypeVariable) typeMirror;
                    TypeMirror variableEnclosingType = typeVariable.asElement().getEnclosingElement().asType();

                    for (TypeMirror sup : supertypes) {
                        if (types.isSameType(types.asElement(sup).asType(), variableEnclosingType)) {
                            List<? extends TypeMirror> supTypeArguments = ((DeclaredType) sup).getTypeArguments();
                            List<? extends TypeMirror> typeArguments = ((DeclaredType) variableEnclosingType).getTypeArguments();
                            for (int i = 0; i < typeArguments.size() && i < supTypeArguments.size(); i++) {
                                if (types.isSameType(typeArguments.get(i), typeVariable)) {
                                    TypeMirror type = supTypeArguments.get(i);
                                    return init(mapperElement, type, fieldNamingConverter, depth);
                                }
                            }
                        }
                    }
                default:
                    return false;
            }
        }

        // 收集所有父类型
        private Set<TypeMirror> collectSupertypes(TypeMirror typeMirror) {
            Set<TypeMirror> supertypes = new HashSet<>();
            doCollectSupertypes(typeMirror, supertypes);
            return supertypes;
        }

        private void doCollectSupertypes(TypeMirror typeMirror, Set<TypeMirror> supertypes) {
            List<? extends TypeMirror> typeMirrors = types.directSupertypes(typeMirror);
            for (TypeMirror sup : typeMirrors) {
                if (!supertypes.contains(sup)) {
                    supertypes.add(sup);
                    doCollectSupertypes(sup, supertypes);
                }
            }
        }

        // 判断typeMirror是否为Iterable
        private boolean isIterable(TypeMirror typeMirror) {
            TypeElement typeElement = (TypeElement) types.asElement(typeMirror);
            if (typeElement == null) {
                return false;
            }

            if (typeElement.getQualifiedName().toString().equals(Iterable.class.getName())) {
                return true;
            }

            TypeMirror superclass = typeElement.getSuperclass();
            if (superclass != null && isIterable(superclass)) {
                return true;
            }

            List<? extends TypeMirror> interfaces = typeElement.getInterfaces();
            if (interfaces != null) {
                for (TypeMirror inter : interfaces) {
                    if (isIterable(inter)) {
                        return true;
                    }
                }
            }

            return false;
        }

        private boolean isJavaDeclared(TypeMirror typeMirror) {
            TypeElement typeElement = (TypeElement) types.asElement(typeMirror);
            String name = typeElement.getQualifiedName().toString();
            return name.startsWith("java.applet")
                    || name.startsWith("java.awt")
                    || name.startsWith("java.beans")
                    || name.startsWith("java.io")
                    || name.startsWith("java.lang")
                    || name.startsWith("java.math")
                    || name.startsWith("java.net")
                    || name.startsWith("java.nio")
                    || name.startsWith("java.rmi")
                    || name.startsWith("java.security")
                    || name.startsWith("java.sql")
                    || name.startsWith("java.text")
                    || name.startsWith("java.time")
                    || name.startsWith("java.util")
//                    || name.startsWith("com.oracle")
//                    || name.startsWith("com.sun")
//                    || name.startsWith("javax.")
//                    || name.startsWith("org.ietf.jgss")
//                    || name.startsWith("org.jcp.xml.dsig.internal")
//                    || name.startsWith("org.omg")
//                    || name.startsWith("org.w3c.dom")
//                    || name.startsWith("org.xml.sax")
//                    || name.startsWith("sun.")
                    ;
        }

        // 解析JavaBean
        private void parseJavaBean(TypeElement typeElement0, NamingConverter fieldNamingConverter) {
            List<TypeElement> allTypeElements = collectSupertypes(typeElement0.asType()).stream()
                    .map(types::asElement)
                    .map(TypeElement.class::cast)
                    .collect(Collectors.toList());
            Collections.reverse(allTypeElements);
            allTypeElements.add(typeElement0);

            Map<String, BeanFieldSource> methodSource = new HashMap<>();
            Map<String, BeanFieldSource> fieldSource = new HashMap<>();
            for (TypeElement typeElement : allTypeElements) {
                List<? extends Element> allMembers = elements.getAllMembers(typeElement);
                for (Element element : allMembers) {
                    if (element.getKind() == ElementKind.METHOD) {
                        ExecutableElement methodElement = (ExecutableElement) element;
                        String methodName = methodElement.getSimpleName().toString();
                        Matcher getterMatcher = PATTERN_GETTER.matcher(methodName);
                        if (methodElement.getParameters().isEmpty()
                            && getterMatcher.find()) {
                            String name = Introspector.decapitalize(getterMatcher.group(2));
                            methodSource.put(name, buildBeanFieldSource(methodElement));
                        }
                    } else if (element.getKind() == ElementKind.FIELD) {
                        VariableElement fieldElement = (VariableElement) element;
                        if (!fieldElement.getModifiers().contains(Modifier.STATIC)) {
                            String name = fieldElement.getSimpleName().toString();
                            fieldSource.put(name, buildBeanFieldSource(fieldElement));
                        }
                    }
                }
            }

            // 合并methodSource和fieldSource构建beanFieldMap，如有冲突methodSource优先级高于fieldSource
            Map<String, BeanField> beanFieldMap = new LinkedHashMap<>();
            for (Map.Entry<String, BeanFieldSource> entry : methodSource.entrySet()) {
                String name = entry.getKey();
                BeanFieldSource methodBfs = entry.getValue();
                BeanFieldSource fieldBfs = fieldSource.remove(name);
                // 目前先采取保守的功能支持，如果不存在对应的field字段则忽略
                // 也就是说仅支持方法级别注解覆盖字段级别注解
                if (fieldBfs == null) {
                    continue;
                }
                methodBfs.merge(fieldBfs);
                putBeanFieldMap(beanFieldMap, name, methodBfs, fieldNamingConverter);
            }
            // 使用剩余的fieldSource构建beanFieldMap
            for (Map.Entry<String, BeanFieldSource> entry : fieldSource.entrySet()) {
                String name = entry.getKey();
                BeanFieldSource fieldBfs = entry.getValue();
                putBeanFieldMap(beanFieldMap, name, fieldBfs, fieldNamingConverter);
            }

            this.beanFields = new ArrayList<>(beanFieldMap.values());

            // 与lombok有执行顺序冲突，暂无法使用getter、setter方法校验bean字段正确性
//            Set<String> getterMatchers = new HashSet<>();
//            Set<String> setterMatchers = new HashSet<>();
//            for (TypeElement typeElement : allTypeElements) {
//                List<? extends Element> allMembers = elements.getAllMembers(typeElement);
//                for (Element element : allMembers) {
//                    if (element.getKind() == ElementKind.METHOD) {
//                        ExecutableElement methodElement = (ExecutableElement) element;
//                        String methodName = methodElement.getSimpleName().toString();
//                        Matcher getterMatcher = PATTERN_GETTER.matcher(methodName);
//                        if (getterMatcher.find()) {
//                            getterMatchers.add(Introspector.decapitalize(getterMatcher.group(1)));
//                        } else {
//                            Matcher setterMatcher = PATTERN_SETTER.matcher(methodName);
//                            if (setterMatcher.find()) {
//                                setterMatchers.add(Introspector.decapitalize(setterMatcher.group(1)));
//                            }
//                        }
//                    }
//                }
//            }
//
//            List<BeanField> beanFields = new ArrayList<>();
//            for (Map.Entry<String, BeanField> entry :beanFieldMap.entrySet()) {
//                if (getterMatchers.contains(entry.getKey()) && setterMatchers.contains(entry.getKey())) {
//                    beanFields.add(entry.getValue());
//                }
//            }
//
//            this.beanFields = beanFields;
        }

        private BeanFieldSource buildBeanFieldSource(Element element) {
            FieldName fieldNameAnnotation = element.getAnnotation(FieldName.class);
            UseGeneratedKeys useGeneratedKeysAnnotation = element.getAnnotation(UseGeneratedKeys.class);
            Selective selective = element.getAnnotation(Selective.class);
            UpdateIncrement updateIncrement = element.getAnnotation(UpdateIncrement.class);
            return new BeanFieldSource(fieldNameAnnotation, useGeneratedKeysAnnotation, selective, updateIncrement);
        }

        private void putBeanFieldMap(
            Map<String, BeanField> beanFieldMap, String name, BeanFieldSource bfs, NamingConverter fieldNamingConverter) {
            if (!beanFieldMap.containsKey(name)) {
                FieldName fieldNameAnnotation = bfs.getFieldNameAnnotation();
                String fieldName = fieldNameAnnotation != null ? fieldNameAnnotation.value()
                        : fieldNamingConverter.convert(StringUtils.upperCamelToLowerCamel(name));
                UseGeneratedKeys useGeneratedKeysAnnotation = bfs.getUseGeneratedKeysAnnotation();
                boolean useGeneratedKeys = useGeneratedKeysAnnotation != null;
                String updateIncrement = bfs.getUpdateIncrement() != null ?
                    bfs.getUpdateIncrement().value() : null;
                beanFieldMap.put(name, new BeanField(name, fieldName, useGeneratedKeys,
                        bfs.getSelective() != null, updateIncrement));
            }
        }

    }

    static class BeanFieldSource {
        private FieldName fieldNameAnnotation;
        private UseGeneratedKeys useGeneratedKeysAnnotation;
        private Selective selective;
        private UpdateIncrement updateIncrement;

        BeanFieldSource(FieldName fieldNameAnnotation, UseGeneratedKeys useGeneratedKeysAnnotation, Selective selective, UpdateIncrement updateIncrement) {
            this.fieldNameAnnotation = fieldNameAnnotation;
            this.useGeneratedKeysAnnotation = useGeneratedKeysAnnotation;
            this.selective = selective;
            this.updateIncrement = updateIncrement;
        }

        public FieldName getFieldNameAnnotation() {
            return fieldNameAnnotation;
        }

        public UseGeneratedKeys getUseGeneratedKeysAnnotation() {
            return useGeneratedKeysAnnotation;
        }

        public Selective getSelective() {
            return selective;
        }

        public UpdateIncrement getUpdateIncrement() {
            return updateIncrement;
        }

        public void merge(BeanFieldSource other) {
            if (fieldNameAnnotation == null) {
                fieldNameAnnotation = other.getFieldNameAnnotation();
            }
            if (useGeneratedKeysAnnotation == null) {
                useGeneratedKeysAnnotation = other.getUseGeneratedKeysAnnotation();
            }
            if (selective == null) {
                selective = other.getSelective();
            }
            if (updateIncrement == null) {
                updateIncrement = other.getUpdateIncrement();
            }
        }
    }

}
