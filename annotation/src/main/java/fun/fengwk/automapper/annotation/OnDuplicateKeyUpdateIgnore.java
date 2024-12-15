package fun.fengwk.automapper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 在发生on duplicate key冲突时将忽略这些字段的更新。
 *
 * @author fengwk
 */
@Id
@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface OnDuplicateKeyUpdateIgnore {

}
