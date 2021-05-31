package fun.fengwk.automapper.processor.mapper;

import fun.fengwk.automapper.annotation.FieldName;
import fun.fengwk.automapper.annotation.UseGeneratedKeys;
import fun.fengwk.automapper.processor.naming.NamingConverter;
import fun.fengwk.automapper.processor.translator.BeanField;
import fun.fengwk.automapper.processor.translator.MethodInfo;
import fun.fengwk.automapper.processor.translator.Param;
import fun.fengwk.automapper.processor.translator.Return;
import fun.fengwk.automapper.processor.util.StringUtils;
import org.apache.ibatis.annotations.Select;

import javax.lang.model.element.*;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author fengwk
 */
public class MapperMethodParser {

    private static final List<Predicate<ExecutableElement>> METHOD_FILTERS = Arrays.asList(
            methodElement -> !methodElement.getModifiers().contains(Modifier.NATIVE),
            methodElement -> !methodElement.getModifiers().contains(Modifier.STATIC),
            methodElement -> !methodElement.isDefault(),
            methodElement -> !isObjectMethod(methodElement)
    );

    private static String getTypeName(VariableElement parameter) {
        String typeName = parameter.asType().toString();
        return typeName;
    }

    private static boolean isObjectMethod(ExecutableElement methodElement) {
        String methodName = methodElement.getSimpleName().toString();
        List<? extends VariableElement> parameters = methodElement.getParameters();
        parameters = parameters == null ? Collections.emptyList() : parameters;

        if ("registerNatives".equals(methodName)
                && parameters.isEmpty()) {
            return true;
        }

        if ("getClass".equals(methodName)
                && parameters.isEmpty()) {
            return true;
        }

        if ("hashCode".equals(methodName)
                && parameters.isEmpty()) {
            return true;
        }

        if ("equals".equals(methodName)
                && parameters.size() == 1
                && "java.lang.Object".equals(getTypeName(parameters.get(0)))) {
            return true;
        }

        if ("clone".equals(methodName)
                && parameters.isEmpty()) {
            return true;
        }

        if ("toString".equals(methodName)
                && parameters.isEmpty()) {
            return true;
        }

        if ("notify".equals(methodName)
                && parameters.isEmpty()) {
            return true;
        }

        if ("notifyAll".equals(methodName)
                && parameters.isEmpty()) {
            return true;
        }

        if ("wait".equals(methodName)
                && parameters.size() == 1
                && "long".equals(getTypeName(parameters.get(0)))) {
            return true;
        }

        if ("wait".equals(methodName)
                && parameters.size() == 2
                && "long".equals(getTypeName(parameters.get(0)))
                && "int".equals(getTypeName(parameters.get(1)))) {
            return true;
        }

        if ("wait".equals(methodName)
                && parameters.isEmpty()) {
            return true;
        }

        if ("finalize".equals(methodName)
                && parameters.isEmpty()) {
            return true;
        }

        return false;
    }

    private final Types types;
    private final Elements elements;

    public MapperMethodParser(Types types, Elements elements) {
        this.types = types;
        this.elements = elements;
    }

    public List<MethodInfo> parse(TypeElement mapperElement, NamingConverter fieldNamingConverter) {
        return collectMethodElements(mapperElement).stream()
                .filter(this::filterMethodElement)
                .map(methodElement -> convert(methodElement, mapperElement, fieldNamingConverter))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Set<ExecutableElement> collectMethodElements(TypeElement mapperElement) {
        Set<ExecutableElement> methodElementCollector = new LinkedHashSet<>();
        doCollectMethodElements(mapperElement, methodElementCollector);
        return methodElementCollector;
    }

    private void doCollectMethodElements(TypeElement mapperElement, Set<ExecutableElement> methodElementCollector) {
        List<? extends TypeMirror> interfaces = mapperElement.getInterfaces();
        for (TypeMirror typeMirror : interfaces) {
            Element superMapperElement = types.asElement(typeMirror);
            doCollectMethodElements((TypeElement) superMapperElement, methodElementCollector);
        }

        List<? extends Element> memberElements = elements.getAllMembers(mapperElement);
        for (Element memberElement : memberElements) {
            if (memberElement.getKind() == ElementKind.METHOD) {
                ExecutableElement methodElement = (ExecutableElement) memberElement;
                if (!methodElementCollector.contains(methodElement)) {
                    methodElementCollector.add(methodElement);
                }
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
        Select selectAnnotation = methodElement.getAnnotation(Select.class);
        if (selectAnnotation != null) {
            return null;
        }

        String methodName = methodElement.getSimpleName().toString();

        List<Param> params = new ArrayList<>();
        List<? extends VariableElement> methodParameters = methodElement.getParameters();
        if (methodParameters != null) {
            for (VariableElement methodParameter : methodParameters) {
                TypeDescriptor desc = new TypeDescriptor();
                if (desc.init(mapperElement, methodParameter.asType(), fieldNamingConverter, 0)) {
                    String name = methodParameter.getSimpleName().toString();
                    org.apache.ibatis.annotations.Param paramAnnotation = methodParameter.getAnnotation(org.apache.ibatis.annotations.Param.class);
                    if (paramAnnotation != null) {
                        name = paramAnnotation.value();
                    }
                    FieldName fieldNameAnnotation = methodParameter.getAnnotation(FieldName.class);
                    String fieldName = fieldNameAnnotation != null ? fieldNameAnnotation.value()
                            : fieldNamingConverter.convert(StringUtils.upperCamelToLowerCamel(name));
                    params.add(new Param(desc.type, name, fieldName, desc.isIterable, desc.isJavaBean, desc.beanFields));
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

        return new MethodInfo(methodName, params, ret);
    }

    class TypeDescriptor {

        String type;
        boolean isIterable;
        boolean isJavaBean;
        List<BeanField> beanFields;

        // 初始化，成功返回true
        public boolean init(TypeElement mapperElement, TypeMirror typeMirror, NamingConverter fieldNamingConverter, int depth) {
            if (depth > 1) {
                return false;
            }
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
                    return true;
                case ARRAY:
                    ArrayType arrayType = (ArrayType) typeMirror;
                    TypeMirror componentTypeMirror = arrayType.getComponentType();
                    return init(mapperElement, componentTypeMirror, fieldNamingConverter, depth + 1);
                case DECLARED:
                    if (isIterable(typeMirror)) {
                        isIterable = true;
                        DeclaredType declaredType = (DeclaredType) typeMirror;
                        List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
                        if (!typeArguments.isEmpty()) {
                            return init(mapperElement, typeArguments.get(0), fieldNamingConverter, depth + 1);
                        }
                        return false;
                    } else {
                        TypeElement typeElement = ((TypeElement) types.asElement(typeMirror));
                        type = typeElement.getQualifiedName().toString();
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
                            for (int i = 0; i < typeArguments.size(); i++) {
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

        // 解析JavaBean
        private void parseJavaBean(TypeElement typeElement, NamingConverter fieldNamingConverter) {
            beanFields = new ArrayList<>();
            List<? extends Element> allMembers = elements.getAllMembers(typeElement);
            for (Element element : allMembers) {
                if (element.getKind() == ElementKind.FIELD) {
                    VariableElement fieldElement = (VariableElement) element;
                    String name = fieldElement.getSimpleName().toString();
                    FieldName fieldNameAnnotation = fieldElement.getAnnotation(FieldName.class);
                    String fieldName = fieldNameAnnotation != null ? fieldNameAnnotation.value()
                            : fieldNamingConverter.convert(StringUtils.upperCamelToLowerCamel(name));
                    UseGeneratedKeys useGeneratedKeysAnnotation = fieldElement.getAnnotation(UseGeneratedKeys.class);
                    boolean useGeneratedKeys = useGeneratedKeysAnnotation != null;
                    beanFields.add(new BeanField(name, fieldName, useGeneratedKeys));
                }
            }
        }

    }

}
