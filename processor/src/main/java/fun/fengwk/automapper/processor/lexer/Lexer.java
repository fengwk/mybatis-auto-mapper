package fun.fengwk.automapper.processor.lexer;

import fun.fengwk.automapper.processor.util.StringCharSequenceView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author fengwk
 */
public class Lexer {

    private static final char EOF = 0;

    private static final Pattern PATTERN_TERM_AND = Pattern.compile("^([_0-9a-zA-Z]+?)" + Keyword.AND.getValue());
    private static final Pattern PATTERN_TERM_AND_OR = Pattern.compile("^([_0-9a-zA-Z]+?)(" + Keyword.AND.getValue() + "|" + Keyword.OR.getValue() + ")");
    private static final Pattern PATTERN_TERM_ORDER_BY_AND_OR = Pattern.compile("^([_0-9a-zA-Z]+?)(" + Keyword.ORDER_BY.getValue() + "|" + Keyword.AND.getValue() + "|" + Keyword.OR.getValue() + ")");
    private static final Pattern PATTERN_TERM_EOF = Pattern.compile("^([_0-9a-zA-Z]+?)" + EOF);

    private static final Keyword[] BY_KEYWORDS = sortByValueLengthDesc(Keyword.getByKeywords());
    private static final Keyword[] ORDER_BY_KEYWORDS = sortByValueLengthDesc(Keyword.getOrderByKeywords());

    private String expression;
    private int offset;
    private List<Token> tokens;

    // 从长到短排序是为了防止歧义
    private static Keyword[] sortByValueLengthDesc(Keyword[] keywords) {
        Arrays.sort(keywords, Comparator.comparing((Keyword kw) -> kw.getValue().length()).reversed());
        return keywords;
    }

    public List<Token> analyse(String expression) {
        // reset
        this.expression = appendEOF(expression);
        this.offset = 0;
        this.tokens = new ArrayList<>();

        return doAnalyse();
    }

    private List<Token> doAnalyse() {
        int state = 0;
        while (offset < expression.length()) {
            Keyword eaten;
            switch (state) {
                case 0:
                    eaten = eatKeyword(new Keyword[] {
                            Keyword.INSERT, Keyword.DELETE, Keyword.UPDATE, Keyword.FIND, Keyword.COUNT, Keyword.PAGE });

                    if (eaten == null) {
                        throw new LexicalException("Expressions must begin with %s|%s|%s|%s|%s|%s",
                                Keyword.INSERT.getValue(), Keyword.DELETE.getValue(), Keyword.UPDATE.getValue(),
                                Keyword.FIND.getValue(), Keyword.COUNT.getValue(), Keyword.PAGE.getValue());
                    }

                    if (eaten == Keyword.INSERT) {
                        state = 1;
                    } else if (eaten == Keyword.DELETE) {
                        state = 2;
                    } else if (eaten == Keyword.UPDATE) {
                        state = 3;
                    } else if (eaten == Keyword.FIND) {
                        state = 4;
                    } else if (eaten == Keyword.COUNT) {
                        state = 5;
                    } else {// eatResult.eaten == Keyword.PAGE
                        state = 6;
                    }

                    break;

                case 1:
                    if (!eatEOF()) {
                        eaten = eatKeyword(Keyword.ALL);

                        if (eaten == null) {
                            throw new LexicalException("Expression failed to parse after %s",
                                    tokens.get(tokens.size() - 1).getValue());
                        }

                        if (!eatEOF()) {
                            throw new LexicalException("Expressions should end after %s",
                                    Keyword.ALL.getValue());
                        }
                    }
                    break;

                case 2:
                    eaten = eatKeyword(new Keyword[] { Keyword.ALL, Keyword.BY });

                    if (eaten == null) {
                        throw new LexicalException("Expression failed to parse after %s",
                                tokens.get(tokens.size() - 1).getValue());
                    }

                    if (eaten == Keyword.ALL) {
                        if (!eatEOF()) {
                            throw new LexicalException("Expressions should end after %s",
                                    Keyword.ALL.getValue());
                        }
                    } else {// eaten == Keyword.BY
                        state = 7;
                    }
                    break;

                case 3:
                    eaten = eatKeyword(Keyword.BY);

                    if (eaten == null) {
                        throw new LexicalException("Expression failed to parse after %s",
                                tokens.get(tokens.size() - 1).getValue());
                    }

                    state = 7;
                    break;

                case 4:
                    eaten = eatKeyword(new Keyword[] { Keyword.ALL, Keyword.BY });

                    if (eaten == null) {
                        throw new LexicalException("Expression failed to parse after %s",
                                tokens.get(tokens.size() - 1).getValue());
                    }

                    if (eaten == Keyword.ALL) {
                        state = 10;
                    } else {// eaten == Keyword.BY
                        state = 8;
                    }
                    break;

                case 5:
                    eaten = eatKeyword(new Keyword[] { Keyword.ALL, Keyword.BY });

                    if (eaten == null) {
                        throw new LexicalException("Expression failed to parse after %s",
                                tokens.get(tokens.size() - 1).getValue());
                    }

                    if (eaten == Keyword.ALL) {
                        if (!eatEOF()) {
                            throw new LexicalException("Expressions should end after %s",
                                    Keyword.ALL.getValue());
                        }
                    } else {// eaten == Keyword.BY
                        state = 7;
                    }
                    break;

                case 6:
                    eaten = eatKeyword(new Keyword[] { Keyword.ALL, Keyword.BY });

                    if (eaten == null) {
                        throw new LexicalException("Expression failed to parse after %s",
                                tokens.get(tokens.size() - 1).getValue());
                    }

                    if (eaten == Keyword.ALL) {
                        state = 10;
                    } else if (eaten == Keyword.BY) {// eaten == Keyword.BY
                        state = 8;
                    }
                    break;

                case 7:
                    if (eatTermAndOr(this::parseByTerm)) {
                        break;
                    }

                    if (eatTermEOF(this::parseByTerm)) {
                        // is end
                        break;
                    }

                    throw new LexicalException("Expression failed to parse after %s",
                            tokens.get(tokens.size() - 1).getValue());

                case 8:
                    if (eatTermOrderByAndOr(this::parseByTerm)) {
                        if (tokens.get(tokens.size() - 1).isKeyword(Keyword.ORDER_BY)) {
                            state = 9;
                        }
                        break;
                    }

                    if (eatTermEOF(this::parseByTerm)) {
                        // is end
                        break;
                    }

                    throw new LexicalException("Expression failed to parse after %s",
                            tokens.get(tokens.size() - 1).getValue());

                case 9:
                    if (eatTermAnd(this::parseOrderByTerm)) {
                        break;
                    }

                    if (eatTermEOF(this::parseOrderByTerm)) {
                        // is end
                        break;
                    }

                    throw new LexicalException("Expression failed to parse after %s",
                            tokens.get(tokens.size() - 1).getValue());

                case 10:
                    if (!eatEOF()) {
                        eaten = eatKeyword(Keyword.ORDER_BY);

                        if (eaten == null) {
                            throw new LexicalException("Expression failed to parse after %s",
                                    tokens.get(tokens.size() - 1).getValue());
                        }

                        state = 9;
                    }
                    break;

                default:
                    throw new AssertionError();
            }
        }

        return tokens;
    }

    private String appendEOF(String str) {
        return str + EOF;
    }

    public boolean eatEOF() {
        if (expression.charAt(offset) == EOF) {
            offset++;
            return true;
        }
        return false;
    }

    private Keyword eatKeyword(Keyword[] starts) {
        for (Keyword start : starts) {
            Keyword eaten = eatKeyword(start);
            if (eaten != null) {
                return eaten;
            }
        }
        return null;
    }

    private Keyword eatKeyword(Keyword start) {
        String startValue = start.getValue();
        if (expression.startsWith(startValue, offset)) {
            offset += startValue.length();
            tokens.add(new Token(TokenType.KEYWORD, startValue));
            return start;
        }
        return null;
    }

    private boolean eatTermOrderByAndOr(Consumer<String> termParser) {
        CharSequence expression = new StringCharSequenceView(this.expression, offset);
        Matcher m = PATTERN_TERM_ORDER_BY_AND_OR.matcher(expression);
        if (m.find()) {
            Keyword kw = Keyword.of(m.group(2));
            if (kw == null || (kw != Keyword.ORDER_BY && kw != Keyword.AND && kw != Keyword.OR)) {
                return false;
            }

            termParser.accept(m.group(1));
            tokens.add(new Token(TokenType.KEYWORD, kw.getValue()));
            offset += m.group().length();
            return true;
        }
        return false;
    }

    private boolean eatTermAndOr(Consumer<String> termParser) {
        CharSequence expression = new StringCharSequenceView(this.expression, offset);
        Matcher m = PATTERN_TERM_AND_OR.matcher(expression);
        if (m.find()) {
            Keyword kw = Keyword.of(m.group(2));
            if (kw == null || (kw != Keyword.ORDER_BY && kw != Keyword.AND && kw != Keyword.OR)) {
                return false;
            }

            termParser.accept(m.group(1));
            tokens.add(new Token(TokenType.KEYWORD, kw.getValue()));
            offset += m.group().length();
            return true;
        }
        return false;
    }

    private boolean eatTermAnd(Consumer<String> termParser) {
        CharSequence expression = new StringCharSequenceView(this.expression, offset);
        Matcher m = PATTERN_TERM_AND.matcher(expression);
        if (m.find()) {
            termParser.accept(m.group(1));
            tokens.add(new Token(TokenType.KEYWORD, Keyword.AND.getValue()));
            offset += m.group().length();
            return true;
        }
        return false;
    }

    private boolean eatTermEOF(Consumer<String> termParser) {
        CharSequence expression = new StringCharSequenceView(this.expression, offset);
        Matcher m = PATTERN_TERM_EOF.matcher(expression);
        if (m.find()) {
            termParser.accept(m.group(1));
            offset += m.group().length();
            return true;
        }
        return false;
    }

    private void parseByTerm(String term) {
        for (Keyword keyword : BY_KEYWORDS) {
            String keywordValue = keyword.getValue();
            if (term.length() > keywordValue.length() && term.endsWith(keywordValue)) {
                tokens.add(new Token(TokenType.VARIABLE, term.substring(0, term.length() - keywordValue.length())));
                tokens.add(new Token(TokenType.KEYWORD, keywordValue));
                return;
            }
        }
        tokens.add(new Token(TokenType.VARIABLE, term));
    }

    private void parseOrderByTerm(String term) {
        for (Keyword keyword : ORDER_BY_KEYWORDS) {
            String keywordValue = keyword.getValue();
            if (term.length() > keywordValue.length() && term.endsWith(keywordValue)) {
                tokens.add(new Token(TokenType.VARIABLE, term.substring(0, term.length() - keywordValue.length())));
                tokens.add(new Token(TokenType.KEYWORD, keywordValue));
                return;
            }
        }
        tokens.add(new Token(TokenType.VARIABLE, term));
    }

}
