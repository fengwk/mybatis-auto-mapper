package fun.fengwk.automapper.processor.translator;

/**
 * @author fengwk
 */
public class ImmutableSelectiveNameEntry implements SelectiveNameEntry {

    private final String name;
    private final String fieldName;
    private final boolean inferredName;
    private final boolean inferredFieldName;
    private final boolean selective;

    public ImmutableSelectiveNameEntry(String name, String fieldName, boolean inferredName, boolean inferredFieldName, boolean selective) {
        this.name = name;
        this.fieldName = fieldName;
        this.inferredName = inferredName;
        this.inferredFieldName = inferredFieldName;
        this.selective = selective;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public boolean isInferredName() {
        return inferredName;
    }

    @Override
    public boolean isInferredFieldName() {
        return inferredFieldName;
    }

    @Override
    public boolean isSelective() {
        return selective;
    }

}
