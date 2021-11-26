package fun.fengwk.automapper.example.mapper;

import fun.fengwk.automapper.annotation.AutoMapper;
import fun.fengwk.automapper.example.model.SimpleExampleDO;

import java.util.List;

/**
 * @author fengwk
 */
@AutoMapper(tableName = "example")
public interface SimpleExampleMapper {

    List<SimpleExampleDO> findAllOrderByNameDesc();

}
