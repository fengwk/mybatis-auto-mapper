package fun.fengwk.automapper.processor.demo;

import fun.fengwk.automapper.annotation.AutoMapper;
import fun.fengwk.automapper.annotation.DBType;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author fengwk
 */
@AutoMapper(dbType = DBType.POSTGRESQL)
public interface DemoPgMapper extends BaseMapper<DemoDO> {

    List<DemoDO> findByNameStartingWith(@Param("name") String name);

    List<DemoDO> pageByName(@Param("name") String name, @Param("offset") int offset, @Param("limit") int limit);

    int insertAllSelective(List<DemoDO> demoDOList);

    @Override
    default int add(int a, int b) {
        return a + b;
    }

}
