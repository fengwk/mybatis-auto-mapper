package fun.fengwk.automapper.example.model;

import fun.fengwk.automapper.annotation.FieldName;
import fun.fengwk.automapper.annotation.Selective;
import fun.fengwk.automapper.annotation.UseGeneratedKeys;

/**
 * @author fengwk
 */
public class BaseOverrideDO {

    private Long id;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
