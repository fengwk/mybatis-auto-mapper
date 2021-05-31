package fun.fengwk.automapper.processor.lexer;

import fun.fengwk.automapper.processor.AutoMapperException;

/**
 * @author fengwk
 */
public class LexicalException extends AutoMapperException {

    private static final long serialVersionUID = 1L;

    public LexicalException(String message, Object... args) {
        super(message, args);
    }

}
