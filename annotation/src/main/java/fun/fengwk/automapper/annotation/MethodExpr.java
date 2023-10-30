package fun.fengwk.automapper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指明方法表达式，使用该注解可以代替方法名表达式，这在简化冗长的方法名上很有作用。
 *
 * @author fengwk
 */
@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.METHOD })
public @interface MethodExpr {

    /**
     * 表达式内容，例如{@code findByUsernameAndPassword}。
     */
    String value();

}
