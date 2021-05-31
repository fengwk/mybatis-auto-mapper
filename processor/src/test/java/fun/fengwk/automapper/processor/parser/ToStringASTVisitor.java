package fun.fengwk.automapper.processor.parser;

import fun.fengwk.automapper.processor.parser.ast.*;

import java.util.LinkedList;

/**
 * @author fengwk
 */
public class ToStringASTVisitor implements ASTVisitor {

    LinkedList<String> stack = new LinkedList<>();

    @Override
    public void visit(Insert insert) {
        StringBuilder sb = new StringBuilder();
        sb.append(insert.getLexeme().getValue());
        for (int i = stack.size() - 1; i >= 0; i--) {
            sb.append(stack.get(i));
        }
        stack.push(sb.toString());
    }

    @Override
    public void visit(Delete delete) {
        StringBuilder sb = new StringBuilder();
        sb.append(delete.getLexeme().getValue());
        for (int i = stack.size() - 1; i >= 0; i--) {
            sb.append(stack.get(i));
        }
        stack.push(sb.toString());
    }

    @Override
    public void visit(Update update) {
        StringBuilder sb = new StringBuilder();
        sb.append(update.getLexeme().getValue());
        for (int i = stack.size() - 1; i >= 0; i--) {
            sb.append(stack.get(i));
        }
        stack.push(sb.toString());    }

    @Override
    public void visit(Find find) {
        StringBuilder sb = new StringBuilder();
        sb.append(find.getLexeme().getValue());
        for (int i = stack.size() - 1; i >= 0; i--) {
            sb.append(stack.get(i));
        }
        stack.push(sb.toString());
    }

    @Override
    public void visit(Count count) {
        StringBuilder sb = new StringBuilder();
        sb.append(count.getLexeme().getValue());
        for (int i = stack.size() - 1; i >= 0; i--) {
            sb.append(stack.get(i));
        }
        stack.push(sb.toString());
    }

    @Override
    public void visit(Page page) {
        StringBuilder sb = new StringBuilder();
        sb.append(page.getLexeme().getValue());
        for (int i = stack.size() - 1; i >= 0; i--) {
            sb.append(stack.get(i));
        }
        stack.push(sb.toString());
    }

    @Override
    public void visit(By by) {
        StringBuilder sb = new StringBuilder();
        sb.append(by.getLexeme().getValue());
        for (int i = 0; i < by.childrenSize(); i++) {
            sb.append(stack.pop());
        }
        stack.push(sb.toString());
    }

    @Override
    public void visit(OrderBy orderBy) {
        StringBuilder sb = new StringBuilder();
        sb.append(orderBy.getLexeme().getValue());
        for (int i = 0; i < orderBy.childrenSize(); i++) {
            sb.append(stack.pop());
        }
        stack.push(sb.toString());
    }

    @Override
    public void visit(ConnectOp connectOp) {
        StringBuilder sb = new StringBuilder();
        String c2 = stack.pop();
        String c1 = stack.pop();
        sb.append(c1).append(connectOp.getLexeme().getValue()).append(c2);
        stack.push(sb.toString());
    }

    @Override
    public void visit(ByOp byOp) {
        StringBuilder sb = new StringBuilder();
        sb.append(stack.pop()).append(byOp.getLexeme().getValue());
        stack.push(sb.toString());
    }

    @Override
    public void visit(OrderByOp orderByOp) {
        StringBuilder sb = new StringBuilder();
        sb.append(stack.pop()).append(orderByOp.getLexeme().getValue());
        stack.push(sb.toString());
    }

    @Override
    public void visit(Variable variable) {
        stack.push(variable.getLexeme().getValue());
    }

    @Override
    public void visit(All all) {
        stack.push(all.getLexeme().getValue());
    }

    @Override
    public String toString() {
        return stack.pop();
    }
}
