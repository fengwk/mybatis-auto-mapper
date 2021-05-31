package fun.fengwk.automapper.processor.demo;

import fun.fengwk.automapper.annotation.AutoMapper;

import java.util.List;

/**
 * @author fengwk
 */
public interface BaseMapper<T> {

    List<T> pageAll();

}
