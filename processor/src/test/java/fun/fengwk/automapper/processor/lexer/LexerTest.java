package fun.fengwk.automapper.processor.lexer;

import org.junit.Test;

import java.util.List;

/**
 * @author fengwk
 */
public class LexerTest {

    @Test
    public void test1() {
        Lexer lexer = new Lexer.Builder().build();
        List<Token> tokens = lexer.analyse("insert");
        assert tokens.get(0).equals(new DerivedToken(TokenType.KEYWORD, "insert", "insert"));
    }

    @Test
    public void test2() {
        Lexer lexer = new Lexer.Builder().build();
        List<Token> tokens = lexer.analyse("insertAll");
        assert tokens.get(0).equals(new DerivedToken(TokenType.KEYWORD, "insert", "insert"));
        assert tokens.get(1).equals(new Token(TokenType.KEYWORD, "All"));
    }

    @Test(expected = LexicalException.class)
    public void test3() {
        Lexer lexer = new Lexer.Builder().build();
        lexer.analyse("insertEntity");
    }

    @Test(expected = LexicalException.class)
    public void test4() {
        Lexer lexer = new Lexer.Builder().build();
        List<Token> insertAllEntity = lexer.analyse("insertAllEntity");
        System.out.println(insertAllEntity);
    }

    @Test(expected = LexicalException.class)
    public void test5() {
        Lexer lexer = new Lexer.Builder().build();
        lexer.analyse("insertEntityAll");
    }

    @Test
    public void test6() {
        Lexer lexer = new Lexer.Builder().build();
        List<Token> tokens = lexer.analyse("deleteByUserTypeAndUserName");
        assert tokens.get(0).equals(new DerivedToken(TokenType.KEYWORD, "delete", "delete"));
        assert tokens.get(1).equals(new Token(TokenType.KEYWORD, "By"));
        assert tokens.get(2).equals(new Token(TokenType.VARIABLE, "UserType"));
        assert tokens.get(3).equals(new Token(TokenType.KEYWORD, "And"));
        assert tokens.get(4).equals(new Token(TokenType.VARIABLE, "UserName"));
    }

    @Test
    public void test7() {
        Lexer lexer = new Lexer.Builder().build();
        List<Token> tokens = lexer.analyse("deleteAll");
        assert tokens.get(0).equals(new DerivedToken(TokenType.KEYWORD, "delete", "delete"));
        assert tokens.get(1).equals(new Token(TokenType.KEYWORD, "All"));
    }

    @Test(expected = LexicalException.class)
    public void test8() {
        Lexer lexer = new Lexer.Builder().build();
        lexer.analyse("deleteOrderBy");
    }

    @Test
    public void test9() {
        Lexer lexer = new Lexer.Builder().build();
        List<Token> tokens = lexer.analyse("deleteByIdOrderByName");
        assert tokens.get(0).equals(new DerivedToken(TokenType.KEYWORD, "delete", "delete"));
        assert tokens.get(1).equals(new Token(TokenType.KEYWORD, "By"));
        assert tokens.get(2).equals(new Token(TokenType.VARIABLE, "Id"));
        assert tokens.get(3).equals(new Token(TokenType.KEYWORD, "Or"));
        assert tokens.get(4).equals(new Token(TokenType.VARIABLE, "derByName"));
    }

    @Test
    public void test10() {
        Lexer lexer = new Lexer.Builder().build();
        List<Token> tokens = lexer.analyse("updateByUserTypeAndUserName");
        assert tokens.get(0).equals(new DerivedToken(TokenType.KEYWORD, "update", "update"));
        assert tokens.get(1).equals(new Token(TokenType.KEYWORD, "By"));
        assert tokens.get(2).equals(new Token(TokenType.VARIABLE, "UserType"));
        assert tokens.get(3).equals(new Token(TokenType.KEYWORD, "And"));
        assert tokens.get(4).equals(new Token(TokenType.VARIABLE, "UserName"));
    }

    @Test(expected = LexicalException.class)
    public void test11() {
        Lexer lexer = new Lexer.Builder().build();
        lexer.analyse("updateAll");
    }

    @Test
    public void test12() {
        Lexer lexer = new Lexer.Builder().build();
        List<Token> tokens = lexer.analyse("findByUsernameAndPasswordOrderByCreatedTimeDesc");
        assert tokens.get(0).equals(new DerivedToken(TokenType.KEYWORD, "find", "find"));
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
        Lexer lexer = new Lexer.Builder().build();
        List<Token> tokens = lexer.analyse("findIdByUsernameAndPasswordOrderByCreatedTimeDesc");
        assert tokens.get(0).equals(new DerivedToken(TokenType.KEYWORD, "find", "find"));
        assert tokens.get(1).equals(new Token(TokenType.KEYWORD, "By"));
        assert tokens.get(2).equals(new Token(TokenType.VARIABLE, "Username"));
        assert tokens.get(3).equals(new Token(TokenType.KEYWORD, "And"));
        assert tokens.get(4).equals(new Token(TokenType.VARIABLE, "Password"));
        assert tokens.get(5).equals(new Token(TokenType.KEYWORD, "OrderBy"));
        assert tokens.get(6).equals(new Token(TokenType.VARIABLE, "CreatedTime"));
        assert tokens.get(7).equals(new Token(TokenType.KEYWORD, "Desc"));
    }

    @Test
    public void test14() {
        Lexer lexer = new Lexer.Builder().build();
        List<Token> tokens = lexer.analyse("findByAfter");
        assert tokens.get(0).equals(new DerivedToken(TokenType.KEYWORD, "find", "find"));
        assert tokens.get(1).equals(new Token(TokenType.KEYWORD, "By"));
        assert tokens.get(2).equals(new Token(TokenType.VARIABLE, "After"));
    }

    @Test
    public void test15() {
        Lexer lexer = new Lexer.Builder().build();
        List<Token> tokens = lexer.analyse("findByUsernameAndPasswordOrderByCreatedTime");
        assert tokens.get(0).equals(new DerivedToken(TokenType.KEYWORD, "find", "find"));
        assert tokens.get(1).equals(new Token(TokenType.KEYWORD, "By"));
        assert tokens.get(2).equals(new Token(TokenType.VARIABLE, "Username"));
        assert tokens.get(3).equals(new Token(TokenType.KEYWORD, "And"));
        assert tokens.get(4).equals(new Token(TokenType.VARIABLE, "Password"));
        assert tokens.get(5).equals(new Token(TokenType.KEYWORD, "OrderBy"));
        assert tokens.get(6).equals(new Token(TokenType.VARIABLE, "CreatedTime"));
    }

    @Test
    public void test16() {
        Lexer lexer = new Lexer.Builder().deriveInsert("insertIgnore").build();
        List<Token> tokens = lexer.analyse("insertIgnore");
        assert tokens.get(0).equals(new DerivedToken(TokenType.KEYWORD, "insert", "insertIgnore"));
    }

    @Test
    public void test17() {
        Lexer lexer = new Lexer.Builder().deriveInsert("replace").build();
        List<Token> tokens = lexer.analyse("replace");
        assert tokens.get(0).equals(new DerivedToken(TokenType.KEYWORD, "insert", "replace"));
    }

    @Test
    public void test18() {
        Lexer lexer = new Lexer.Builder().deriveInsert("insertIgnore").build();
        List<Token> tokens = lexer.analyse("insertIgnoreAll");
        assert tokens.get(0).equals(new DerivedToken(TokenType.KEYWORD, "insert", "insertIgnore"));
        assert tokens.get(1).equals(new Token(TokenType.KEYWORD, "All"));
    }

}
