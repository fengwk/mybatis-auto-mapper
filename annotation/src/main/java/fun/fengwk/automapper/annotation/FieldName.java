package fun.fengwk.automapper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字段名称注解，该注解用于应对字段与表字段无法通过简单名称转换达成映射的场景。
 *
 * @author fengwk
 */
@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD })
public @interface FieldName {

    /**
     * 指定字段对应表名称。
     *
     * @return
     */
    String value();

}
