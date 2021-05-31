package fun.fengwk.automapper.processor.translator;

import java.util.List;

/**
 * @author fengwk
 */
public class Param implements NameEntry {

    /**
     * 类型。
     */
    private String type;

    /**
     * 参数变量名称，即@Param("name")中指定的名称。
     */
    private String name;

    /**
     * 字段在数据库的名称，如果该参数是javaBean，那么该参数为null。
     */
    private String fieldName;

    /**
     * 是否是可迭代的。
     */
    private boolean isIterable;

    /**
     * 是否为javaBean。
     */
    private boolean isJavaBean;

    /**
     * 如果当前参数是javaBean，如果当前对象是集合则判断其泛型对象是否为javaBean，那么该参数有值，否则为null。
     */
    private List<BeanField> beanFields;

    public Param(String type, String name, String fieldName, boolean isIterable, boolean isJavaBean, List<BeanField> beanFields) {
        this.type = type;
        this.name = name;
        this.fieldName = fieldName;
        this.isIterable = isIterable;
        this.isJavaBean = isJavaBean;
        this.beanFields = beanFields;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getFieldName() {
        return fieldName;
    }

    public boolean isIterable() {
        return isIterable;
    }

    public boolean isJavaBean() {
        return isJavaBean;
    }

    public List<BeanField> getBeanFields() {
        return beanFields;
    }

    public BeanField findUseGeneratedKeysField() {
        return beanFields.stream()
                .filter(BeanField::isUseGeneratedKeys)
                .findFirst()
                .orElse(null);
    }
}
