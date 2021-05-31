package fun.fengwk.automapper.processor.parser;

import fun.fengwk.automapper.processor.lexer.Token;
import fun.fengwk.automapper.processor.lexer.TokenType;
import fun.fengwk.automapper.processor.util.PeekBackIterator;

import java.util.Iterator;
import java.util.function.Predicate;

/**
 * @author fengwk
 */
public class TokenIterator extends PeekBackIterator<Token> {

    public TokenIterator(Iterator<Token> iterator) {
        super(iterator);
    }

    public Token nextMatch(Predicate<Token> predicate) {
        Token next = next();
        if (!predicate.test(next)) {
            throw new ParseException(next);
        }
        return next;
    }

    public Token nextMatch(String value) {
        return nextMatch(token -> token.getValue().equals(value));
    }

    public Token nextMatch(TokenType type) {
        return nextMatch(token -> token.getType() == type);
    }

    @Override
    protected void handleNoElementCanNext() throws RuntimeException {
        if (putBackStack.isEmpty()) {
            throw new ParseException("Syntax error");
        } else {
            throw new ParseException("Syntax error, after %s", putBackStack.peek().getValue());
        }
    }

}
