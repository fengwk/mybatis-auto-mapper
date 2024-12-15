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

    /**
     * 当前字段是否是自增字段。
     */
    private final String updateIncrement;

    /**
     * 类型处理器。
     */
    private final String typeHandler;

    /**
     * 是否为id
     */
    private final boolean id;

    /**
     * 是否被排除
     */
    private final boolean excludeField;

    /**
     * 是否被包含
     */
    private final boolean includeField;

    /**
     * 是否包含OnDuplicateKeyUpdateIgnore
     */
    private final boolean onDuplicateKeyUpdateIgnore;

    public BeanField(String name, String fieldName, boolean useGeneratedKeys, boolean isSelective,
                     String updateIncrement, String typeHandler, boolean id, boolean excludeField, boolean includeField,
                     boolean onDuplicateKeyUpdateIgnore) {
        this.name = name;
        this.fieldName = fieldName;
        this.useGeneratedKeys = useGeneratedKeys;
        this.isSelective = isSelective;
        this.updateIncrement = updateIncrement;
        this.typeHandler = typeHandler;
        this.id = id;
        this.excludeField = excludeField;
        this.includeField = includeField;
        this.onDuplicateKeyUpdateIgnore = onDuplicateKeyUpdateIgnore;
    }

    public String getName() {
        return name;
    }

    public String getFieldName() {
        return fieldName;
    }

    @Override
    public String getVariableName() {
        if (typeHandler == null) {
            return name;
        } else {
            return name + ",typeHandler=" + typeHandler;
        }
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

    public String getUpdateIncrement() {
        return updateIncrement;
    }

    public String getTypeHandler() {
        return typeHandler;
    }

    public boolean isId() {
        return id;
    }

    public boolean isExcludeField() {
        return excludeField;
    }

    public boolean isIncludeField() {
        return includeField;
    }

    public boolean isOnDuplicateKeyUpdateIgnore() {
        return onDuplicateKeyUpdateIgnore;
    }

}
