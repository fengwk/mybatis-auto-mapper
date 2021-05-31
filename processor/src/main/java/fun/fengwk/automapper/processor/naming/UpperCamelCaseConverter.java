package fun.fengwk.automapper.processor.naming;

import java.util.Objects;

/**
 * @author fengwk
 */
public class UpperCamelCaseConverter implements NamingConverter {

    @Override
    public String convert(String name) {
        Objects.requireNonNull(name);
        StringBuilder sb = new StringBuilder();
        sb.append(Character.toUpperCase(name.charAt(0)));
        for (int i = 1; i < name.length(); i++) {
            sb.append(name.charAt(i));
        }
        return sb.toString();
    }

}
