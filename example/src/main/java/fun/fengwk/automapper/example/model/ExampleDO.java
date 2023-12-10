package fun.fengwk.automapper.example.model;

import fun.fengwk.automapper.annotation.UpdateIncrement;

/**
 * create table example (
 *   id bigint not null auto_increment,
 *   name varchar(32),
 *   sort int,
 *   primary key(id)
 * )
 *
 * @author fengwk
 */
public class ExampleDO extends BaseDO {

    private String name;
    private Integer sort;

    private String f1;
    private String f2;
    private String f3;

    private Integer isDeleted;

    @UpdateIncrement
    private Long version;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getF1() {
        return f1;
    }

    public void setF1(String f1) {
        this.f1 = f1;
    }

    public String getF2() {
        return f2;
    }

    public void setF2(String f2) {
        this.f2 = f2;
    }

    public String getF3() {
        return f3;
    }

    public void setF3(String f3) {
        this.f3 = f3;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
