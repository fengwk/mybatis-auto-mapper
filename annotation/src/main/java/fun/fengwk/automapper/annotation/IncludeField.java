package fun.fengwk.automapper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 引入字段，该注解用于insert或update方法，一旦使用该注解，那么只有被指定的字段在insert或update表达式生成xml时被使用。
 *
 * @author fengwk
 */
@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.METHOD })
@Repeatable(IncludeField.List.class)
public @interface IncludeField {

    /**
     * 指定Bean字段名称。
     *
     * @return
     */
    String value();

    /**
     * 批量处理引入字段注解。
     */
    @Retention(RetentionPolicy.CLASS)
    @Target({ ElementType.METHOD })
    @interface List {

        /**
         * 引入字段注解数组。
         *
         * @return
         */
        IncludeField[] value();

    }

}
