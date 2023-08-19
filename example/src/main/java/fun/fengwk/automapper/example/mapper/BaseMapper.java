package fun.fengwk.automapper.example.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 *
 * @author fengwk
 */
public interface BaseMapper<E> {

    List<E> pageAll(@Param("offset") int offset, @Param("limit") int limit);

}
