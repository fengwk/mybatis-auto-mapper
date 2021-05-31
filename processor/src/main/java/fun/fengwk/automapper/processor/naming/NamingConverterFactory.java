package fun.fengwk.automapper.processor.naming;

import fun.fengwk.automapper.annotation.NamingStyle;
import fun.fengwk.automapper.processor.AutoMapperException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fengwk
 */
public class NamingConverterFactory {

    private static final Map<NamingStyle, NamingConverter> REGISTRY;

    static {
        Map<NamingStyle, NamingConverter> registry = new HashMap<>();
        registry.put(NamingStyle.LOWER_CAMEL_CASE, new LowerCamelCaseConverter());
        registry.put(NamingStyle.UPPER_CAMEL_CASE, new UpperCamelCaseConverter());
        registry.put(NamingStyle.LOWER_UNDER_SCORE_CASE, new LowerUnderScoreCaseConverter());
        registry.put(NamingStyle.UPPER_UNDER_SCORE_CASE, new UpperUnderScoreCaseConverter());
        REGISTRY = registry;
    }

    public static NamingConverter getInstance(NamingStyle namingStyle) {
        NamingConverter namingConverter = REGISTRY.get(namingStyle);
        if (namingConverter == null) {
            throw new AutoMapperException("Unsupported namingStyle %s", namingStyle);
        }

        return namingConverter;
    }

}
