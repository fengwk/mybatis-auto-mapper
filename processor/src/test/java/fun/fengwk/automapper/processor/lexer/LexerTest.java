package fun.fengwk.automapper.processor.lexer;

import org.junit.Test;

import java.util.List;

/**
 * @author fengwk
 */
public class LexerTest {

    @Test
    public void test1() {
        Lexer lexer = new Lexer();
        List<Token> tokens = lexer.analyse("insert");
        assert tokens.get(0).equals(new Token(TokenType.KEYWORD, "insert"));
    }

    @Test
    public void test2() {
        Lexer lexer = new Lexer();
        List<Token> tokens = lexer.analyse("insertAll");
        assert tokens.get(0).equals(new Token(TokenType.KEYWORD, "insert"));
        assert tokens.get(1).equals(new Token(TokenType.KEYWORD, "All"));
    }

    @Test(expected = LexicalException.class)
    public void test3() {
        Lexer lexer = new Lexer();
        lexer.analyse("insertEntity");
    }

    @Test(expected = LexicalException.class)
    public void test4() {
        Lexer lexer = new Lexer();
        lexer.analyse("insertAllEntity");
    }

    @Test(expected = LexicalException.class)
    public void test5() {
        Lexer lexer = new Lexer();
        lexer.analyse("insertEntityAll");
    }

    @Test
    public void test6() {
        Lexer lexer = new Lexer();
        List<Token> tokens = lexer.analyse("deleteByUserTypeAndUserName");
        assert tokens.get(0).equals(new Token(TokenType.KEYWORD, "delete"));
        assert tokens.get(1).equals(new Token(TokenType.KEYWORD, "By"));
        assert tokens.get(2).equals(new Token(TokenType.VARIABLE, "UserType"));
        assert tokens.get(3).equals(new Token(TokenType.KEYWORD, "And"));
        assert tokens.get(4).equals(new Token(TokenType.VARIABLE, "UserName"));
    }

    @Test
    public void test7() {
        Lexer lexer = new Lexer();
        List<Token> tokens = lexer.analyse("deleteAll");
        assert tokens.get(0).equals(new Token(TokenType.KEYWORD, "delete"));
        assert tokens.get(1).equals(new Token(TokenType.KEYWORD, "All"));
    }

    @Test(expected = LexicalException.class)
    public void test8() {
        Lexer lexer = new Lexer();
        lexer.analyse("deleteOrderBy");
    }

    @Test
    public void test9() {
        Lexer lexer = new Lexer();
        List<Token> tokens = lexer.analyse("deleteByIdOrderByName");
        assert tokens.get(0).equals(new Token(TokenType.KEYWORD, "delete"));
        assert tokens.get(1).equals(new Token(TokenType.KEYWORD, "By"));
        assert tokens.get(2).equals(new Token(TokenType.VARIABLE, "Id"));
        assert tokens.get(3).equals(new Token(TokenType.KEYWORD, "Or"));
        assert tokens.get(4).equals(new Token(TokenType.VARIABLE, "derByName"));
    }

    @Test
    public void test10() {
        Lexer lexer = new Lexer();
        List<Token> tokens = lexer.analyse("updateByUserTypeAndUserName");
        assert tokens.get(0).equals(new Token(TokenType.KEYWORD, "update"));
        assert tokens.get(1).equals(new Token(TokenType.KEYWORD, "By"));
        assert tokens.get(2).equals(new Token(TokenType.VARIABLE, "UserType"));
        assert tokens.get(3).equals(new Token(TokenType.KEYWORD, "And"));
        assert tokens.get(4).equals(new Token(TokenType.VARIABLE, "UserName"));
    }

    @Test(expected = LexicalException.class)
    public void test11() {
        Lexer lexer = new Lexer();
        lexer.analyse("updateAll");
    }

    @Test
    public void test12() {
        Lexer lexer = new Lexer();
        List<Token> tokens = lexer.analyse("findByUsernameAndPasswordOrderByCreatedTimeDesc");
        assert tokens.get(0).equals(new Token(TokenType.KEYWORD, "find"));
        assert tokens.get(1).equals(new Token(TokenType.KEYWORD, "By"));
        assert tokens.get(2).equals(new Token(TokenType.VARIABLE, "Username"));
        assert tokens.get(3).equals(new Token(TokenType.KEYWORD, "And"));
        assert tokens.get(4).equals(new Token(TokenType.VARIABLE, "Password"));
        assert tokens.get(5).equals(new Token(TokenType.KEYWORD, "OrderBy"));
        assert tokens.get(6).equals(new Token(TokenType.VARIABLE, "CreatedTime"));
        assert tokens.get(7).equals(new Token(TokenType.KEYWORD, "Desc"));
    }

    @Test
    public void test13() {
        Lexer lexer = new Lexer();
        List<Token> tokens = lexer.analyse("findByAfter");
        assert tokens.get(0).equals(new Token(TokenType.KEYWORD, "find"));
        assert tokens.get(1).equals(new Token(TokenType.KEYWORD, "By"));
        assert tokens.get(2).equals(new Token(TokenType.VARIABLE, "After"));
    }

    @Test
    public void test14() {
        Lexer lexer = new Lexer();
        List<Token> tokens = lexer.analyse("findByUsernameAndPasswordOrderByCreatedTime");
        assert tokens.get(0).equals(new Token(TokenType.KEYWORD, "find"));
        assert tokens.get(1).equals(new Token(TokenType.KEYWORD, "By"));
        assert tokens.get(2).equals(new Token(TokenType.VARIABLE, "Username"));
        assert tokens.get(3).equals(new Token(TokenType.KEYWORD, "And"));
        assert tokens.get(4).equals(new Token(TokenType.VARIABLE, "Password"));
        assert tokens.get(5).equals(new Token(TokenType.KEYWORD, "OrderBy"));
        assert tokens.get(6).equals(new Token(TokenType.VARIABLE, "CreatedTime"));
    }

}
