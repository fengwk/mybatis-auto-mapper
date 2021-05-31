package fun.fengwk.automapper.annotation;

import java.lang.annotation.*;

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
