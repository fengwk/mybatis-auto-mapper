package fun.fengwk.automapper.processor.user;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author fengwk
 */
public interface CacheRepository<DATA, ID> {

    /* query */

    List<DATA> doListByIds(Collection<ID> ids);

    /* annotation and id converter */

    ID idKeyMapToId(Map<String, Object> idKeyMap);

    /* serialize and deserialize */

    String serializeId(ID id);

    ID deserializedId(String idCacheKey);

    String serializeData(DATA data);

    DATA deserializeData(String dataStr, Class<DATA> dataClass);

    String serializedReturnValue(Object returnValue);

    Object deserializedReturnValue(String returnValueStr, Type returnValueType);

}
