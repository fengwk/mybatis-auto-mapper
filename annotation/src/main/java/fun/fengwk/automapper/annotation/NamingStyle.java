package fun.fengwk.automapper.annotation;

/**
 * {@link NamingStyle}定义了数据库的命名风格。
 *
 * @author fengwk
 */
public enum NamingStyle {

    /**
     * 小驼峰命名，例如：<code>autoMapper</code>。
     */
    LOWER_CAMEL_CASE,

    /**
     * 大驼峰命名，例如：<code>AutoMapper</code>。
     */
    UPPER_CAMEL_CASE,

    /**
     * 全小写下划线命名，例如：<code>auto_mapper</code>。
     */
    LOWER_UNDER_SCORE_CASE,

    /**
     * 全大写下划线命名，例如：<code>AUTO_MAPPER</code>。
     */
    UPPER_UNDER_SCORE_CASE;

}
