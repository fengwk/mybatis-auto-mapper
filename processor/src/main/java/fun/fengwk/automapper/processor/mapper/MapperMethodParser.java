package fun.fengwk.automapper.processor.mapper;

import fun.fengwk.automapper.annotation.ExcludeField;
import fun.fengwk.automapper.annotation.FieldName;
import fun.fengwk.automapper.annotation.IncludeField;
import fun.fengwk.automapper.annotation.UseGeneratedKeys;
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

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author fengwk
 */
public class MapperMethodParser {

    private static final Pattern PATTERN_GETTER = Pattern.compile("^get(.+)$");
    private static final Pattern PATTERN_SETTER = Pattern.compile("^set(.+)$");

    private static final List<Predicate<ExecutableElement>> METHOD_FILTERS = Arrays.asList(
            methodElement -> !methodElement.getModifiers().contains(Modifier.NATIVE),
            methodElement -> !methodElement.getModifiers().contains(Modifier.STATIC),
            methodElement -> !methodElement.isDefault(),
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
        Set<String> includeFieldNames = getIncludeFieldNames(methodElement);
        Set<String> excludeFieldNames = getExcludeFieldNames(methodElement);
//        List<Anno> annos = parseAnnotations(methodElement);
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
//                    params.add(new Param(desc.type, name, fieldName, desc.isIterable, desc.isJavaBean, desc.beanFields));
                    params.add(new Param(desc.type, name, fieldName, desc.isIterable, desc.isJavaBean, getAndFilterBeanFields(desc, includeFieldNames, excludeFieldNames)));
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
                    } else if (isJavaDeclared(typeMirror)) {
                        TypeElement typeElement = ((TypeElement) types.asElement(typeMirror));
                        type = typeElement.getQualifiedName().toString();
                        return true;
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

            Map<String, BeanField> beanFieldMap = new LinkedHashMap<>();
            for (TypeElement typeElement : allTypeElements) {
                List<? extends Element> allMembers = elements.getAllMembers(typeElement);
                for (Element element : allMembers) {
                    if (element.getKind() == ElementKind.FIELD) {
                        VariableElement fieldElement = (VariableElement) element;
                        if (!fieldElement.getModifiers().contains(Modifier.STATIC)) {
                            String name = fieldElement.getSimpleName().toString();
                            if (!beanFieldMap.containsKey(name)) {
                                FieldName fieldNameAnnotation = fieldElement.getAnnotation(FieldName.class);
                                String fieldName = fieldNameAnnotation != null ? fieldNameAnnotation.value()
                                        : fieldNamingConverter.convert(StringUtils.upperCamelToLowerCamel(name));
                                UseGeneratedKeys useGeneratedKeysAnnotation = fieldElement.getAnnotation(UseGeneratedKeys.class);
                                boolean useGeneratedKeys = useGeneratedKeysAnnotation != null;
                                beanFieldMap.put(name, new BeanField(name, fieldName, useGeneratedKeys));
                            }
                        }
                    }
                }
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

    }

}
