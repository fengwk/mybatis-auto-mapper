package fun.fengwk.automapper.example.model;

/**
 * @author fengwk
 */
public class SimpleExampleDO {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
