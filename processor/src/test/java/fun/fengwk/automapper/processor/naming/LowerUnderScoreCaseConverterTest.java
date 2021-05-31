package fun.fengwk.automapper.processor.naming;

import org.junit.Test;

/**
 * @author fengwk
 */
public class LowerUnderScoreCaseConverterTest {

    @Test
    public void test1() {
        LowerUnderScoreCaseConverter converter = new LowerUnderScoreCaseConverter();
        assert "username".equals(converter.convert("username"));
    }

    @Test
    public void test2() {
        LowerUnderScoreCaseConverter converter = new LowerUnderScoreCaseConverter();
        assert "user_info".equals(converter.convert("userInfo"));
    }

    @Test
    public void test3() {
        LowerUnderScoreCaseConverter converter = new LowerUnderScoreCaseConverter();
        assert "_url".equals(converter.convert("URL"));
    }

    @Test
    public void test4() {
        LowerUnderScoreCaseConverter converter = new LowerUnderScoreCaseConverter();
        assert "the_url_name".equals(converter.convert("theURLName"));
    }

    @Test
    public void test5() {
        LowerUnderScoreCaseConverter converter = new LowerUnderScoreCaseConverter();
        assert "the_url_name_x".equals(converter.convert("theURLNameX"));
    }

}
