package fun.fengwk.automapper.example.mapper;

import fun.fengwk.automapper.annotation.AutoMapper;
import fun.fengwk.automapper.example.model.ExampleDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *
 * @author fengwk
 */
@AutoMapper
public interface ExampleMapper extends BaseMapper<ExampleDO> {

    int insert(ExampleDO exampleDO);

    int deleteById(long id);

    int updateById(ExampleDO exampleDO);

    ExampleDO findById(long id);

    List<ExampleDO> findByNameStartingWith(String name);

    List<ExampleDO> findByNameStartingWithAndSortGreaterThanEqualsOrderBySortDesc(@Param("name") String name, @Param("sort") int sort);

    List<ExampleDO> pageByNameStartingWithAndSortGreaterThanEqualsOrderBySortDesc(@Param("name") String name, @Param("sort") int sort, @Param("limit") int limit);

}
