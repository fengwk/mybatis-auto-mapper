package fun.fengwk.automapper.processor.lexer;

/**
 * @author fengwk
 */
public enum Keyword {

    INSERT("insert"),
    DELETE("delete"),
    UPDATE("update"),
    FIND("find"),
    COUNT("count"),
    PAGE("page"),

    ALL("All"),
    BY("By"),
    ORDER_BY("OrderBy"),

    AND("And"),
    OR("Or"),

    ASC("Asc"),
    DESC("Desc"),

    IS("Is"),
    EQUALS("Equals"),
    LESS_THAN("LessThan"),
    LESS_THAN_EQUALS("LessThanEquals"),
    GREATER_THAN("GreaterThan"),
    GREATER_THAN_EQUALS("GreaterThanEquals"),
    AFTER("After"),
    BEFORE("Before"),
    IS_NULL("IsNull"),
    IS_NOT_NULL("IsNotNull"),
    NOT_NULL("NotNull"),
    LIKE("Like"),
    NOT_LIKE("NotLike"),
    STARTING_WITH("StartingWith"),
    ENDING_WITH("EndingWith"),
    CONTAINING("Containing"),
    NOT("Not"),
    IN("In"),
    NOT_IN("NotIn");

    private final String value;

    private Keyword(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Keyword of(String value) {
        if (value != null) {
            for (Keyword kw : Keyword.values()) {
                if (value.equals(kw.getValue())) {
                    return kw;
                }
            }
        }
        return null;
    }

    public static Keyword[] getAndOr() {
        return new Keyword[] {
                Keyword.AND,
                Keyword.OR
        };
    }

    public static Keyword[] getByKeywords() {
        return new Keyword[] {
                Keyword.IS,
                Keyword.EQUALS,
                Keyword.LESS_THAN,
                Keyword.LESS_THAN_EQUALS,
                Keyword.GREATER_THAN,
                Keyword.GREATER_THAN_EQUALS,
                Keyword.AFTER,
                Keyword.BEFORE,
                Keyword.IS_NULL,
                Keyword.IS_NOT_NULL,
                Keyword.NOT_NULL,
                Keyword.LIKE,
                Keyword.NOT_LIKE,
                Keyword.STARTING_WITH,
                Keyword.ENDING_WITH,
                Keyword.CONTAINING,
                Keyword.NOT,
                Keyword.IN,
                Keyword.NOT_IN
        };
    }

    public static Keyword[] getOrderByKeywords() {
        return new Keyword[] {
                Keyword.ASC,
                Keyword.DESC
        };
    }
}
