package fun.fengwk.automapper.processor.naming;

import fun.fengwk.automapper.annotation.NamingStyle;

/**
 * {@link NamingConverter}用于将java惯用的{@link NamingStyle#LOWER_CAMEL_CASE}命名转换为目标命名。
 *
 * @author fengwk
 */
public interface NamingConverter {

    /**
     * 将java惯用的{@link NamingStyle#LOWER_CAMEL_CASE}命名转换为目标命名。
     *
     * @param name not null
     * @return
     */
    String convert(String name);

}
