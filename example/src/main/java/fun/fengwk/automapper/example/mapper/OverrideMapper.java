package fun.fengwk.automapper.example.mapper;

import fun.fengwk.automapper.annotation.AutoMapper;
import fun.fengwk.automapper.annotation.FieldName;
import fun.fengwk.automapper.annotation.Selective;
import fun.fengwk.automapper.example.model.EmptyDO;
import fun.fengwk.automapper.example.model.OverrideDO;

import java.util.List;

/**
 * @author fengwk
 */
@AutoMapper
public interface OverrideMapper {

    int insert(OverrideDO overrideDO);

    OverrideDO findById(@FieldName("ov_id") Long id);

    List<OverrideDO> findByAge(@Selective Integer age);

}
