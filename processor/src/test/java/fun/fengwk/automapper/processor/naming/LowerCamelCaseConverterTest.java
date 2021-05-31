package fun.fengwk.automapper.processor.naming;

import org.junit.Test;

/**
 * @author fengwk
 */
public class LowerCamelCaseConverterTest {

    @Test
    public void test1() {
        LowerCamelCaseConverter converter = new LowerCamelCaseConverter();
        assert "username".equals(converter.convert("username"));
    }

    @Test
    public void test2() {
        LowerCamelCaseConverter converter = new LowerCamelCaseConverter();
        assert "userInfo".equals(converter.convert("userInfo"));
    }

}
