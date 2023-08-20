package fun.fengwk.automapper.processor.demo;

import java.util.List;

/**
 * @author fengwk
 */
public interface BaseMapper<T> {

    List<T> pageAll(int limit);

    int add(int a, int b);

}
