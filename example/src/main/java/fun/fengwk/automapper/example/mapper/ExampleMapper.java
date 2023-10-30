package fun.fengwk.automapper.example.mapper;

import fun.fengwk.automapper.annotation.*;
import fun.fengwk.automapper.example.model.ExampleDO;
import fun.fengwk.automapper.example.model.LogicDeleteParams;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 *
 * @author fengwk
 */
@AutoMapper
public interface ExampleMapper extends BaseMapper<ExampleDO> {

    @ExcludeField("f1")
    int insert(ExampleDO exampleDO);

    @IncludeField("f1")
    @IncludeField("f2")
    @ExcludeField("f1")
    int insertSelective(ExampleDO exampleDO);

    int insertIgnore(ExampleDO exampleDO);

    int insertOnDuplicateKeyUpdateSelective(ExampleDO exampleDO);

    int replace(ExampleDO exampleDO);

    @ExcludeField.List({
            @ExcludeField("f1"),
            @ExcludeField("f2")
    })
    int insertAll(List<ExampleDO> exampleDOs);

    int insertAllSelective(List<ExampleDO> exampleDOs);

    int deleteById(long id);

    int updateById(ExampleDO exampleDO);

    int updateByIdSelective(ExampleDO exampleDO);

    ExampleDO findById(long id);

    List<ExampleDO> findByName(@Selective String name);

    List<ExampleDO> findByNameStartingWith(@Param("name") String name, @DynamicOrderBy @Param("orderBy") String orderBy);

    List<ExampleDO> findByNameStartingWithAndSortGreaterThanEqualsOrderBySortDesc(@Param("name") String name, @Param("sort") int sort);

    List<ExampleDO> pageByNameStartingWithAndSortGreaterThanEqualsOrderBySortDesc(@Param("name") String name, @Param("sort") int sort, @Param("limit") int limit);

    List<ExampleDO> findByIdInAndIsDeleted(@Param("id") Collection<Long> ids, int isDeleted);

    int countByIdIn(List<Long> ids);

    @MethodExpr("updateByIdAndVersion")
    int logicDelete(LogicDeleteParams params);

}
