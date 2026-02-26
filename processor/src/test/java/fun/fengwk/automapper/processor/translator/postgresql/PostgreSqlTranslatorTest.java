package fun.fengwk.automapper.processor.translator.postgresql;

import fun.fengwk.automapper.processor.naming.LowerUnderScoreCaseConverter;
import fun.fengwk.automapper.processor.translator.BeanField;
import fun.fengwk.automapper.processor.translator.MethodInfo;
import fun.fengwk.automapper.processor.translator.Param;
import fun.fengwk.automapper.processor.translator.Return;
import fun.fengwk.automapper.processor.translator.TranslateContext;
import fun.fengwk.automapper.processor.util.DOMUtils;
import org.junit.Test;

import java.util.Arrays;

/**
 * @author fengwk
 */
public class PostgreSqlTranslatorTest {

    @Test
    public void testPageAllWithOffset() {
        String methodName = "pageAll";

        Param p1 = new Param("int", "int", "offset", "offset", false, false, false, false, null, false, false);
        Param p2 = new Param("int", "int", "limit", "limit", false, false, false, false, null, false, false);

        BeanField bf1 = new BeanField("id", "id", true, false, null, null, true, false, false, false);
        BeanField bf2 = new BeanField("username", "username", false, false, null, null, false, false, false, false);
        BeanField bf3 = new BeanField("userAddress", "user_address", false, false, null, null, false, false, false, false);

        Return ret = new Return("DemoDO", true, Arrays.asList(bf1, bf2, bf3));

        PostgreSqlTranslator translator = new PostgreSqlTranslator(new TranslateContext("demo", "demo", new LowerUnderScoreCaseConverter()));
        translator.translate(new MethodInfo(methodName, Arrays.asList(p1, p2), ret, false));

        String xml = DOMUtils.toString(translator.getDocument());
        assert xml.contains("select id, username, user_address as \"userAddress\"");
        assert xml.contains("limit #{limit} offset #{offset}");
    }

    @Test
    public void testPageAllWithoutOffset() {
        String methodName = "pageAll";

        Param p1 = new Param("int", "int", "limit", "limit", false, false, false, false, null, false, false);

        BeanField bf1 = new BeanField("id", "id", true, false, null, null, true, false, false, false);
        BeanField bf2 = new BeanField("username", "username", false, false, null, null, false, false, false, false);
        BeanField bf3 = new BeanField("userAddress", "user_address", false, false, null, null, false, false, false, false);

        Return ret = new Return("DemoDO", true, Arrays.asList(bf1, bf2, bf3));

        PostgreSqlTranslator translator = new PostgreSqlTranslator(new TranslateContext("demo", "demo", new LowerUnderScoreCaseConverter()));
        translator.translate(new MethodInfo(methodName, Arrays.asList(p1), ret, false));

        String xml = DOMUtils.toString(translator.getDocument());
        assert xml.contains("limit #{limit}");
        assert !xml.contains(" offset #");
    }

    @Test
    public void testFindByStartingWith() {
        String methodName = "findByUsernameStartingWith";

        Param p1 = new Param("java.lang.String", "java.lang.String", "username", "username", false, false, false, false, null, false, false);

        BeanField bf1 = new BeanField("id", "id", true, false, null, null, true, false, false, false);
        BeanField bf2 = new BeanField("username", "username", false, false, null, null, false, false, false, false);

        Return ret = new Return("DemoDO", true, Arrays.asList(bf1, bf2));

        PostgreSqlTranslator translator = new PostgreSqlTranslator(new TranslateContext("demo", "demo", new LowerUnderScoreCaseConverter()));
        translator.translate(new MethodInfo(methodName, Arrays.asList(p1), ret, false));

        String xml = DOMUtils.toString(translator.getDocument());
        assert xml.contains("username like #{username} || '%'");
    }

    @Test
    public void testFindByContaining() {
        String methodName = "findByUsernameContaining";

        Param p1 = new Param("java.lang.String", "java.lang.String", "username", "username", false, false, false, false, null, false, false);

        BeanField bf1 = new BeanField("id", "id", true, false, null, null, true, false, false, false);
        BeanField bf2 = new BeanField("username", "username", false, false, null, null, false, false, false, false);

        Return ret = new Return("DemoDO", true, Arrays.asList(bf1, bf2));

        PostgreSqlTranslator translator = new PostgreSqlTranslator(new TranslateContext("demo", "demo", new LowerUnderScoreCaseConverter()));
        translator.translate(new MethodInfo(methodName, Arrays.asList(p1), ret, false));

        String xml = DOMUtils.toString(translator.getDocument());
        assert xml.contains("username like '%' || #{username} || '%'");
    }

    @Test
    public void testInsertAllSelective() {
        String methodName = "insertAllSelective";

        BeanField bf1 = new BeanField("id", "id", true, false, null, null, true, false, false, false);
        BeanField bf2 = new BeanField("username", "username", false, false, null, null, false, false, false, false);
        BeanField bf3 = new BeanField("userAddress", "user_address", false, false, null, null, false, false, false, false);

        Param param = new Param("demoDO", "demoDO", null, null, false, false, true, true, Arrays.asList(bf1, bf2, bf3), false, false);

        PostgreSqlTranslator translator = new PostgreSqlTranslator(new TranslateContext("demo", "demo", new LowerUnderScoreCaseConverter()));
        translator.translate(new MethodInfo(methodName, Arrays.asList(param), null, false));

        String xml = DOMUtils.toString(translator.getDocument());
        assert xml.contains("insert into demo (username, user_address) values");
        assert xml.contains("separator=\",\"");
        assert xml.contains("<otherwise>default</otherwise>");
        assert !xml.contains("separator=\";\"");
    }

    @Test
    public void testLimitAllWithOffset() {
        String methodName = "limitAll";

        Param p1 = new Param("int", "int", "offset", "offset", false, false, false, false, null, false, false);
        Param p2 = new Param("int", "int", "limit", "limit", false, false, false, false, null, false, false);

        BeanField bf1 = new BeanField("id", "id", true, false, null, null, true, false, false, false);
        BeanField bf2 = new BeanField("username", "username", false, false, null, null, false, false, false, false);
        BeanField bf3 = new BeanField("userAddress", "user_address", false, false, null, null, false, false, false, false);

        Return ret = new Return("DemoDO", true, Arrays.asList(bf1, bf2, bf3));

        PostgreSqlTranslator translator = new PostgreSqlTranslator(new TranslateContext("demo", "demo", new LowerUnderScoreCaseConverter()));
        translator.translate(new MethodInfo(methodName, Arrays.asList(p1, p2), ret, false));

        String xml = DOMUtils.toString(translator.getDocument());
        assert xml.contains("<select id=\"limitAll\" resultType=\"DemoDO\">");
        assert xml.contains("limit #{limit} offset #{offset}");
    }

}
