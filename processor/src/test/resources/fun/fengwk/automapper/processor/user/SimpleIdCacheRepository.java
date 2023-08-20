package fun.fengwk.automapper.processor.user;

import java.util.Map;

/**
 * @author fengwk
 */
public interface SimpleIdCacheRepository<DATA, ID> extends CacheRepository<DATA, ID> {

    @Override
    default ID idKeyMapToId(Map<String, Object> idKeyMap) {
        if (idKeyMap == null || idKeyMap.isEmpty()) {
            return null;
        }
        return (ID) idKeyMap.values().iterator().next();
    }

}
