package fun.fengwk.automapper.processor.parser;

import fun.fengwk.automapper.processor.lexer.Lexer;
import fun.fengwk.automapper.processor.lexer.Token;
import fun.fengwk.automapper.processor.parser.ast.ASTNode;
import org.junit.Test;

import java.util.List;

/**
 * @author fengwk
 */
public class ParserTest {

    @Test
    public void test1() {
        String expr = "insert";
        Lexer lexer = new Lexer.Builder().build();
        List<Token> tokens = lexer.analyse(expr);
        TokenIterator tokenIterator = new TokenIterator(tokens.iterator());
        Parser parser = new Parser();
        ASTNode node = parser.parse(tokenIterator);
        ToStringASTVisitor visitor = new ToStringASTVisitor();
        node.postVisit(visitor);
        assert expr.equals(visitor.toString());
    }

    @Test
    public void test2() {
        String expr = "insertAll";
        Lexer lexer = new Lexer.Builder().build();
        List<Token> tokens = lexer.analyse(expr);
        TokenIterator tokenIterator = new TokenIterator(tokens.iterator());
        Parser parser = new Parser();
        ASTNode node = parser.parse(tokenIterator);
        ToStringASTVisitor visitor = new ToStringASTVisitor();
        node.postVisit(visitor);
        assert expr.equals(visitor.toString());
    }

    @Test
    public void test3() {
        String expr = "deleteByUserTypeAndUserName";
        Lexer lexer = new Lexer.Builder().build();
        List<Token> tokens = lexer.analyse(expr);
        TokenIterator tokenIterator = new TokenIterator(tokens.iterator());
        Parser parser = new Parser();
        ASTNode node = parser.parse(tokenIterator);
        ToStringASTVisitor visitor = new ToStringASTVisitor();
        node.postVisit(visitor);
        assert "deleteByUserTypeIsAndUserNameIs".equals(visitor.toString());
    }

    @Test
    public void test4() {
        String expr = "deleteAll";
        Lexer lexer = new Lexer.Builder().build();
        List<Token> tokens = lexer.analyse(expr);
        TokenIterator tokenIterator = new TokenIterator(tokens.iterator());
        Parser parser = new Parser();
        ASTNode node = parser.parse(tokenIterator);
        ToStringASTVisitor visitor = new ToStringASTVisitor();
        node.postVisit(visitor);
        assert expr.equals(visitor.toString());
    }

    @Test
    public void test5() {
        String expr = "deleteByIdOrderByName";
        Lexer lexer = new Lexer.Builder().build();
        List<Token> tokens = lexer.analyse(expr);
        TokenIterator tokenIterator = new TokenIterator(tokens.iterator());
        Parser parser = new Parser();
        ASTNode node = parser.parse(tokenIterator);
        ToStringASTVisitor visitor = new ToStringASTVisitor();
        node.postVisit(visitor);
        assert "deleteByIdIsOrderByNameIs".equals(visitor.toString());
    }

    @Test
    public void test6() {
        String expr = "updateByUserTypeAndUserName";
        Lexer lexer = new Lexer.Builder().build();
        List<Token> tokens = lexer.analyse(expr);
        TokenIterator tokenIterator = new TokenIterator(tokens.iterator());
        Parser parser = new Parser();
        ASTNode node = parser.parse(tokenIterator);
        ToStringASTVisitor visitor = new ToStringASTVisitor();
        node.postVisit(visitor);
        assert "updateByUserTypeIsAndUserNameIs".equals(visitor.toString());
    }

    @Test
    public void test7() {
        String expr = "findByUsernameAndPasswordOrderByCreatedTimeDesc";
        Lexer lexer = new Lexer.Builder().build();
        List<Token> tokens = lexer.analyse(expr);
        TokenIterator tokenIterator = new TokenIterator(tokens.iterator());
        Parser parser = new Parser();
        ASTNode node = parser.parse(tokenIterator);
        ToStringASTVisitor visitor = new ToStringASTVisitor();
        node.postVisit(visitor);
        assert "findByUsernameIsAndPasswordIsOrderByCreatedTimeDesc".equals(visitor.toString());
    }

    @Test
    public void test8() {
        String expr = "findByAfter";
        Lexer lexer = new Lexer.Builder().build();
        List<Token> tokens = lexer.analyse(expr);
        TokenIterator tokenIterator = new TokenIterator(tokens.iterator());
        Parser parser = new Parser();
        ASTNode node = parser.parse(tokenIterator);
        ToStringASTVisitor visitor = new ToStringASTVisitor();
        node.postVisit(visitor);
        assert "findByAfterIs".equals(visitor.toString());
    }

    @Test
    public void test9() {
        String expr = "findByUsernameAndPasswordOrderByCreatedTime";
        Lexer lexer = new Lexer.Builder().build();
        List<Token> tokens = lexer.analyse(expr);
        TokenIterator tokenIterator = new TokenIterator(tokens.iterator());
        Parser parser = new Parser();
        ASTNode node = parser.parse(tokenIterator);
        ToStringASTVisitor visitor = new ToStringASTVisitor();
        node.postVisit(visitor);
        assert "findByUsernameIsAndPasswordIsOrderByCreatedTimeAsc".equals(visitor.toString());
    }

    @Test
    public void test10() {
        String expr = "findByUsernameAndPasswordAndUserTypeBeforeOrderByCreatedTime";
        Lexer lexer = new Lexer.Builder().build();
        List<Token> tokens = lexer.analyse(expr);
        TokenIterator tokenIterator = new TokenIterator(tokens.iterator());
        Parser parser = new Parser();
        ASTNode node = parser.parse(tokenIterator);
        ToStringASTVisitor visitor = new ToStringASTVisitor();
        node.postVisit(visitor);
        assert "findByUsernameIsAndPasswordIsAndUserTypeBeforeOrderByCreatedTimeAsc".equals(visitor.toString());
    }

    @Test
    public void test11() {
        String expr = "insertAllSelective";
        Lexer lexer = new Lexer.Builder().build();
        List<Token> tokens = lexer.analyse(expr);
        TokenIterator tokenIterator = new TokenIterator(tokens.iterator());
        Parser parser = new Parser();
        ASTNode node = parser.parse(tokenIterator);
        ToStringASTVisitor visitor = new ToStringASTVisitor();
        node.postVisit(visitor);
        assert "insertAllSelective".equals(visitor.toString());
    }

    @Test
    public void test12() {
        String expr = "insertSelective";
        Lexer lexer = new Lexer.Builder().build();
        List<Token> tokens = lexer.analyse(expr);
        TokenIterator tokenIterator = new TokenIterator(tokens.iterator());
        Parser parser = new Parser();
        ASTNode node = parser.parse(tokenIterator);
        ToStringASTVisitor visitor = new ToStringASTVisitor();
        node.postVisit(visitor);
        assert "insertSelective".equals(visitor.toString());
    }

    @Test
    public void test13() {
        String expr = "updateByIdSelective";
        Lexer lexer = new Lexer.Builder().build();
        List<Token> tokens = lexer.analyse(expr);
        TokenIterator tokenIterator = new TokenIterator(tokens.iterator());
        Parser parser = new Parser();
        ASTNode node = parser.parse(tokenIterator);
        ToStringASTVisitor visitor = new ToStringASTVisitor();
        node.postVisit(visitor);
        assert "updateByIdIsSelective".equals(visitor.toString());
    }

    @Test
    public void test14() {
        String expr = "updateByIdAndNameBeforeSelective";
        Lexer lexer = new Lexer.Builder().build();
        List<Token> tokens = lexer.analyse(expr);
        TokenIterator tokenIterator = new TokenIterator(tokens.iterator());
        Parser parser = new Parser();
        ASTNode node = parser.parse(tokenIterator);
        ToStringASTVisitor visitor = new ToStringASTVisitor();
        node.postVisit(visitor);
        assert "updateByIdIsAndNameBeforeSelective".equals(visitor.toString());
    }

}
