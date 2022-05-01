package fun.fengwk.automapper.example.mapper;

import fun.fengwk.automapper.annotation.AutoMapper;
import fun.fengwk.automapper.annotation.DBType;
import fun.fengwk.automapper.example.model.EmptyDO;

/**
 * @author fengwk
 */
@AutoMapper(tableNamePrefix = "emm_")
public interface EmptyMapper {

    int insert(EmptyDO emptyDO);

}
