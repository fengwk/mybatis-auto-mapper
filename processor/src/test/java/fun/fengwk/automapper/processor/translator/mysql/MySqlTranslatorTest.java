package fun.fengwk.automapper.processor.translator.mysql;

import fun.fengwk.automapper.processor.naming.LowerUnderScoreCaseConverter;
import fun.fengwk.automapper.processor.translator.*;
import fun.fengwk.automapper.processor.util.DOMUtils;
import org.junit.Test;

import java.util.Arrays;

/**
 * @author fengwk
 */
public class MySqlTranslatorTest {

    @Test
    public void testInsert() {
        String methodName = "insert";

        BeanField bf1 = new BeanField("id", "id", true);
        BeanField bf2 = new BeanField("username", "username", false);
        BeanField bf3 = new BeanField("userAddress", "user_address", false);

        Param param = new Param("demoDO", null, null, false, true, Arrays.asList(bf1, bf2, bf3));

        MySqlTranslator translator = new MySqlTranslator(new TranslateContext("demo", "demo", new LowerUnderScoreCaseConverter()));
        translator.translate(new MethodInfo(methodName, Arrays.asList(param), null));

        assert DOMUtils.toString(translator.getDocument()).equals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n" +
                "<mapper namespace=\"demo\">\n" +
                "\n" +
                "<!--auto mapper generate-->\n" +
                "<insert id=\"insert\" keyProperty=\"id\" parameterType=\"demoDO\" useGeneratedKeys=\"true\">\n" +
                "    insert into demo (username, user_address) values\n" +
                "    (#{username}, #{userAddress})\n" +
                "</insert>\n" +
                "</mapper>"
        );
    }

    @Test
    public void testInsertAll() {
        String methodName = "insertAll";

        BeanField bf1 = new BeanField("id", "id", true);
        BeanField bf2 = new BeanField("username", "username", false);
        BeanField bf3 = new BeanField("userAddress", "user_address", false);

        Param param = new Param("demoDO", null, null, true, true, Arrays.asList(bf1, bf2, bf3));

        MySqlTranslator translator = new MySqlTranslator(new TranslateContext("demo", "demo", new LowerUnderScoreCaseConverter()));
        translator.translate(new MethodInfo(methodName, Arrays.asList(param), null));

        assert DOMUtils.toString(translator.getDocument()).equals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n" +
                "<mapper namespace=\"demo\">\n" +
                "\n" +
                "<!--auto mapper generate-->\n" +
                "<insert id=\"insertAll\" keyProperty=\"id\" parameterType=\"demoDO\" useGeneratedKeys=\"true\">\n" +
                "    insert into demo (username, user_address) values\n" +
                "    <foreach collection=\"collection\" item=\"item\" separator=\",\">\n" +
                "        (#{item.username}, #{item.userAddress})\n" +
                "    </foreach>\n" +
                "</insert>\n" +
                "</mapper>"
        );
    }

    @Test
    public void testDeleteAll() {
        String methodName = "deleteAll";

        MySqlTranslator translator = new MySqlTranslator(new TranslateContext("demo", "demo", new LowerUnderScoreCaseConverter()));
        translator.translate(new MethodInfo(methodName, null, null));

        assert DOMUtils.toString(translator.getDocument()).equals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n" +
                "<mapper namespace=\"demo\">\n" +
                "\n" +
                "<!--auto mapper generate-->\n" +
                "<delete id=\"deleteAll\">\n" +
                "    delete from demo\n" +
                "</delete>\n" +
                "</mapper>"
        );
    }

    @Test
    public void testDeleteBy1() {
        String methodName = "deleteByUsernameAndUserAddress";

        Param p1 = new Param("java.lang.String", "username", "username", false, false, null);
        Param p2 = new Param("java.lang.String", "userAddress", "user_address", false, false, null);

        MySqlTranslator translator = new MySqlTranslator(new TranslateContext("demo", "demo", new LowerUnderScoreCaseConverter()));
        translator.translate(new MethodInfo(methodName, Arrays.asList(p1, p2), null));

        assert DOMUtils.toString(translator.getDocument()).equals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n" +
                "<mapper namespace=\"demo\">\n" +
                "\n" +
                "<!--auto mapper generate-->\n" +
                "<delete id=\"deleteByUsernameAndUserAddress\">\n" +
                "    delete from demo\n" +
                "    where username=#{username}\n" +
                "    and user_address=#{userAddress}\n" +
                "</delete>\n" +
                "</mapper>"
        );
    }

    @Test
    public void testUpdateBy1() {
        String methodName = "updateByUsernameAndUserAddress";

        BeanField bf1 = new BeanField("id", "id", true);
        BeanField bf2 = new BeanField("username", "username", false);
        BeanField bf3 = new BeanField("userAddress", "user_address", false);

        Param p = new Param("DemoDO", null, null, false, true, Arrays.asList(bf1, bf2, bf3));

        MySqlTranslator translator = new MySqlTranslator(new TranslateContext("demo", "demo", new LowerUnderScoreCaseConverter()));
        translator.translate(new MethodInfo(methodName, Arrays.asList(p), null));

        assert DOMUtils.toString(translator.getDocument()).equals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n" +
                "<mapper namespace=\"demo\">\n" +
                "\n" +
                "<!--auto mapper generate-->\n" +
                "<update id=\"updateByUsernameAndUserAddress\" parameterType=\"DemoDO\">\n" +
                "    update demo set id=#{id}, username=#{username}, user_address=#{userAddress}\n" +
                "    where username=#{username}\n" +
                "    and user_address=#{userAddress}\n" +
                "</update>\n" +
                "</mapper>"
        );
    }

    @Test
    public void testFindAll() {
        String methodName = "findAll";

        BeanField bf1 = new BeanField("id", "id", true);
        BeanField bf2 = new BeanField("username", "username", false);
        BeanField bf3 = new BeanField("userAddress", "user_address", false);

        Return ret = new Return("DemoDO", true, Arrays.asList(bf1, bf2, bf3));

        MySqlTranslator translator = new MySqlTranslator(new TranslateContext("demo", "demo", new LowerUnderScoreCaseConverter()));
        translator.translate(new MethodInfo(methodName, null, ret));

        assert DOMUtils.toString(translator.getDocument()).equals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n" +
                "<mapper namespace=\"demo\">\n" +
                "\n" +
                "<!--auto mapper generate-->\n" +
                "<select id=\"findAll\" resultType=\"DemoDO\">\n" +
                "    select id, username, user_address as userAddress\n" +
                "    from demo\n" +
                "</select>\n" +
                "</mapper>"
        );
    }

    @Test
    public void testFindAllOrderBy() {
        String methodName = "findAllOrderById";

        BeanField bf1 = new BeanField("id", "id", true);
        BeanField bf2 = new BeanField("username", "username", false);
        BeanField bf3 = new BeanField("userAddress", "user_address", false);

        Return ret = new Return("DemoDO", true, Arrays.asList(bf1, bf2, bf3));

        MySqlTranslator translator = new MySqlTranslator(new TranslateContext("demo", "demo", new LowerUnderScoreCaseConverter()));
        translator.translate(new MethodInfo(methodName, null, ret));

        assert DOMUtils.toString(translator.getDocument()).equals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n" +
                "<mapper namespace=\"demo\">\n" +
                "\n" +
                "<!--auto mapper generate-->\n" +
                "<select id=\"findAllOrderById\" resultType=\"DemoDO\">\n" +
                "    select id, username, user_address as userAddress\n" +
                "    from demo\n" +
                "    order by id\n" +
                "</select>\n" +
                "</mapper>"
        );
    }

    @Test
    public void testFindBy1() {
        String methodName = "findByUsernameAndUserAddress";

        Param p1 = new Param("java.lang.String", "username", "username", false, false, null);
        Param p2 = new Param("java.lang.String", "userAddress", "user_address", false, false, null);

        BeanField bf1 = new BeanField("id", "id", true);
        BeanField bf2 = new BeanField("username", "username", false);
        BeanField bf3 = new BeanField("userAddress", "user_address", false);

        Return ret = new Return("DemoDO", true, Arrays.asList(bf1, bf2, bf3));

        MySqlTranslator translator = new MySqlTranslator(new TranslateContext("demo", "demo", new LowerUnderScoreCaseConverter()));
        translator.translate(new MethodInfo(methodName, Arrays.asList(p1, p2), ret));

        assert DOMUtils.toString(translator.getDocument()).equals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n" +
                "<mapper namespace=\"demo\">\n" +
                "\n" +
                "<!--auto mapper generate-->\n" +
                "<select id=\"findByUsernameAndUserAddress\" resultType=\"DemoDO\">\n" +
                "    select id, username, user_address as userAddress\n" +
                "    from demo\n" +
                "    where username=#{username}\n" +
                "    and user_address=#{userAddress}\n" +
                "</select>\n" +
                "</mapper>"
        );
    }

    @Test
    public void testFindByOrderBy1() {
        String methodName = "findByUsernameAndUserAddressOrderByUsernameAndUserAddress";

        Param p1 = new Param("java.lang.String", "username", "username", false, false, null);
        Param p2 = new Param("java.lang.String", "userAddress", "user_address", false, false, null);

        BeanField bf1 = new BeanField("id", "id", true);
        BeanField bf2 = new BeanField("username", "username", false);
        BeanField bf3 = new BeanField("userAddress", "user_address", false);

        Return ret = new Return("DemoDO", true, Arrays.asList(bf1, bf2, bf3));

        MySqlTranslator translator = new MySqlTranslator(new TranslateContext("demo", "demo", new LowerUnderScoreCaseConverter()));
        translator.translate(new MethodInfo(methodName, Arrays.asList(p1, p2), ret));

        assert DOMUtils.toString(translator.getDocument()).equals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n" +
                "<mapper namespace=\"demo\">\n" +
                "\n" +
                "<!--auto mapper generate-->\n" +
                "<select id=\"findByUsernameAndUserAddressOrderByUsernameAndUserAddress\" resultType=\"DemoDO\">\n" +
                "    select id, username, user_address as userAddress\n" +
                "    from demo\n" +
                "    where username=#{username}\n" +
                "    and user_address=#{userAddress}\n" +
                "    order by username, user_address\n" +
                "</select>\n" +
                "</mapper>"
        );
    }

    @Test
    public void testCountAll() {
        String methodName = "countAll";

        Return ret = new Return("int", false, null);

        MySqlTranslator translator = new MySqlTranslator(new TranslateContext("demo", "demo", new LowerUnderScoreCaseConverter()));
        translator.translate(new MethodInfo(methodName, null, ret));

        assert DOMUtils.toString(translator.getDocument()).equals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n" +
                "<mapper namespace=\"demo\">\n" +
                "\n" +
                "<!--auto mapper generate-->\n" +
                "<select id=\"countAll\" resultType=\"int\">\n" +
                "    select count(*)\n" +
                "    from demo\n" +
                "</select>\n" +
                "</mapper>"
        );
    }

    @Test
    public void testCountBy1() {
        String methodName = "countByUsernameAndUserAddress";

        Param p1 = new Param("java.lang.String", "username", "username", false, false, null);
        Param p2 = new Param("java.lang.String", "userAddress", "user_address", false, false, null);

        Return ret = new Return("int", false, null);

        MySqlTranslator translator = new MySqlTranslator(new TranslateContext("demo", "demo", new LowerUnderScoreCaseConverter()));
        translator.translate(new MethodInfo(methodName, Arrays.asList(p1, p2), ret));

        assert DOMUtils.toString(translator.getDocument()).equals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n" +
                "<mapper namespace=\"demo\">\n" +
                "\n" +
                "<!--auto mapper generate-->\n" +
                "<select id=\"countByUsernameAndUserAddress\" resultType=\"int\">\n" +
                "    select count(*)\n" +
                "    from demo\n" +
                "    where username=#{username}\n" +
                "    and user_address=#{userAddress}\n" +
                "</select>\n" +
                "</mapper>"
        );
    }

    @Test
    public void testCountBy2() {
        String methodName = "countByUsernameIsAndUserAddressEquals";

        Param p1 = new Param("java.lang.String", "username", "username", false, false, null);
        Param p2 = new Param("java.lang.String", "userAddress", "user_address", false, false, null);

        Return ret = new Return("int", false, null);

        MySqlTranslator translator = new MySqlTranslator(new TranslateContext("demo", "demo", new LowerUnderScoreCaseConverter()));
        translator.translate(new MethodInfo(methodName, Arrays.asList(p1, p2), ret));

        assert DOMUtils.toString(translator.getDocument()).equals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n" +
                "<mapper namespace=\"demo\">\n" +
                "\n" +
                "<!--auto mapper generate-->\n" +
                "<select id=\"countByUsernameIsAndUserAddressEquals\" resultType=\"int\">\n" +
                "    select count(*)\n" +
                "    from demo\n" +
                "    where username=#{username}\n" +
                "    and user_address=#{userAddress}\n" +
                "</select>\n" +
                "</mapper>"
        );
    }

    @Test
    public void testCountBy3() {
        String methodName = "countByUsernameLessThanAndUserAddressLessThanEquals";

        Param p1 = new Param("java.lang.String", "username", "username", false, false, null);
        Param p2 = new Param("java.lang.String", "userAddress", "user_address", false, false, null);

        Return ret = new Return("int", false, null);

        MySqlTranslator translator = new MySqlTranslator(new TranslateContext("demo", "demo", new LowerUnderScoreCaseConverter()));
        translator.translate(new MethodInfo(methodName, Arrays.asList(p1, p2), ret));

        assert DOMUtils.toString(translator.getDocument()).equals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n" +
                "<mapper namespace=\"demo\">\n" +
                "\n" +
                "<!--auto mapper generate-->\n" +
                "<select id=\"countByUsernameLessThanAndUserAddressLessThanEquals\" resultType=\"int\">\n" +
                "    select count(*)\n" +
                "    from demo\n" +
                "    where username&lt;#{username}\n" +
                "    and user_address&lt;=#{userAddress}\n" +
                "</select>\n" +
                "</mapper>"
        );
    }

    @Test
    public void testCountBy4() {
        String methodName = "countByUsernameGreaterThanAndUserAddressGreaterThanEquals";

        Param p1 = new Param("java.lang.String", "username", "username", false, false, null);
        Param p2 = new Param("java.lang.String", "userAddress", "user_address", false, false, null);

        Return ret = new Return("int", false, null);

        MySqlTranslator translator = new MySqlTranslator(new TranslateContext("demo", "demo", new LowerUnderScoreCaseConverter()));
        translator.translate(new MethodInfo(methodName, Arrays.asList(p1, p2), ret));

        assert DOMUtils.toString(translator.getDocument()).equals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n" +
                "<mapper namespace=\"demo\">\n" +
                "\n" +
                "<!--auto mapper generate-->\n" +
                "<select id=\"countByUsernameGreaterThanAndUserAddressGreaterThanEquals\" resultType=\"int\">\n" +
                "    select count(*)\n" +
                "    from demo\n" +
                "    where username&gt;#{username}\n" +
                "    and user_address&gt;=#{userAddress}\n" +
                "</select>\n" +
                "</mapper>"
        );
    }

    @Test
    public void testCountBy5() {
        String methodName = "countByUsernameAfterAndUserAddressBefore";

        Param p1 = new Param("java.lang.String", "username", "username", false, false, null);
        Param p2 = new Param("java.lang.String", "userAddress", "user_address", false, false, null);

        Return ret = new Return("int", false, null);

        MySqlTranslator translator = new MySqlTranslator(new TranslateContext("demo", "demo", new LowerUnderScoreCaseConverter()));
        translator.translate(new MethodInfo(methodName, Arrays.asList(p1, p2), ret));

        assert DOMUtils.toString(translator.getDocument()).equals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n" +
                "<mapper namespace=\"demo\">\n" +
                "\n" +
                "<!--auto mapper generate-->\n" +
                "<select id=\"countByUsernameAfterAndUserAddressBefore\" resultType=\"int\">\n" +
                "    select count(*)\n" +
                "    from demo\n" +
                "    where username&gt;#{username}\n" +
                "    and user_address&lt;#{userAddress}\n" +
                "</select>\n" +
                "</mapper>"
        );

    }

    @Test
    public void testCountBy6() {
        String methodName = "countByUsernameIsNullAndUserAddressIsNotNull";

        Param p1 = new Param("java.lang.String", "username", "username", false, false, null);
        Param p2 = new Param("java.lang.String", "userAddress", "user_address", false, false, null);

        Return ret = new Return("int", false, null);

        MySqlTranslator translator = new MySqlTranslator(new TranslateContext("demo", "demo", new LowerUnderScoreCaseConverter()));
        translator.translate(new MethodInfo(methodName, Arrays.asList(p1, p2), ret));

        assert DOMUtils.toString(translator.getDocument()).equals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n" +
                "<mapper namespace=\"demo\">\n" +
                "\n" +
                "<!--auto mapper generate-->\n" +
                "<select id=\"countByUsernameIsNullAndUserAddressIsNotNull\" resultType=\"int\">\n" +
                "    select count(*)\n" +
                "    from demo\n" +
                "    where username is null\n" +
                "    and user_address is not null\n" +
                "</select>\n" +
                "</mapper>"
        );
    }

    @Test
    public void testCountBy7() {
        String methodName = "countByUsernameNotNullAndUserAddressLike";

        Param p1 = new Param("java.lang.String", "username", "username", false, false, null);
        Param p2 = new Param("java.lang.String", "userAddress", "user_address", false, false, null);

        Return ret = new Return("int", false, null);

        MySqlTranslator translator = new MySqlTranslator(new TranslateContext("demo", "demo", new LowerUnderScoreCaseConverter()));
        translator.translate(new MethodInfo(methodName, Arrays.asList(p1, p2), ret));

        assert DOMUtils.toString(translator.getDocument()).equals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n" +
                "<mapper namespace=\"demo\">\n" +
                "\n" +
                "<!--auto mapper generate-->\n" +
                "<select id=\"countByUsernameNotNullAndUserAddressLike\" resultType=\"int\">\n" +
                "    select count(*)\n" +
                "    from demo\n" +
                "    where username is not null\n" +
                "    and user_address like #{userAddress}\n" +
                "</select>\n" +
                "</mapper>"
        );
    }

    @Test
    public void testCountBy8() {
        String methodName = "countByUsernameNotLikeAndUserAddressStartingWith";

        Param p1 = new Param("java.lang.String", "username", "username", false, false, null);
        Param p2 = new Param("java.lang.String", "userAddress", "user_address", false, false, null);

        Return ret = new Return("int", false, null);

        MySqlTranslator translator = new MySqlTranslator(new TranslateContext("demo", "demo", new LowerUnderScoreCaseConverter()));
        translator.translate(new MethodInfo(methodName, Arrays.asList(p1, p2), ret));

        assert DOMUtils.toString(translator.getDocument()).equals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n" +
                "<mapper namespace=\"demo\">\n" +
                "\n" +
                "<!--auto mapper generate-->\n" +
                "<select id=\"countByUsernameNotLikeAndUserAddressStartingWith\" resultType=\"int\">\n" +
                "    select count(*)\n" +
                "    from demo\n" +
                "    where username not like #{username}\n" +
                "    and user_address like concat(#{userAddress}, '%')\n" +
                "</select>\n" +
                "</mapper>"
        );
    }

    @Test
    public void testCountBy9() {
        String methodName = "countByUsernameEndingWithAndUserAddressContaining";

        Param p1 = new Param("java.lang.String", "username", "username", false, false, null);
        Param p2 = new Param("java.lang.String", "userAddress", "user_address", false, false, null);

        Return ret = new Return("int", false, null);

        MySqlTranslator translator = new MySqlTranslator(new TranslateContext("demo", "demo", new LowerUnderScoreCaseConverter()));
        translator.translate(new MethodInfo(methodName, Arrays.asList(p1, p2), ret));

        assert DOMUtils.toString(translator.getDocument()).equals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n" +
                "<mapper namespace=\"demo\">\n" +
                "\n" +
                "<!--auto mapper generate-->\n" +
                "<select id=\"countByUsernameEndingWithAndUserAddressContaining\" resultType=\"int\">\n" +
                "    select count(*)\n" +
                "    from demo\n" +
                "    where username like concat('%', #{username})\n" +
                "    and user_address like concat('%', #{userAddress}, '%')\n" +
                "</select>\n" +
                "</mapper>"
        );
    }

    @Test
    public void testCountBy10() {
        String methodName = "countByUsernameNotAndUserAddressIn";

        Param p1 = new Param("java.lang.String", "username", "username", false, false, null);
        Param p2 = new Param("java.lang.String", "userAddress", "user_address", false, false, null);

        Return ret = new Return("int", false, null);

        MySqlTranslator translator = new MySqlTranslator(new TranslateContext("demo", "demo", new LowerUnderScoreCaseConverter()));
        translator.translate(new MethodInfo(methodName, Arrays.asList(p1, p2), ret));

        assert DOMUtils.toString(translator.getDocument()).equals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n" +
                "<mapper namespace=\"demo\">\n" +
                "\n" +
                "<!--auto mapper generate-->\n" +
                "<select id=\"countByUsernameNotAndUserAddressIn\" resultType=\"int\">\n" +
                "    select count(*)\n" +
                "    from demo\n" +
                "    where username != #{username}\n" +
                "    and user_address in\n" +
                "    <foreach close=\")\" collection=\"userAddress\" item=\"item\" open=\"(\" separator=\",\">\n" +
                "        #{item}\n" +
                "    </foreach>\n" +
                "</select>\n" +
                "</mapper>"
        );
    }

    @Test
    public void testCountBy11() {
        String methodName = "countByUsernameInAndUserAddressNotIn";

        Param p1 = new Param("java.lang.String", "username", "username", false, false, null);
        Param p2 = new Param("java.lang.String", "userAddress", "user_address", false, false, null);

        Return ret = new Return("int", false, null);

        MySqlTranslator translator = new MySqlTranslator(new TranslateContext("demo", "demo", new LowerUnderScoreCaseConverter()));
        translator.translate(new MethodInfo(methodName, Arrays.asList(p1, p2), ret));

        assert DOMUtils.toString(translator.getDocument()).equals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n" +
                "<mapper namespace=\"demo\">\n" +
                "\n" +
                "<!--auto mapper generate-->\n" +
                "<select id=\"countByUsernameInAndUserAddressNotIn\" resultType=\"int\">\n" +
                "    select count(*)\n" +
                "    from demo\n" +
                "    where username in\n" +
                "    <foreach close=\")\" collection=\"username\" item=\"item\" open=\"(\" separator=\",\">\n" +
                "        #{item}\n" +
                "    </foreach>\n" +
                "    and user_address not in\n" +
                "    <foreach close=\")\" collection=\"userAddress\" item=\"item\" open=\"(\" separator=\",\">\n" +
                "        #{item}\n" +
                "    </foreach>\n" +
                "</select>\n" +
                "</mapper>"
        );
    }

    @Test
    public void testPageAll1() {
        String methodName = "pageAll";

        Param p1 = new Param("int", "offset", "offset", false, false, null);
        Param p2 = new Param("int", "limit", "limit", false, false, null);

        BeanField bf1 = new BeanField("id", "id", true);
        BeanField bf2 = new BeanField("username", "username", false);
        BeanField bf3 = new BeanField("userAddress", "user_address", false);

        Return ret = new Return("DemoDO", true, Arrays.asList(bf1, bf2, bf3));

        MySqlTranslator translator = new MySqlTranslator(new TranslateContext("demo", "demo", new LowerUnderScoreCaseConverter()));
        translator.translate(new MethodInfo(methodName, Arrays.asList(p1, p2), ret));

        assert DOMUtils.toString(translator.getDocument()).equals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n" +
                "<mapper namespace=\"demo\">\n" +
                "\n" +
                "<!--auto mapper generate-->\n" +
                "<select id=\"pageAll\" resultType=\"DemoDO\">\n" +
                "    select id, username, user_address as userAddress\n" +
                "    from demo\n" +
                "    limit #{offset},#{limit}\n" +
                "</select>\n" +
                "</mapper>"
        );
    }

    @Test
    public void testPageAll2() {
        String methodName = "pageAll";

        Param p1 = new Param("int", "limit", "limit", false, false, null);

        BeanField bf1 = new BeanField("id", "id", true);
        BeanField bf2 = new BeanField("username", "username", false);
        BeanField bf3 = new BeanField("userAddress", "user_address", false);

        Return ret = new Return("DemoDO", true, Arrays.asList(bf1, bf2, bf3));

        MySqlTranslator translator = new MySqlTranslator(new TranslateContext("demo", "demo", new LowerUnderScoreCaseConverter()));
        translator.translate(new MethodInfo(methodName, Arrays.asList(p1), ret));

        assert DOMUtils.toString(translator.getDocument()).equals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n" +
                "<mapper namespace=\"demo\">\n" +
                "\n" +
                "<!--auto mapper generate-->\n" +
                "<select id=\"pageAll\" parameterType=\"int\" resultType=\"DemoDO\">\n" +
                "    select id, username, user_address as userAddress\n" +
                "    from demo\n" +
                "    limit #{limit}\n" +
                "</select>\n" +
                "</mapper>"
        );
    }

    @Test
    public void testPageAllOrderBy() {
        String methodName = "pageAllOrderById";

        Param p1 = new Param("int", "offset", "offset", false, false, null);
        Param p2 = new Param("int", "limit", "limit", false, false, null);

        BeanField bf1 = new BeanField("id", "id", true);
        BeanField bf2 = new BeanField("username", "username", false);
        BeanField bf3 = new BeanField("userAddress", "user_address", false);

        Return ret = new Return("DemoDO", true, Arrays.asList(bf1, bf2, bf3));

        MySqlTranslator translator = new MySqlTranslator(new TranslateContext("demo", "demo", new LowerUnderScoreCaseConverter()));
        translator.translate(new MethodInfo(methodName, Arrays.asList(p1, p2), ret));

        assert DOMUtils.toString(translator.getDocument()).equals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n" +
                "<mapper namespace=\"demo\">\n" +
                "\n" +
                "<!--auto mapper generate-->\n" +
                "<select id=\"pageAllOrderById\" resultType=\"DemoDO\">\n" +
                "    select id, username, user_address as userAddress\n" +
                "    from demo\n" +
                "    order by id\n" +
                "    limit #{offset},#{limit}\n" +
                "</select>\n" +
                "</mapper>"
        );
    }

    @Test
    public void testPageBy1() {
        String methodName = "pageByUsernameAndUserAddress";

        Param p1 = new Param("java.lang.String", "username", "username", false, false, null);
        Param p2 = new Param("java.lang.String", "userAddress", "user_address", false, false, null);
        Param p3 = new Param("int", "offset", "offset", false, false, null);
        Param p4 = new Param("int", "limit", "limit", false, false, null);

        BeanField bf1 = new BeanField("id", "id", true);
        BeanField bf2 = new BeanField("username", "username", false);
        BeanField bf3 = new BeanField("userAddress", "user_address", false);

        Return ret = new Return("DemoDO", true, Arrays.asList(bf1, bf2, bf3));

        MySqlTranslator translator = new MySqlTranslator(new TranslateContext("demo", "demo", new LowerUnderScoreCaseConverter()));
        translator.translate(new MethodInfo(methodName, Arrays.asList(p1, p2, p3, p4), ret));

        assert DOMUtils.toString(translator.getDocument()).equals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n" +
                "<mapper namespace=\"demo\">\n" +
                "\n" +
                "<!--auto mapper generate-->\n" +
                "<select id=\"pageByUsernameAndUserAddress\" resultType=\"DemoDO\">\n" +
                "    select id, username, user_address as userAddress\n" +
                "    from demo\n" +
                "    where username=#{username}\n" +
                "    and user_address=#{userAddress}\n" +
                "    limit #{offset},#{limit}\n" +
                "</select>\n" +
                "</mapper>"
        );
    }

    @Test
    public void testPageByOrderBy1() {
        String methodName = "pageByUsernameAndUserAddressOrderByUsernameAndUserAddress";

        Param p1 = new Param("java.lang.String", "username", "username", false, false, null);
        Param p2 = new Param("java.lang.String", "userAddress", "user_address", false, false, null);
        Param p3 = new Param("int", "offset", "offset", false, false, null);
        Param p4 = new Param("int", "limit", "limit", false, false, null);

        BeanField bf1 = new BeanField("id", "id", true);
        BeanField bf2 = new BeanField("username", "username", false);
        BeanField bf3 = new BeanField("userAddress", "user_address", false);

        Return ret = new Return("DemoDO", true, Arrays.asList(bf1, bf2, bf3));

        MySqlTranslator translator = new MySqlTranslator(new TranslateContext("demo", "demo", new LowerUnderScoreCaseConverter()));
        translator.translate(new MethodInfo(methodName, Arrays.asList(p1, p2, p3, p4), ret));

        assert DOMUtils.toString(translator.getDocument()).equals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n" +
                "<mapper namespace=\"demo\">\n" +
                "\n" +
                "<!--auto mapper generate-->\n" +
                "<select id=\"pageByUsernameAndUserAddressOrderByUsernameAndUserAddress\" resultType=\"DemoDO\">\n" +
                "    select id, username, user_address as userAddress\n" +
                "    from demo\n" +
                "    where username=#{username}\n" +
                "    and user_address=#{userAddress}\n" +
                "    order by username, user_address\n" +
                "    limit #{offset},#{limit}\n" +
                "</select>\n" +
                "</mapper>"
        );
    }

}
