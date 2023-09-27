package fun.fengwk.automapper.example.model;

import fun.fengwk.automapper.annotation.FieldName;
import fun.fengwk.automapper.annotation.Selective;
import fun.fengwk.automapper.annotation.UseGeneratedKeys;

/**
 * @author fengwk
 */
public class OverrideDO extends BaseOverrideDO {

    private Integer age;

    @UseGeneratedKeys
    @FieldName("ov_id")
    @Override
    public Long getId() {
        return super.getId();
    }

    @Selective
    @Override
    public String getName() {
        return super.getName();
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

}
