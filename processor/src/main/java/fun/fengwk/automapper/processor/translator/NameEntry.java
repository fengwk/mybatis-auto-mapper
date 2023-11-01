package fun.fengwk.automapper.processor.translator;

/**
 * @author fengwk
 */
public interface NameEntry {

    /**
     * java name
     *
     * @return
     */
    String getName();

    /**
     * db name
     *
     * @return
     */
    String getFieldName();

    /**
     * #{${name}}，如果有typeHandler则为#{${name},typeHandler=${typeHandler}}
     *
     * @return
     */
    String getVariableName();

    boolean isInferredName();

    boolean isInferredFieldName();

}
