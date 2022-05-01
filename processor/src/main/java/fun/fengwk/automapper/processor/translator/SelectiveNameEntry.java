package fun.fengwk.automapper.processor.translator;

/**
 * @author fengwk
 */
public interface SelectiveNameEntry extends NameEntry {

    /**
     * 检查当前名称字段是否是可选的。
     *
     * @return
     */
    boolean isSelective();

}
