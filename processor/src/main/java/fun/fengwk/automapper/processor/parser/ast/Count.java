package fun.fengwk.automapper.processor.parser.ast;

import fun.fengwk.automapper.processor.lexer.Token;

/**
 * @author fengwk
 */
public class Count extends ASTNode {

    public Count(Token lexeme) {
        super(lexeme);
    }

    @Override
    protected void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

}
