package fun.fengwk.automapper.processor.translator;

/**
 * @author fengwk
 */
public class BeanField implements NameEntry {

    /**
     * java程序中的名称。
     */
    private String name;

    /**
     * 数据库中的字段名称。
     */
    private String fieldName;

    /**
     * 是否使用useGeneratedKeys。
     */
    private boolean useGeneratedKeys;

    public BeanField(String name, String fieldName, boolean useGeneratedKeys) {
        this.name = name;
        this.fieldName = fieldName;
        this.useGeneratedKeys = useGeneratedKeys;
    }

    public String getName() {
        return name;
    }

    public String getFieldName() {
        return fieldName;
    }

    public boolean isUseGeneratedKeys() {
        return useGeneratedKeys;
    }

}
