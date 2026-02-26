package fun.fengwk.automapper.processor.naming;

/**
 * 对sql语句中的字段名进行转换，例如：`id`。
 *
 * @author fengwk
 */
public class FieldNamingConvert implements NamingConverter {

    protected final NamingConverter delegate;
    protected final String quote;

    public FieldNamingConvert(NamingConverter delegate, String quote) {
        this.delegate = delegate;
        this.quote = quote;
    }

    @Override
    public String convert(String name) {
        return quote + delegate.convert(name) + quote;
    }

}
