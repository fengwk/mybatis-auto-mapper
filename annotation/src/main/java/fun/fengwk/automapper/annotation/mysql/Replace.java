package fun.fengwk.automapper.annotation.mysql;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 在insert方法上添加@Replace注解，可以获得replace into语法支持。
 *
 * @author fengwk
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface Replace {
}
