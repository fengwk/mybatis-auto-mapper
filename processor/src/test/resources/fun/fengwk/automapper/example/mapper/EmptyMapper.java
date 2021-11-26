package fun.fengwk.automapper.example.mapper;

import fun.fengwk.automapper.annotation.AutoMapper;
import fun.fengwk.automapper.example.model.EmptyDO;

/**
 * @author fengwk
 */
@AutoMapper
public interface EmptyMapper {

    int insert(EmptyDO emptyDO);

}
