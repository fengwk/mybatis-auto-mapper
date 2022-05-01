package fun.fengwk.automapper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 排除字段，该注解将作用于insert或update方法，被该注解指定的字段将在insert或update表达式生成xml时被忽略。
 *
 * @author fengwk
 */
@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.METHOD })
@Repeatable(ExcludeField.List.class)
public @interface ExcludeField {

    /**
     * 指定Bean字段名称。
     *
     * @return
     */
    String value();

    /**
     * 批量处理排除字段注解。
     */
    @Retention(RetentionPolicy.CLASS)
    @Target({ ElementType.METHOD })
    @interface List {

        /**
         * 排除字段注解数组。
         *
         * @return
         */
        ExcludeField[] value();

    }

}
