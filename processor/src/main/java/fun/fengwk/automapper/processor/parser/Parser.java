package fun.fengwk.automapper.processor.parser;

import fun.fengwk.automapper.processor.lexer.Keyword;
import fun.fengwk.automapper.processor.lexer.Token;
import fun.fengwk.automapper.processor.lexer.TokenType;
import fun.fengwk.automapper.processor.parser.ast.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 产生式：
 * {@code
 * e0  -> insert e1 | delete e2 | update e3 | find e4 | count e2 | page e4
 * e1  -> All e5 | e5 | ε
 * e2  -> All | By e6
 * e3  -> By e7
 * e4  -> All e8 | By e9
 * e5  -> Selective | ε
 * e6  -> byTerm (byOp){0,1} (And|Or) e6 | byTerm (byOp){0,1}
 * e7  -> byTerm (byOp){0,1} (And|Or) e7 | byTerm (byOp){0,1} Selective | byTerm (byOp){0,1}
 * e8  -> OrderBy e10 | ε
 * e9  -> byTerm (byOp){0,1} (And|Or) e9 | byTerm (byOp){0,1} OrderBy e10 | byTerm (byOp){0,1}
 * e10 -> orderByTerm (orderByOp){0,1} And e10 | orderByTerm (orderByOp){0,1}
 * }
 *
 * @author fengwk
 */
public class Parser {

    private static final Set<String> AND_OR_KEYWORD_SET = Arrays.stream(Keyword.getAndOr())
            .map(Keyword::getValue).collect(Collectors.toSet());
    private static final Set<String> BY_OP_SET = Arrays.stream(Keyword.getByOps())
            .map(Keyword::getValue).collect(Collectors.toSet());
    private static final Set<String> ORDER_BY_OP_SET = Arrays.stream(Keyword.getOrderByOps())
            .map(Keyword::getValue).collect(Collectors.toSet());

    private TokenIterator iterator;

    public ASTNode parse(TokenIterator iterator) {
        this.iterator = iterator;
        return e0();
    }

    // e0  -> insert e1 | delete e2 | update e3 | find e4 | count e2 | page e4
    private ASTNode e0() {
        ASTNode node;
        Token token = iterator.next();
        if (token.isKeyword(Keyword.INSERT)) {
            Insert insert = new Insert(token);
            insert.addChildren(e1());
            node = insert;
        } else if (token.isKeyword(Keyword.DELETE)) {
            Delete delete = new Delete(token);
            delete.addChildren(e2());
            node = delete;
        } else if (token.isKeyword(Keyword.UPDATE)) {
            Update update = new Update(token);
            update.addChildren(e3());
            node = update;
        } else if (token.isKeyword(Keyword.FIND)) {
            Find find = new Find(token);
            find.addChildren(e4());
            node = find;
        } else if (token.isKeyword(Keyword.COUNT)) {
            Count count = new Count(token);
            count.addChildren(e2());
            node = count;
        } else if (token.isKeyword(Keyword.PAGE)) {
            Page page = new Page(token);
            page.addChildren(e4());
            node = page;
        } else {
            throw new ParseException(token);
        }

        if (iterator.hasNext()) {
            throw new ParseException(iterator.next());
        }

        return node;
    }

    // e1  -> All e5 | ε
    private List<ASTNode> e1() {
        if (!iterator.hasNext()) {
            return Collections.emptyList();
        }
        List<ASTNode> nodes = new ArrayList<>();
        if (iterator.peek().isKeyword(Keyword.ALL)) {
            Token token = iterator.nextMatch(Keyword.ALL.getValue());
            nodes.add(new All(token));
        }
        nodes.addAll(e5());
        return nodes;
    }

    // e2  -> All | By e6
    private List<ASTNode> e2() {
        if (iterator.peek().isKeyword(Keyword.ALL)) {
            return Collections.singletonList(new All(iterator.next()));
        } else {
            By by = new By(iterator.nextMatch(Keyword.BY.getValue()));
            return e6(by);
        }
    }

    // e3  -> By e7
    private List<ASTNode> e3() {
        By by = new By(iterator.nextMatch(Keyword.BY.getValue()));
        return e7(by);
    }

    // e4  -> All e8 | By e9
    private List<ASTNode> e4() {
        if (iterator.peek().isKeyword(Keyword.ALL)) {
            List<ASTNode> nodes = new ArrayList<>();
            nodes.add(new All(iterator.next()));
            nodes.addAll(e8());
            return nodes;
        } else {
            By by = new By(iterator.nextMatch(Keyword.BY.getValue()));
            return e9(by);
        }
    }

    // e5  -> Selective | ε
    private List<ASTNode> e5() {
        if (iterator.hasNext()) {
            return Collections.singletonList(new Selective(iterator.nextMatch(Keyword.SELECTIVE.getValue())));
        } else {
            return Collections.emptyList();
        }
    }

    // e6  -> byTerm (byOp){0,1} (And|Or) e6 | byTerm (byOp){0,1}
    private List<ASTNode> e6(By by) {
        ConnectOp prevConnectOp = null;
        LinkedList<ASTNode> output = new LinkedList<>();

        for (;;) {
            Token varToken = iterator.nextMatch(TokenType.VARIABLE);
            Variable variable = new Variable(varToken);
            if (iterator.hasNext() && BY_OP_SET.contains(iterator.peek().getValue())) {
                ByOp byOp = new ByOp(iterator.next());
                byOp.addChild(variable);
                output.push(byOp);
            } else {
                ByOp byOp = new ByOp(new Token(TokenType.KEYWORD, Keyword.IS.getValue()));
                byOp.addChild(variable);
                output.push(byOp);
            }

            if (!iterator.hasNext() || !AND_OR_KEYWORD_SET.contains(iterator.peek().getValue())) {
                break;
            }

            if (prevConnectOp != null) {
                connect(prevConnectOp, output);
            }

            prevConnectOp = new ConnectOp(iterator.next());
        }

        if (prevConnectOp != null) {
            connect(prevConnectOp, output);
        }

        if (output.size() != 1) {
            throw new ParseException("Syntax error");
        }

        by.addChild(output.pop());
        return Collections.singletonList(by);
    }

    // e7  -> byTerm (byOp){0,1} (And|Or) e7 | byTerm (byOp){0,1} Selective | byTerm (byOp){0,1}
    // e7  -> e6 | e6 Selective
    private List<ASTNode> e7(By by) {
        List<ASTNode> e6 = e6(by);
        if (!iterator.hasNext()) {
            return e6;
        }
        List<ASTNode> nodes = new ArrayList<>(e6);
        nodes.add(new Selective(iterator.nextMatch(Keyword.SELECTIVE.getValue())));
        return nodes;
    }

    // e8  -> OrderBy e10 | ε
    private List<ASTNode> e8() {
        if (iterator.hasNext()) {
            return e10(new OrderBy(iterator.nextMatch(Keyword.ORDER_BY.getValue())));
        } else {
            return Collections.emptyList();
        }
    }

    // e9  -> byTerm (byOp){0,1} (And|Or) e9 | byTerm (byOp){0,1} OrderBy e10 | byTerm (byOp){0,1}
    // e9  -> e6 | e6 OrderBy e10
    private List<ASTNode> e9(By by) {
        List<ASTNode> e6 = e6(by);
        if (!iterator.hasNext()) {
            return e6;
        }
        List<ASTNode> nodes = new ArrayList<>(e6);
        nodes.addAll(e10(new OrderBy(iterator.nextMatch(Keyword.ORDER_BY.getValue()))));
        return nodes;
    }

    // e10 -> orderByTerm (orderByOp){0,1} And e10 | orderByTerm (orderByOp){0,1}
    private List<ASTNode> e10(OrderBy orderBy) {
        ConnectOp prevConnectOp = null;
        LinkedList<ASTNode> output = new LinkedList<>();

        for (;;) {
            Token varToken = iterator.nextMatch(TokenType.VARIABLE);
            Variable variable = new Variable(varToken);
            if (iterator.hasNext() && ORDER_BY_OP_SET.contains(iterator.peek().getValue())) {
                OrderByOp orderByOp = new OrderByOp(iterator.next());
                orderByOp.addChild(variable);
                output.push(orderByOp);
            } else {
                OrderByOp orderByOp = new OrderByOp(new Token(TokenType.KEYWORD, Keyword.ASC.getValue()));
                orderByOp.addChild(variable);
                output.push(orderByOp);
            }

            if (!iterator.hasNext() || !AND_OR_KEYWORD_SET.contains(iterator.peek().getValue())) {
                break;
            }

            if (prevConnectOp != null) {
                connect(prevConnectOp, output);
            }

            prevConnectOp = new ConnectOp(iterator.next());
        }

        if (prevConnectOp != null) {
            connect(prevConnectOp, output);
        }

        if (output.size() != 1) {
            throw new ParseException("Syntax error");
        }

        orderBy.addChild(output.pop());
        return Collections.singletonList(orderBy);
    }

    private void connect(ConnectOp connectOp, LinkedList<ASTNode> output) {
        ASTNode n2 = output.pop();
        ASTNode n1 = output.pop();
        connectOp.addChild(n1);
        connectOp.addChild(n2);
        output.push(connectOp);
    }

}
