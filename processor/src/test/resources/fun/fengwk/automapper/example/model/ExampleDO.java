package fun.fengwk.automapper.example.model;

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

//    @UseGeneratedKeys
//    private Long id;
    private String name;
    private Integer sort;

//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }

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

    @Override
    public String toString() {
        return String.format("<%d, %s, %d>", getId(), name, sort);
    }
}
