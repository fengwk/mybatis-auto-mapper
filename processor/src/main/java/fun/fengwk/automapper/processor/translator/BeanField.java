package fun.fengwk.automapper.processor.translator;

/**
 * @author fengwk
 */
public class BeanField implements SelectiveNameEntry {

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

    /**
     * 当前字段是否是可选的。
     */
    private final boolean isSelective;

    public BeanField(String name, String fieldName, boolean useGeneratedKeys, boolean isSelective) {
        this.name = name;
        this.fieldName = fieldName;
        this.useGeneratedKeys = useGeneratedKeys;
        this.isSelective = isSelective;
    }

    public String getName() {
        return name;
    }

    public String getFieldName() {
        return fieldName;
    }

    @Override
    public boolean isInferredName() {
        return false;
    }

    @Override
    public boolean isInferredFieldName() {
        return false;
    }

    public boolean isUseGeneratedKeys() {
        return useGeneratedKeys;
    }

    public boolean isSelective() {
        return isSelective;
    }
}
