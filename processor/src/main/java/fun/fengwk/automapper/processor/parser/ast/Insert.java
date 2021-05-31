package fun.fengwk.automapper.processor.parser.ast;

import fun.fengwk.automapper.processor.lexer.Token;

/**
 * @author fengwk
 */
public class Insert extends ASTNode {

    public Insert(Token lexeme) {
        super(lexeme);
    }

    @Override
    protected void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

}
