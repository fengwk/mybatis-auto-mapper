package fun.fengwk.automapper.processor.lexer;

import java.util.Objects;

/**
 * @author fengwk
 */
public class Token {

    private final TokenType type;
    private final String value;

    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public boolean isKeyword(Keyword keyword) {
        return keyword.getValue().equals(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Token other = (Token) o;
        return type == other.type && Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value);
    }

    @Override
    public String toString() {
        return String.format("type %s, value %s", type, value);
    }

}
