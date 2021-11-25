package fun.fengwk.automapper.processor.translator;

import fun.fengwk.automapper.annotation.DBType;
import fun.fengwk.automapper.processor.AutoMapperException;
import fun.fengwk.automapper.processor.translator.mysql.MySqlTranslator;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author fengwk
 */
public class TranslatorFactory {

    private static final Map<DBType, Function<TranslateContext, Translator>> REGISTRY;

    static {
        Map<DBType, Function<TranslateContext, Translator>> registry = new HashMap<>();
        registry.put(DBType.MYSQL, MySqlTranslator::new);
        REGISTRY = registry;
    }

    public static Translator getInstance(DBType dbType, TranslateContext ctx) {
        Function<TranslateContext, Translator> factory = REGISTRY.get(dbType);
        if (factory == null) {
            throw new AutoMapperException("Unsupported dbType %s", dbType);
        }

        return factory.apply(ctx);
    }

}
