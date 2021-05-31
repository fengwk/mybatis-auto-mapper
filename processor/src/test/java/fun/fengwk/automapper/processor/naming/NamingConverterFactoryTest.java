package fun.fengwk.automapper.processor.naming;

import fun.fengwk.automapper.annotation.NamingStyle;
import org.junit.Test;

/**
 * @author fengwk
 */
public class NamingConverterFactoryTest {

    @Test
    public void test1() {
        NamingConverter converter = NamingConverterFactory.getInstance(NamingStyle.LOWER_CAMEL_CASE);
        assert "myURL".equals(converter.convert("myURL"));
    }

    @Test
    public void test2() {
        NamingConverter converter = NamingConverterFactory.getInstance(NamingStyle.UPPER_CAMEL_CASE);
        assert "MyURL".equals(converter.convert("myURL"));
    }

    @Test
    public void test3() {
        NamingConverter converter = NamingConverterFactory.getInstance(NamingStyle.LOWER_UNDER_SCORE_CASE);
        assert "my_url".equals(converter.convert("myURL"));
    }

    @Test
    public void test4() {
        NamingConverter converter = NamingConverterFactory.getInstance(NamingStyle.UPPER_UNDER_SCORE_CASE);
        assert "MY_URL".equals(converter.convert("myURL"));
    }

}
