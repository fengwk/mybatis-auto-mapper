package fun.fengwk.automapper.processor.lexer;

import java.util.Objects;

/**
 * 派生的令牌，由某一种类型的令牌派生而来。
 *
 * @author fengwk
 */
public class DerivedToken extends Token {

    private final String derivedValue;

    public DerivedToken(TokenType type, String value, String derivedValue) {
        super(type, value);
        this.derivedValue = derivedValue;
    }

    public String getDerivedValue() {
        return derivedValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DerivedToken that = (DerivedToken) o;
        return Objects.equals(derivedValue, that.derivedValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), derivedValue);
    }

    @Override
    public String toString() {
        return super.toString() + ", derivedValue " + derivedValue;
    }

}
