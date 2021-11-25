package fun.fengwk.automapper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author fengwk
 */
@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
public @interface FieldName {

    /**
     * 指定字段名称。
     *
     * @return
     */
    String value() default "";

}
