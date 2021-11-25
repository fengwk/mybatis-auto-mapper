package fun.fengwk.automapper.example.model;

import fun.fengwk.automapper.annotation.UseGeneratedKeys;

/**
 * @author fengwk
 */
public class BaseDO {

    @UseGeneratedKeys
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
