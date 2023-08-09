package fun.fengwk.automapper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link DynamicOrderBy}标记的参数将会用于动态生成order by语句。
 *
 * @author fengwk
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.PARAMETER)
public @interface DynamicOrderBy {

}
