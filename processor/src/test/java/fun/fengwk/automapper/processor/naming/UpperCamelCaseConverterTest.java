package fun.fengwk.automapper.processor.naming;

import org.junit.Test;

/**
 * @author fengwk
 */
public class UpperCamelCaseConverterTest {

    @Test
    public void test1() {
        UpperCamelCaseConverter converter = new UpperCamelCaseConverter();
        assert "Username".equals(converter.convert("username"));
    }

    @Test
    public void test2() {
        UpperCamelCaseConverter converter = new UpperCamelCaseConverter();
        assert "UserInfo".equals(converter.convert("userInfo"));
    }

}
