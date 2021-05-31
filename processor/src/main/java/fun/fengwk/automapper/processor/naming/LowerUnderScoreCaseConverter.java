package fun.fengwk.automapper.processor.naming;

import java.util.Objects;

/**
 * @author fengwk
 */
public class LowerUnderScoreCaseConverter implements NamingConverter {

    private static final String UNDER_SCORE = "_";

    @Override
    public String convert(String name) {
        Objects.requireNonNull(name);
        StringBuilder sb = new StringBuilder();
        // i指向name中下一个要处理的字符
        for (int i = 0; i < name.length();) {
            if (Character.isUpperCase(name.charAt(i))) {
                sb.append(UNDER_SCORE).append(Character.toLowerCase(name.charAt(i)));
                i++;
                // case1.URLName时需要从U检查到L
                // case2.URL需要从U检查到L
                while (i < name.length()
                        && Character.isUpperCase(name.charAt(i))
                        && (i + 1 == name.length() || Character.isUpperCase(name.charAt(i + 1)))) {
                    sb.append(Character.toLowerCase(name.charAt(i)));
                    i++;
                }
            } else {
                sb.append(name.charAt(i));
                i++;
            }
        }
        return sb.toString();
    }

}
