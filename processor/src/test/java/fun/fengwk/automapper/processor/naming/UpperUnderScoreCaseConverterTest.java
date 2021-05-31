package fun.fengwk.automapper.processor.naming;

import org.junit.Test;

/**
 * @author fengwk
 */
public class UpperUnderScoreCaseConverterTest {

    @Test
    public void test1() {
        UpperUnderScoreCaseConverter converter = new UpperUnderScoreCaseConverter();
        assert "USERNAME".equals(converter.convert("username"));
    }

    @Test
    public void test2() {
        UpperUnderScoreCaseConverter converter = new UpperUnderScoreCaseConverter();
        assert "USER_INFO".equals(converter.convert("userInfo"));
    }

    @Test
    public void test3() {
        UpperUnderScoreCaseConverter converter = new UpperUnderScoreCaseConverter();
        assert "_URL".equals(converter.convert("URL"));
    }

    @Test
    public void test4() {
        UpperUnderScoreCaseConverter converter = new UpperUnderScoreCaseConverter();
        assert "THE_URL_NAME".equals(converter.convert("theURLName"));
    }

    @Test
    public void test5() {
        UpperUnderScoreCaseConverter converter = new UpperUnderScoreCaseConverter();
        assert "THE_URL_NAME_X".equals(converter.convert("theURLNameX"));
    }

}
