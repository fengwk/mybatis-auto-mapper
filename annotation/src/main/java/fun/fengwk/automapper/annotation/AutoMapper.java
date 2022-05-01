package fun.fengwk.automapper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解处理程序将自动为标记了{@link AutoMapper}的接口生成相应的xml文件。
 *
 * @author fengwk
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface AutoMapper {

    /**
     * 数据库类型，如果使用不兼容的数据库类型可能导致小部分翻译的sql语句产生问题。
     *
     * @return
     */
    DBType dbType() default DBType.MYSQL;

    /**
     * mapper接口类后缀，如果自动转换表名，将去除后缀再使用tableNamingStyle进行转换。
     * 例如mapperSuffix为Mapper，tableNamingStyle为LOWER_UNDER_SCORE_CASE，
     * 那么UserInfoMapper将被转换为表名user_info。
     *
     * @return
     */
    String mapperSuffix() default "Mapper";

    /**
     * 数据库表名命名风格，默认使用{@link NamingStyle#LOWER_UNDER_SCORE_CASE}，
     * 例如<code>UserInfoMapper</code>将转换为<code>user_info</code>。
     *
     * @return
     */
    NamingStyle tableNamingStyle() default NamingStyle.LOWER_UNDER_SCORE_CASE;

    /**
     * 数据库字段命名风格，默认使用{@link NamingStyle#LOWER_UNDER_SCORE_CASE}。
     *
     * @return
     */
    NamingStyle fieldNamingStyle() default NamingStyle.LOWER_UNDER_SCORE_CASE;

    /**
     * 指定表名，默认使用{@link #tableNamingStyle()}方式转换。
     *
     * @return
     */
    String tableName() default "";

    /**
     * 指定表名前缀，通常用于从逻辑上区分表的命名空间。
     *
     * @return
     */
    String tableNamePrefix() default "";

}
