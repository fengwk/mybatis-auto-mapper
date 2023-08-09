package fun.fengwk.automapper.processor.translator.mysql;

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
public class MySqlTranslatorTest {

    @Test
    public void testInsert() {
        String methodName = "insert";

        BeanField bf1 = new BeanField("id", "id", true, false);
        BeanField bf2 = new BeanField("username", "username", false, false);
        BeanField bf3 = new BeanField("userAddress", "user_address", false, false);

        Param param = new Param("demoDO", null, null, false, true, Arrays.asList(bf1, bf2, bf3), false, false);

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
    public void testInsertSelective() {
        String methodName = "insertSelective";

        BeanField bf1 = new BeanField("id", "id", true, false);
        BeanField bf2 = new BeanField("username", "username", false, false);
        BeanField bf3 = new BeanField("userAddress", "user_address", false, false);

        Param param = new Param("demoDO", null, null, false, true, Arrays.asList(bf1, bf2, bf3), false, false);

        MySqlTranslator translator = new MySqlTranslator(new TranslateContext("demo", "demo", new LowerUnderScoreCaseConverter()));
        translator.translate(new MethodInfo(methodName, Arrays.asList(param), null));

        assert DOMUtils.toString(translator.getDocument()).equals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n" +
                "<mapper namespace=\"demo\">\n" +
                "\n" +
                "<!--auto mapper generate-->\n" +
                "<insert id=\"insertSelective\" keyProperty=\"id\" parameterType=\"demoDO\" useGeneratedKeys=\"true\">\n" +
                "    insert into demo\n" +
                "    <trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n" +
                "        <if test=\"username != null\">username,</if>\n" +
                "        <if test=\"userAddress != null\">user_address,</if>\n" +
                "    </trim>\n" +
                "    values\n" +
                "    <trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n" +
                "        <if test=\"username != null\">#{username},</if>\n" +
                "        <if test=\"userAddress != null\">#{userAddress},</if>\n" +
                "    </trim>\n" +
                "</insert>\n" +
                "</mapper>"
        );
    }

    @Test
    public void testInsertIgnoreSelective() {
        String methodName = "insertIgnoreSelective";

        BeanField bf1 = new BeanField("id", "id", true, false);
        BeanField bf2 = new BeanField("username", "username", false, false);
        BeanField bf3 = new BeanField("userAddress", "user_address", false, false);

        Param param = new Param("demoDO", null, null, false, true, Arrays.asList(bf1, bf2, bf3), false, false);

        MySqlTranslator translator = new MySqlTranslator(new TranslateContext("demo", "demo", new LowerUnderScoreCaseConverter()));
        translator.translate(new MethodInfo(methodName, Arrays.asList(param), null));

        assert DOMUtils.toString(translator.getDocument()).equals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n" +
                "<mapper namespace=\"demo\">\n" +
                "\n" +
                "<!--auto mapper generate-->\n" +
                "<insert id=\"insertIgnoreSelective\" keyProperty=\"id\" parameterType=\"demoDO\" useGeneratedKeys=\"true\">\n" +
                "    insert ignore into demo\n" +
                "    <trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n" +
                "        <if test=\"username != null\">username,</if>\n" +
                "        <if test=\"userAddress != null\">user_address,</if>\n" +
                "    </trim>\n" +
                "    values\n" +
                "    <trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n" +
                "        <if test=\"username != null\">#{username},</if>\n" +
                "        <if test=\"userAddress != null\">#{userAddress},</if>\n" +
                "    </trim>\n" +
                "</insert>\n" +
                "</mapper>"
        );
    }

    @Test
    public void testInsertOnDuplicateKeyUpdate() {
        String methodName = "insertOnDuplicateKeyUpdate";

        BeanField bf1 = new BeanField("id", "id", true, false);
        BeanField bf2 = new BeanField("username", "username", false, false);
        BeanField bf3 = new BeanField("userAddress", "user_address", false, false);

        Param param = new Param("demoDO", null, null, false, true, Arrays.asList(bf1, bf2, bf3), false, false);

        MySqlTranslator translator = new MySqlTranslator(new TranslateContext("demo", "demo", new LowerUnderScoreCaseConverter()));
        translator.translate(new MethodInfo(methodName, Arrays.asList(param), null));
        assert DOMUtils.toString(translator.getDocument()).equals(
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
            "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n" +
            "<mapper namespace=\"demo\">\n" +
            "\n" +
            "<!--auto mapper generate-->\n" +
            "<insert id=\"insertOnDuplicateKeyUpdate\" keyProperty=\"id\" parameterType=\"demoDO\" useGeneratedKeys=\"true\">\n" +
            "    insert into demo (username, user_address) values\n" +
            "    (#{username}, #{userAddress})\n" +
            "    on duplicate key update username=#{username}, user_address=#{userAddress}\n" +
            "    </insert>\n" +
            "</mapper>"
        );
    }

    @Test
    public void testInsertOnDuplicateKeyUpdateSelective() {
        String methodName = "insertOnDuplicateKeyUpdateSelective";

        BeanField bf1 = new BeanField("id", "id", true, false);
        BeanField bf2 = new BeanField("username", "username", false, false);
        BeanField bf3 = new BeanField("userAddress", "user_address", false, false);

        Param param = new Param("demoDO", null, null, false, true, Arrays.asList(bf1, bf2, bf3), false, false);

        MySqlTranslator translator = new MySqlTranslator(new TranslateContext("demo", "demo", new LowerUnderScoreCaseConverter()));
        translator.translate(new MethodInfo(methodName, Arrays.asList(param), null));

        assert DOMUtils.toString(translator.getDocument()).equals(
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
            "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n" +
            "<mapper namespace=\"demo\">\n" +
            "\n" +
            "<!--auto mapper generate-->\n" +
            "<insert id=\"insertOnDuplicateKeyUpdateSelective\" keyProperty=\"id\" parameterType=\"demoDO\" useGeneratedKeys=\"true\">\n" +
            "    insert into demo\n" +
            "    <trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n" +
            "        <if test=\"username != null\">username,</if>\n" +
            "        <if test=\"userAddress != null\">user_address,</if>\n" +
            "    </trim>\n" +
            "    values\n" +
            "    <trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n" +
            "        <if test=\"username != null\">#{username},</if>\n" +
            "        <if test=\"userAddress != null\">#{userAddress},</if>\n" +
            "    </trim>\n" +
            "    <trim prefix=\"on duplicate key update \" suffixOverrides=\",\">\n" +
            "        <if test=\"username != null\">username=#{username},</if>\n" +
            "        <if test=\"userAddress != null\">user_address=#{userAddress},</if>\n" +
            "    </trim>\n" +
            "</insert>\n" +
            "</mapper>"
        );
    }

    @Test
    public void testReplaceSelective() {
        String methodName = "replaceSelective";

        BeanField bf1 = new BeanField("id", "id", true, false);
        BeanField bf2 = new BeanField("username", "username", false, false);
        BeanField bf3 = new BeanField("userAddress", "user_address", false, false);

        Param param = new Param("demoDO", null, null, false, true, Arrays.asList(bf1, bf2, bf3), false, false);

        MySqlTranslator translator = new MySqlTranslator(new TranslateContext("demo", "demo", new LowerUnderScoreCaseConverter()));
        translator.translate(new MethodInfo(methodName, Arrays.asList(param), null));

        assert DOMUtils.toString(translator.getDocument()).equals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n" +
                "<mapper namespace=\"demo\">\n" +
                "\n" +
                "<!--auto mapper generate-->\n" +
                "<insert id=\"replaceSelective\" keyProperty=\"id\" parameterType=\"demoDO\" useGeneratedKeys=\"true\">\n" +
                "    replace into demo\n" +
                "    <trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n" +
                "        <if test=\"username != null\">username,</if>\n" +
                "        <if test=\"userAddress != null\">user_address,</if>\n" +
                "    </trim>\n" +
                "    values\n" +
                "    <trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n" +
                "        <if test=\"username != null\">#{username},</if>\n" +
                "        <if test=\"userAddress != null\">#{userAddress},</if>\n" +
                "    </trim>\n" +
                "</insert>\n" +
                "</mapper>"
        );
    }

    @Test
    public void testInsertAll() {
        String methodName = "insertAll";

        BeanField bf1 = new BeanField("id", "id", true, false);
        BeanField bf2 = new BeanField("username", "username", false, false);
        BeanField bf3 = new BeanField("userAddress", "user_address", false, false);

        Param param = new Param("demoDO", null, null, true, true, Arrays.asList(bf1, bf2, bf3), false, false);

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
    public void testInsertAllSelective() {
        String methodName = "insertAllSelective";

        BeanField bf1 = new BeanField("id", "id", true, false);
        BeanField bf2 = new BeanField("username", "username", false, false);
        BeanField bf3 = new BeanField("userAddress", "user_address", false, false);

        Param param = new Param("demoDO", null, null, true, true, Arrays.asList(bf1, bf2, bf3), false, false);

        MySqlTranslator translator = new MySqlTranslator(new TranslateContext("demo", "demo", new LowerUnderScoreCaseConverter()));
        translator.translate(new MethodInfo(methodName, Arrays.asList(param), null));

        assert DOMUtils.toString(translator.getDocument()).equals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n" +
                "<mapper namespace=\"demo\">\n" +
                "\n" +
                "<!--auto mapper generate-->\n" +
                "<insert id=\"insertAllSelective\" keyProperty=\"id\" parameterType=\"demoDO\" useGeneratedKeys=\"true\">\n" +
                "    <foreach collection=\"collection\" item=\"item\" separator=\";\">\n" +
                "        insert into demo\n" +
                "        <trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n" +
                "            <if test=\"item.username != null\">username,</if>\n" +
                "            <if test=\"item.userAddress != null\">user_address,</if>\n" +
                "        </trim>\n" +
                "        values\n" +
                "        <trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n" +
                "            <if test=\"item.username != null\">#{item.username},</if>\n" +
                "            <if test=\"item.userAddress != null\">#{item.userAddress},</if>\n" +
                "        </trim>\n" +
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

        Param p1 = new Param("java.lang.String", "username", "username", false, false, null, false, false);
        Param p2 = new Param("java.lang.String", "userAddress", "user_address", false, false, null, false, false);

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
                "    <where>\n" +
                "        username=#{username}\n" +
                "        and user_address=#{userAddress}\n" +
                "    </where>\n" +
                "</delete>\n" +
                "</mapper>"
        );
    }

    @Test
    public void testDeleteBy2() {
        String methodName = "deleteByUsernameAndUserAddress";

        Param p1 = new Param("java.lang.String", "username", "username", false, false, null, true, false);
        Param p2 = new Param("java.lang.String", "userAddress", "user_address", false, false, null, true, false);

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
                "    <where>\n" +
                "        <if test=\"username != null\">\n" +
                "            username=#{username}\n" +
                "        </if>\n" +
                "        <if test=\"userAddress != null\">\n" +
                "            and user_address=#{userAddress}\n" +
                "        </if>\n" +
                "    </where>\n" +
                "</delete>\n" +
                "</mapper>"
        );
    }

    @Test
    public void testUpdateBy() {
        String methodName = "updateByUsernameAndUserAddress";

        BeanField bf1 = new BeanField("id", "id", true, false);
        BeanField bf2 = new BeanField("username", "username", false, false);
        BeanField bf3 = new BeanField("userAddress", "user_address", false, false);

        Param p = new Param("DemoDO", null, null, false, true, Arrays.asList(bf1, bf2, bf3), false, false);

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
                "    <where>\n" +
                "        username=#{username}\n" +
                "        and user_address=#{userAddress}\n" +
                "    </where>\n" +
                "</update>\n" +
                "</mapper>"
        );
    }

    @Test
    public void testUpdateBySelective() {
        String methodName = "updateByUsernameAndUserAddressSelective";

        BeanField bf1 = new BeanField("id", "id", true, false);
        BeanField bf2 = new BeanField("username", "username", false, false);
        BeanField bf3 = new BeanField("userAddress", "user_address", false, false);

        Param p = new Param("DemoDO", null, null, false, true, Arrays.asList(bf1, bf2, bf3), false, false);

        MySqlTranslator translator = new MySqlTranslator(new TranslateContext("demo", "demo", new LowerUnderScoreCaseConverter()));
        translator.translate(new MethodInfo(methodName, Arrays.asList(p), null));

        assert DOMUtils.toString(translator.getDocument()).equals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n" +
                "<mapper namespace=\"demo\">\n" +
                "\n" +
                "<!--auto mapper generate-->\n" +
                "<update id=\"updateByUsernameAndUserAddressSelective\" parameterType=\"DemoDO\">\n" +
                "    update demo set \n" +
                "    <trim suffixOverrides=\",\">\n" +
                "        <if test=\"id != null\">id=#{id},</if>\n" +
                "        <if test=\"username != null\">username=#{username},</if>\n" +
                "        <if test=\"userAddress != null\">user_address=#{userAddress},</if>\n" +
                "    </trim>\n" +
                "    <where>\n" +
                "        username=#{username}\n" +
                "        and user_address=#{userAddress}\n" +
                "    </where>\n" +
                "</update>\n" +
                "</mapper>"
        );
    }

    @Test
    public void testFindAll() {
        String methodName = "findAll";

        BeanField bf1 = new BeanField("id", "id", true, false);
        BeanField bf2 = new BeanField("username", "username", false, false);
        BeanField bf3 = new BeanField("userAddress", "user_address", false, false);

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

        BeanField bf1 = new BeanField("id", "id", true, false);
        BeanField bf2 = new BeanField("username", "username", false, false);
        BeanField bf3 = new BeanField("userAddress", "user_address", false, false);

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

        Param p1 = new Param("java.lang.String", "username", "username", false, false, null, false, false);
        Param p2 = new Param("java.lang.String", "userAddress", "user_address", false, false, null, false, false);

        BeanField bf1 = new BeanField("id", "id", true, false);
        BeanField bf2 = new BeanField("username", "username", false, false);
        BeanField bf3 = new BeanField("userAddress", "user_address", false, false);

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
                "    <where>\n" +
                "        username=#{username}\n" +
                "        and user_address=#{userAddress}\n" +
                "    </where>\n" +
                "</select>\n" +
                "</mapper>"
        );
    }

    @Test
    public void testFindLockInShareMode() {
        String methodName = "findLockInShareModeByUsernameAndUserAddress";

        Param p1 = new Param("java.lang.String", "username", "username", false, false, null, false, false);
        Param p2 = new Param("java.lang.String", "userAddress", "user_address", false, false, null, false, false);

        BeanField bf1 = new BeanField("id", "id", true, false);
        BeanField bf2 = new BeanField("username", "username", false, false);
        BeanField bf3 = new BeanField("userAddress", "user_address", false, false);

        Return ret = new Return("DemoDO", true, Arrays.asList(bf1, bf2, bf3));

        MySqlTranslator translator = new MySqlTranslator(new TranslateContext("demo", "demo", new LowerUnderScoreCaseConverter()));
        translator.translate(new MethodInfo(methodName, Arrays.asList(p1, p2), ret));

        assert DOMUtils.toString(translator.getDocument()).equals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n" +
                "<mapper namespace=\"demo\">\n" +
                "\n" +
                "<!--auto mapper generate-->\n" +
                "<select id=\"findLockInShareModeByUsernameAndUserAddress\" resultType=\"DemoDO\">\n" +
                "    select id, username, user_address as userAddress\n" +
                "    from demo\n" +
                "    <where>\n" +
                "        username=#{username}\n" +
                "        and user_address=#{userAddress}\n" +
                "    </where>\n" +
                "    lock in share mode\n" +
                "</select>\n" +
                "</mapper>"
        );
    }

    @Test
    public void testFindForUpdate() {
        String methodName = "findForUpdateByUsernameAndUserAddress";

        Param p1 = new Param("java.lang.String", "username", "username", false, false, null, false, false);
        Param p2 = new Param("java.lang.String", "userAddress", "user_address", false, false, null, false, false);

        BeanField bf1 = new BeanField("id", "id", true, false);
        BeanField bf2 = new BeanField("username", "username", false, false);
        BeanField bf3 = new BeanField("userAddress", "user_address", false, false);

        Return ret = new Return("DemoDO", true, Arrays.asList(bf1, bf2, bf3));

        MySqlTranslator translator = new MySqlTranslator(new TranslateContext("demo", "demo", new LowerUnderScoreCaseConverter()));
        translator.translate(new MethodInfo(methodName, Arrays.asList(p1, p2), ret));

        assert DOMUtils.toString(translator.getDocument()).equals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n" +
                "<mapper namespace=\"demo\">\n" +
                "\n" +
                "<!--auto mapper generate-->\n" +
                "<select id=\"findForUpdateByUsernameAndUserAddress\" resultType=\"DemoDO\">\n" +
                "    select id, username, user_address as userAddress\n" +
                "    from demo\n" +
                "    <where>\n" +
                "        username=#{username}\n" +
                "        and user_address=#{userAddress}\n" +
                "    </where>\n" +
                "    for update\n" +
                "</select>\n" +
                "</mapper>"
        );
    }

    @Test
    public void testFindByOrderBy1() {
        String methodName = "findByUsernameAndUserAddressOrderByUsernameAndUserAddress";

        Param p1 = new Param("java.lang.String", "username", "username", false, false, null, false, false);
        Param p2 = new Param("java.lang.String", "userAddress", "user_address", false, false, null, false, false);

        BeanField bf1 = new BeanField("id", "id", true, false);
        BeanField bf2 = new BeanField("username", "username", false, false);
        BeanField bf3 = new BeanField("userAddress", "user_address", false, false);

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
                "    <where>\n" +
                "        username=#{username}\n" +
                "        and user_address=#{userAddress}\n" +
                "    </where>\n" +
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

        Param p1 = new Param("java.lang.String", "username", "username", false, false, null, false, false);
        Param p2 = new Param("java.lang.String", "userAddress", "user_address", false, false, null, false, false);

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
                "    <where>\n" +
                "        username=#{username}\n" +
                "        and user_address=#{userAddress}\n" +
                "    </where>\n" +
                "</select>\n" +
                "</mapper>"
        );
    }

    @Test
    public void testCountBy2() {
        String methodName = "countByUsernameIsAndUserAddressEquals";

        Param p1 = new Param("java.lang.String", "username", "username", false, false, null, false, false);
        Param p2 = new Param("java.lang.String", "userAddress", "user_address", false, false, null, false, false);

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
                "    <where>\n" +
                "        username=#{username}\n" +
                "        and user_address=#{userAddress}\n" +
                "    </where>\n" +
                "</select>\n" +
                "</mapper>"
        );
    }

    @Test
    public void testCountBy3() {
        String methodName = "countByUsernameLessThanAndUserAddressLessThanEquals";

        Param p1 = new Param("java.lang.String", "username", "username", false, false, null, false, false);
        Param p2 = new Param("java.lang.String", "userAddress", "user_address", false, false, null, false, false);

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
                "    <where>\n" +
                "        username&lt;#{username}\n" +
                "        and user_address&lt;=#{userAddress}\n" +
                "    </where>\n" +
                "</select>\n" +
                "</mapper>"
        );
    }

    @Test
    public void testCountBy4() {
        String methodName = "countByUsernameGreaterThanAndUserAddressGreaterThanEquals";

        Param p1 = new Param("java.lang.String", "username", "username", false, false, null, false, false);
        Param p2 = new Param("java.lang.String", "userAddress", "user_address", false, false, null, false, false);

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
                "    <where>\n" +
                "        username&gt;#{username}\n" +
                "        and user_address&gt;=#{userAddress}\n" +
                "    </where>\n" +
                "</select>\n" +
                "</mapper>"
        );
    }

    @Test
    public void testCountBy5() {
        String methodName = "countByUsernameAfterAndUserAddressBefore";

        Param p1 = new Param("java.lang.String", "username", "username", false, false, null, false, false);
        Param p2 = new Param("java.lang.String", "userAddress", "user_address", false, false, null, false, false);

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
                "    <where>\n" +
                "        username&gt;#{username}\n" +
                "        and user_address&lt;#{userAddress}\n" +
                "    </where>\n" +
                "</select>\n" +
                "</mapper>"
        );

    }

    @Test
    public void testCountBy6() {
        String methodName = "countByUsernameIsNullAndUserAddressIsNotNull";

        Param p1 = new Param("java.lang.String", "username", "username", false, false, null, false, false);
        Param p2 = new Param("java.lang.String", "userAddress", "user_address", false, false, null, false, false);

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
                "    <where>\n" +
                "        username is null\n" +
                "        and user_address is not null\n" +
                "    </where>\n" +
                "</select>\n" +
                "</mapper>"
        );
    }

    @Test
    public void testCountBy7() {
        String methodName = "countByUsernameNotNullAndUserAddressLike";

        Param p1 = new Param("java.lang.String", "username", "username", false, false, null, false, false);
        Param p2 = new Param("java.lang.String", "userAddress", "user_address", false, false, null, false, false);

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
                "    <where>\n" +
                "        username is not null\n" +
                "        and user_address like #{userAddress}\n" +
                "    </where>\n" +
                "</select>\n" +
                "</mapper>"
        );
    }

    @Test
    public void testCountBy8() {
        String methodName = "countByUsernameNotLikeAndUserAddressStartingWith";

        Param p1 = new Param("java.lang.String", "username", "username", false, false, null, false, false);
        Param p2 = new Param("java.lang.String", "userAddress", "user_address", false, false, null, false, false);

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
                "    <where>\n" +
                "        username not like #{username}\n" +
                "        and user_address like concat(#{userAddress}, '%')\n" +
                "    </where>\n" +
                "</select>\n" +
                "</mapper>"
        );
    }

    @Test
    public void testCountBy9() {
        String methodName = "countByUsernameEndingWithAndUserAddressContaining";

        Param p1 = new Param("java.lang.String", "username", "username", false, false, null, false, false);
        Param p2 = new Param("java.lang.String", "userAddress", "user_address", false, false, null, false, false);

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
                "    <where>\n" +
                "        username like concat('%', #{username})\n" +
                "        and user_address like concat('%', #{userAddress}, '%')\n" +
                "    </where>\n" +
                "</select>\n" +
                "</mapper>"
        );
    }

    @Test
    public void testCountBy10() {
        String methodName = "countByUsernameNotAndUserAddressIn";

        Param p1 = new Param("java.lang.String", "username", "username", false, false, null, false, false);
        Param p2 = new Param("java.lang.String", "userAddress", "user_address", false, false, null, false, false);

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
                "    <where>\n" +
                "        username != #{username}\n" +
                "        and user_address in\n" +
                "        <foreach close=\")\" collection=\"userAddress\" item=\"item\" open=\"(\" separator=\",\">\n" +
                "            #{item}\n" +
                "        </foreach>\n" +
                "    </where>\n" +
                "</select>\n" +
                "</mapper>"
        );
    }

    @Test
    public void testCountBy11() {
        String methodName = "countByUsernameInAndUserAddressNotIn";

        Param p1 = new Param("java.lang.String", "username", "username", false, false, null, false, false);
        Param p2 = new Param("java.lang.String", "userAddress", "user_address", false, false, null, false, false);

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
                "    <where>\n" +
                "        username in\n" +
                "        <foreach close=\")\" collection=\"username\" item=\"item\" open=\"(\" separator=\",\">\n" +
                "            #{item}\n" +
                "        </foreach>\n" +
                "        and user_address not in\n" +
                "        <foreach close=\")\" collection=\"userAddress\" item=\"item\" open=\"(\" separator=\",\">\n" +
                "            #{item}\n" +
                "        </foreach>\n" +
                "    </where>\n" +
                "</select>\n" +
                "</mapper>"
        );
    }

    @Test
    public void testCountBy12() {
        String methodName = "countByUsernameInAndUserAddressNotIn";

        Param p1 = new Param("java.lang.String", "username", "username", false, false, null, true, false);
        Param p2 = new Param("java.lang.String", "userAddress", "user_address", false, false, null, true, false);

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
                "    <where>\n" +
                "        <if test=\"username != null\">\n" +
                "            username in\n" +
                "            <foreach close=\")\" collection=\"username\" item=\"item\" open=\"(\" separator=\",\">\n" +
                "                #{item}\n" +
                "            </foreach>\n" +
                "        </if>\n" +
                "        <if test=\"userAddress != null\">\n" +
                "            and user_address not in\n" +
                "            <foreach close=\")\" collection=\"userAddress\" item=\"item\" open=\"(\" separator=\",\">\n" +
                "                #{item}\n" +
                "            </foreach>\n" +
                "        </if>\n" +
                "    </where>\n" +
                "</select>\n" +
                "</mapper>"
        );
    }

    @Test
    public void testPageAll1() {
        String methodName = "pageAll";

        Param p1 = new Param("int", "offset", "offset", false, false, null, false, false);
        Param p2 = new Param("int", "limit", "limit", false, false, null, false, false);

        BeanField bf1 = new BeanField("id", "id", true, false);
        BeanField bf2 = new BeanField("username", "username", false, false);
        BeanField bf3 = new BeanField("userAddress", "user_address", false, false);

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

        Param p1 = new Param("int", "limit", "limit", false, false, null, false, false);

        BeanField bf1 = new BeanField("id", "id", true, false);
        BeanField bf2 = new BeanField("username", "username", false, false);
        BeanField bf3 = new BeanField("userAddress", "user_address", false, false);

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

        Param p1 = new Param("int", "offset", "offset", false, false, null, false, false);
        Param p2 = new Param("int", "limit", "limit", false, false, null, false, false);

        BeanField bf1 = new BeanField("id", "id", true, false);
        BeanField bf2 = new BeanField("username", "username", false, false);
        BeanField bf3 = new BeanField("userAddress", "user_address", false, false);

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

        Param p1 = new Param("java.lang.String", "username", "username", false, false, null, false, false);
        Param p2 = new Param("java.lang.String", "userAddress", "user_address", false, false, null, false, false);
        Param p3 = new Param("int", "offset", "offset", false, false, null, false, false);
        Param p4 = new Param("int", "limit", "limit", false, false, null, false, false);

        BeanField bf1 = new BeanField("id", "id", true, false);
        BeanField bf2 = new BeanField("username", "username", false, false);
        BeanField bf3 = new BeanField("userAddress", "user_address", false, false);

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
                "    <where>\n" +
                "        username=#{username}\n" +
                "        and user_address=#{userAddress}\n" +
                "    </where>\n" +
                "    limit #{offset},#{limit}\n" +
                "</select>\n" +
                "</mapper>"
        );
    }

    @Test
    public void testPageByOrderBy1() {
        String methodName = "pageByUsernameAndUserAddressOrderByUsernameAndUserAddress";

        Param p1 = new Param("java.lang.String", "username", "username", false, false, null, false, false);
        Param p2 = new Param("java.lang.String", "userAddress", "user_address", false, false, null, false, false);
        Param p3 = new Param("int", "offset", "offset", false, false, null, false, false);
        Param p4 = new Param("int", "limit", "limit", false, false, null, false, false);

        BeanField bf1 = new BeanField("id", "id", true, false);
        BeanField bf2 = new BeanField("username", "username", false, false);
        BeanField bf3 = new BeanField("userAddress", "user_address", false, false);

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
                "    <where>\n" +
                "        username=#{username}\n" +
                "        and user_address=#{userAddress}\n" +
                "    </where>\n" +
                "    order by username, user_address\n" +
                "    limit #{offset},#{limit}\n" +
                "</select>\n" +
                "</mapper>"
        );
    }

}
