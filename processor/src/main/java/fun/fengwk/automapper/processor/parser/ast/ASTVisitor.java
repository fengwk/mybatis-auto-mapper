package fun.fengwk.automapper.processor.parser.ast;

/**
 * @author fengwk
 */
public interface ASTVisitor {

    default void visit(Insert insert) {}

    default void visit(Delete delete) {}

    default void visit(Update update) {}

    default void visit(Find find) {}

    default void visit(Count count) {}

    default void visit(Page page) {}

    default void visit(By by) {}

    default void visit(OrderBy orderBy) {}

    default void visit(ConnectOp connectOp) {}

    default void visit(ByOp byOp) {}

    default void visit(OrderByOp orderByOp) {}

    default void visit(Variable variable) {}

    default void visit(All all) {}

}
