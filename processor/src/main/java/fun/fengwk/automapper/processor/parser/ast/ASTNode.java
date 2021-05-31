package fun.fengwk.automapper.processor.parser.ast;

import fun.fengwk.automapper.processor.lexer.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author fengwk
 */
public abstract class ASTNode {

    protected List<ASTNode> children = new ArrayList<>();
    protected Token lexeme;

    public ASTNode() {}

    public ASTNode(Token lexeme) {
        this.lexeme = lexeme;
    }

    public int childrenSize() {
        return children.size();
    }

    public void addChildren(List<ASTNode> children) {
        children.forEach(this::addChild);
    }

    public void addChild(ASTNode child) {
        Objects.requireNonNull(child);
        children.add(child);
    }

    public ASTNode getChild(int index) {
        return children.get(index);
    }

    public Token getLexeme() {
        return lexeme;
    }

    public void setLexeme(Token lexeme) {
        this.lexeme = lexeme;
    }

    protected abstract void accept(ASTVisitor visitor);

    public void visit(ASTVisitor preVisitor, ASTVisitor postVisitor) {
        doVisit(this, preVisitor, postVisitor);
    }

    private void doVisit(ASTNode node, ASTVisitor preVisitor, ASTVisitor postVisitor) {
        node.accept(preVisitor);
        node.children.forEach(child -> doVisit(child, preVisitor, postVisitor));
        node.accept(postVisitor);
    }

    public void postVisit(ASTVisitor visitor) {
        doPostVisit(this, visitor);
    }

    private void doPostVisit(ASTNode node, ASTVisitor visitor) {
        node.children.forEach(child -> doPostVisit(child, visitor));
        node.accept(visitor);
    }

    @Override
    public String toString() {
        return lexeme.getValue();
    }
}
