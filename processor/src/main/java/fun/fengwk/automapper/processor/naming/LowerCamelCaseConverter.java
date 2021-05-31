package fun.fengwk.automapper.processor.naming;

import java.util.Objects;

/**
 * @author fengwk
 */
public class LowerCamelCaseConverter implements NamingConverter {

    @Override
    public String convert(String name) {
        Objects.requireNonNull(name);
        return name;
    }

}
