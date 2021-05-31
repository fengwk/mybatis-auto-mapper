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

}
