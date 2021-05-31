package fun.fengwk.automapper.processor.util;

import org.junit.Test;

/**
 * @author fengwk
 */
public class StringUtilsTest {

    @Test
    public void testLowerFirst() {
        assert StringUtils.lowerFirst("Username").equals("username");
    }

    @Test
    public void testTrimSuffix1() {
        assert StringUtils.trimSuffix("UserMapper", "Mapper").equals("User");
    }

    @Test
    public void testTrimSuffix2() {
        assert StringUtils.trimSuffix("User", "Mapper").equals("User");
    }

    @Test
    public void testTrimSuffix3() {
        assert StringUtils.trimSuffix("UserMapper", "").equals("UserMapper");
    }

    @Test
    public void testUpperCamelToLowerCamel1() {
        assert StringUtils.upperCamelToLowerCamel("username").equals("username");
    }

    @Test
    public void testUpperCamelToLowerCamel2() {
        assert StringUtils.upperCamelToLowerCamel("FirstName").equals("firstName");
    }

    @Test
    public void testUpperCamelToLowerCamel3() {
        assert StringUtils.upperCamelToLowerCamel("URLName").equals("urlName");
    }

    @Test
    public void testUpperCamelToLowerCamel4() {
        assert StringUtils.upperCamelToLowerCamel("URL").equals("url");
    }

}
