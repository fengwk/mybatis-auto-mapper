package fun.fengwk.automapper.processor.user;

import fun.fengwk.automapper.annotation.AutoMapper;

import java.util.Collection;
import java.util.List;

/**
 * @author fengwk
 */
@AutoMapper
public interface UserMapper extends GsonLongIdCacheRepository<UserDO> {

    int insert(UserDO record);

    int insertAll(Collection<UserDO> records);

    int updateById(UserDO record);

    int updateByIdSelective(UserDO record);

    int countById(Long id);

    UserDO findById(Long id);

    List<UserDO> findByIdIn(Collection<Long> ids);

    List<UserDO> findByAgeOrderByIdDesc(int age);

    @Override
    default List<UserDO> doListByIds(Collection<Long> ids) {
        return findByIdIn(ids);
    }

}
