package fun.fengwk.automapper.example.model;

import fun.fengwk.automapper.annotation.TypeHandler;
import fun.fengwk.automapper.example.handler.ToStringHandler;

/**
 * @author fengwk
 */
public class NewExampleDO {

    private Long id;
    @TypeHandler(ToStringHandler.class)
    private Info info;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public static class Info {

        private String name;
        private Integer sort;

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

    }

}
