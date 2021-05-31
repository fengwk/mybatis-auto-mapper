package fun.fengwk.automapper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 该标识表示其为自增键，对应mybatis的useGeneratedKeys。
 *
 * @author fengwk
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface UseGeneratedKeys {
}
