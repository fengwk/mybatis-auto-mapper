package fun.fengwk.automapper.processor.parser;

import fun.fengwk.automapper.processor.AutoMapperException;
import fun.fengwk.automapper.processor.lexer.Token;

/**
 * @author fengwk
 */
public class ParseException extends AutoMapperException {

    private static final long serialVersionUID = 1L;

    public ParseException(Throwable cause) {
        super(cause);
    }

    public ParseException(String message) {
        super(message);
    }

    public ParseException(String message, Object... args) {
        super(message, args);
    }

    public ParseException(Token token) {
        this("Syntax error, unexpected token %s", token.getValue());
    }

}
