package fun.fengwk.automapper.processor.parser.ast;

import fun.fengwk.automapper.processor.lexer.Token;

/**
 * @author fengwk
 */
public class Variable extends ASTNode {

    public Variable(Token lexeme) {
        super(lexeme);
    }

    @Override
    protected void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

}
