package fun.fengwk.automapper.processor.naming;

/**
 * 对sql语句中的字段名进行转换，例如：`id`。
 *
 * @author fengwk
 */
public class FieldNamingConvert implements NamingConverter {

    protected final NamingConverter delegate;

    public FieldNamingConvert(NamingConverter delegate) {
        this.delegate = delegate;
    }

    @Override
    public String convert(String name) {
        return "`" + delegate.convert(name) + "`";
    }

}
