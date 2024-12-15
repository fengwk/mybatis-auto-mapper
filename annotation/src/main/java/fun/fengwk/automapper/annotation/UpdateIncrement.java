package fun.fengwk.automapper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标识更新增量字段，用于更新时{@code field=field+?}，常见的使用场景是应用在版本号字段中。
 *
 * @author fengwk
 */
@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface UpdateIncrement {

    /**
     * 递增值。
     */
    String value() default "+1";

}
