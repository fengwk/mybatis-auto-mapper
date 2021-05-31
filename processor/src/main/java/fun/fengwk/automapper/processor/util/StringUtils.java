package fun.fengwk.automapper.processor.util;

/**
 * @author fengwk
 */
public class StringUtils {

    private StringUtils() {}

    public static String lowerFirst(String str) {
        if (str != null && !str.isEmpty()) {
            return Character.toLowerCase(str.charAt(0)) + str.substring(1);
        }
        return str;
    }

    public static String trimSuffix(String str, String suffix) {
        if (str != null && str.endsWith(suffix)) {
            return str.substring(0, str.length() - suffix.length());
        }
        return str;
    }

    /**
     * 如果str是UpperCameCase则将其转为LowerCamelCase
     *
     * @param str
     * @return
     */
    public static String upperCamelToLowerCamel(String str) {
        if (str == null || str.isEmpty() || Character.isLowerCase(str.charAt(0))) {
            return str;
        }

        // 将i定位到一个小写字符或者结束位置
        int i = 0;
        for (; i < str.length() && Character.isUpperCase(str.charAt(i)); i++) {}

        if (i == 1) {
            return lowerFirst(str);
        } else if (i == str.length()) {
            return str.toLowerCase();
        } else {
            return str.substring(0, i-1).toLowerCase() + str.substring(i-1);
        }
    }

}
