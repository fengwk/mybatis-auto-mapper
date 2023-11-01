package fun.fengwk.automapper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 允许自定义mybatis类型处理器。
 *
 * @author fengwk
 */
@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface TypeHandler {

    /**
     * 类型处理器类。
     */
    Class<? extends org.apache.ibatis.type.TypeHandler<?>> value();

}
