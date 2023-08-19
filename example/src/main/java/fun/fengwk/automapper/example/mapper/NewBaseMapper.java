package fun.fengwk.automapper.example.mapper;

import java.util.Collection;
import java.util.List;

/**
 * @author fengwk
 */
public interface NewBaseMapper<DO, ID> {

    int insert(DO record);

    int insertAll(Collection<DO> records);

    int deleteById(ID id);

    int deleteByIdIn(Collection<ID> ids);

    int updateById(DO record);

    int updateByIdSelective(DO record);

    int countById(ID id);

    DO findById(ID id);

    List<DO> findByIdIn(Collection<ID> ids);

}
