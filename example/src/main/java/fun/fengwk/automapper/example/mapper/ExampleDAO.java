package fun.fengwk.automapper.example.mapper;

import fun.fengwk.automapper.annotation.AutoMapper;
import fun.fengwk.automapper.annotation.NamingStyle;
import fun.fengwk.automapper.example.model.ExampleDO;

/**
 * @author fengwk
 */
@AutoMapper(
        mapperSuffix = "DAO",
        tableNamingStyle = NamingStyle.UPPER_CAMEL_CASE,
        fieldNamingStyle = NamingStyle.UPPER_UNDER_SCORE_CASE)
public interface ExampleDAO {

    ExampleDO findById(long id);

}
