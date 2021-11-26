package fun.fengwk.automapper.example.model;

import fun.fengwk.automapper.annotation.UseGeneratedKeys;

/**
 * @author fengwk
 */
public class BaseDO {

    private static final int IS_DELETED = 1;

    @UseGeneratedKeys
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
