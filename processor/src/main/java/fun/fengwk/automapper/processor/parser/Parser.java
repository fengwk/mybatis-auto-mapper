package fun.fengwk.automapper.processor.parser;

import fun.fengwk.automapper.processor.lexer.Keyword;
import fun.fengwk.automapper.processor.lexer.Token;
import fun.fengwk.automapper.processor.lexer.TokenType;
import fun.fengwk.automapper.processor.parser.ast.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 产生式：
 * e0 -> insert e1 | delete e2 | update e4 | find e3 | count e2 | page e3
 * e1 -> All | ε
 * e2 -> All | e4
 * e3 -> All | All e5 | e4 | e4 e5
 * e4 -> By e6
 * e5 -> OrderBy e7
 * e6 -> Variable ByOp | Variable | Variable ByOp And e5 | Variable And e5 | Variable ByOp Or e5 | Variable Or e5
 * e7 -> Variable OrderByOp | Variable | Variable OrderByOp And e5 | Variable And e5 | Variable OrderByOp Or e5 | Variable Or e5
 *
 * @author fengwk
 */
public class Parser {

    private static final Set<String> AND_OR_KEYWORD_SET = Arrays.asList(Keyword.getAndOr()).stream()
            .map(Keyword::getValue).collect(Collectors.toSet());
    private static final Set<String> BY_KEYWORD_SET = Arrays.asList(Keyword.getByKeywords()).stream()
            .map(Keyword::getValue).collect(Collectors.toSet());
    private static final Set<String> ORDER_BY_KEYWORD_SET = Arrays.asList(Keyword.getOrderByKeywords()).stream()
            .map(Keyword::getValue).collect(Collectors.toSet());

    private TokenIterator iterator;

    public ASTNode parse(TokenIterator iterator) {
        this.iterator = iterator;
        return e0();
    }

    private ASTNode e0() {
        ASTNode node;
        Token token = iterator.next();
        if (token.isKeyword(Keyword.INSERT)) {
            Insert insert = new Insert(token);
            ASTNode e1 = e1();
            if (e1 != null) {
                insert.addChild(e1);
            }
            node = insert;
        } else if (token.isKeyword(Keyword.DELETE)) {
            Delete delete = new Delete(token);
            delete.addChild(e2());
            node = delete;
        } else if (token.isKeyword(Keyword.UPDATE)) {
            Update update = new Update(token);
            update.addChild(e4());
            node = update;
        } else if (token.isKeyword(Keyword.FIND)) {
            Find find = new Find(token);
            find.addChildren(e3());
            node = find;
        } else if (token.isKeyword(Keyword.COUNT)) {
            Count count = new Count(token);
            count.addChild(e2());
            node = count;
        } else if (token.isKeyword(Keyword.PAGE)) {
            Page page = new Page(token);
            page.addChildren(e3());
            node = page;
        } else {
            throw new ParseException(token);
        }

        if (iterator.hasNext()) {
            throw new ParseException(iterator.next());
        }

        return node;
    }

    private ASTNode e1() {
        if (!iterator.hasNext()) {
            return null;
        }

        Token token = iterator.nextMatch(Keyword.ALL.getValue());
        return new All(token);
    }

    private ASTNode e2() {
        if (iterator.peek().isKeyword(Keyword.ALL)) {
            return new All(iterator.next());
        } else {
            return e4();
        }
    }

    private List<ASTNode> e3() {
        List<ASTNode> nodes = new ArrayList<>();

        if (iterator.peek().isKeyword(Keyword.ALL)) {
            nodes.add(new All(iterator.next()));
            if (iterator.hasNext()) {
                nodes.add(e5());
            }
        } else {
            nodes.add(e4());
            ASTNode e5 = tryE5();
            if (e5 != null) {
                nodes.add(e5);
            }
        }

        return nodes;
    }

    private ASTNode tryE5() {
        if (!iterator.hasNext() || !iterator.peek().isKeyword(Keyword.ORDER_BY)) {
            return null;
        }

        return e5();
    }

    private ASTNode e4() {
        By by = new By(iterator.nextMatch(Keyword.BY.getValue()));
        by.addChild(e6());
        return by;
    }

    private ASTNode e5() {
        OrderBy orderBy = new OrderBy(iterator.nextMatch(Keyword.ORDER_BY.getValue()));
        orderBy.addChild(e7());
        return orderBy;
    }

    private ASTNode e6() {
        ConnectOp prevConnectOp = null;
        LinkedList<ASTNode> output = new LinkedList<>();

        for (;;) {
            Token varToken = iterator.nextMatch(TokenType.VARIABLE);
            Variable variable = new Variable(varToken);
            if (iterator.hasNext() && BY_KEYWORD_SET.contains(iterator.peek().getValue())) {
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

        return output.pop();
    }

    private ASTNode e7() {
        ConnectOp prevConnectOp = null;
        LinkedList<ASTNode> output = new LinkedList<>();

        for (;;) {
            Token varToken = iterator.nextMatch(TokenType.VARIABLE);
            Variable variable = new Variable(varToken);
            if (iterator.hasNext() && ORDER_BY_KEYWORD_SET.contains(iterator.peek().getValue())) {
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

        return output.pop();
    }

    private void connect(ConnectOp connectOp, LinkedList<ASTNode> output) {
        ASTNode n2 = output.pop();
        ASTNode n1 = output.pop();
        connectOp.addChild(n1);
        connectOp.addChild(n2);
        output.push(connectOp);
    }

}
