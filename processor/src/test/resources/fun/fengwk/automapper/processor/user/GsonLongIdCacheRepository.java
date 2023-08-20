package fun.fengwk.automapper.processor.user;

/**
 * @author fengwk
 */
public interface GsonLongIdCacheRepository<DATA>
    extends GsonCacheRepository<DATA, Long>, SimpleIdCacheRepository<DATA, Long> {

    default String serializeId(Long id) {
        return id == null ? null : String.valueOf(id);
    }

    default Long deserializedId(String idCacheKey) {
        return idCacheKey == null ? null : Long.valueOf(idCacheKey);
    }

}
