package fun.fengwk.automapper.example.model;

import fun.fengwk.automapper.annotation.UpdateIncrement;

import javax.annotation.sql.DataSourceDefinition;

/**
 * @author fengwk
 */
public class LogicDeleteParams {

    private Long id;
    @UpdateIncrement
    private Long version;
    private Integer deleted = 1;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }
}
