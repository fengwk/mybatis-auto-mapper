package fun.fengwk.automapper.example.mapper;

import fun.fengwk.automapper.annotation.AutoMapper;
import fun.fengwk.automapper.example.model.NewExampleDO;

import java.util.List;

/**
 * @author fengwk
 */
@AutoMapper
public interface NewExampleMapper {

    int insert(NewExampleDO newExampleDO);

    List<NewExampleDO> findAll();

    int updateById(NewExampleDO exampleDO);

}
