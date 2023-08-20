package fun.fengwk.automapper.processor.user;

import java.lang.reflect.Type;

/**
 * @author fengwk
 */
public interface GsonCacheRepository<DATA, ID> extends CacheRepository<DATA, ID> {

    @Override
    default String serializeData(DATA data) {
        return (String) data;
    }

    @Override
    default DATA deserializeData(String dataStr, Class<DATA> dataClass) {
        return (DATA) dataStr;
    }

    @Override
    default String serializedReturnValue(Object returnValue) {
        return (String) returnValue;
    }

    @Override
    default Object deserializedReturnValue(String returnValueStr, Type returnValueType) {
        return returnValueStr;
    }

}
