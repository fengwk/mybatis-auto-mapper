package fun.fengwk.automapper.example.mapper;

import fun.fengwk.automapper.annotation.AutoMapper;
import fun.fengwk.automapper.example.model.EmptyDO;

/**
 * @author fengwk
 */
@AutoMapper(tableNamePrefix = "emm_${xx}_", tableNameSuffix = "_${zxc}")
public interface EmptyMapper {

    int insert(EmptyDO emptyDO);

}
