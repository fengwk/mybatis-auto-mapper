package fun.fengwk.automapper.processor.lexer;

import fun.fengwk.automapper.processor.util.StringCharSequenceView;

import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 词法分析器，状态转移见doc/MapperMethodStateMachine.drawio
 *
 * @author fengwk
 */
public class Lexer {

    private static final String SELECTIVE = "Selective";
    private static final char EOF = 0;
    private static final String SELECTIVE_EOF = SELECTIVE + EOF;

    private static final Map<Keyword, Pattern> PATTERN_TERM_KEYWORD_MAP = new HashMap<>();
    private static final Pattern PATTERN_TERM_AND = Pattern.compile("^([_0-9a-zA-Z]+?)" + Keyword.AND.getValue());
    private static final Pattern PATTERN_TERM_AND_OR = Pattern.compile("^([_0-9a-zA-Z]+?)(" + Keyword.AND.getValue() + "|" + Keyword.OR.getValue() + ")");
    private static final Pattern PATTERN_TERM_ORDER_BY_AND_OR = Pattern.compile("^([_0-9a-zA-Z]+?)(" + Keyword.ORDER_BY.getValue() + "|" + Keyword.AND.getValue() + "|" + Keyword.OR.getValue() + ")");

    private static final Pattern PATTERN_TERM_EOF = Pattern.compile("^([_0-9a-zA-Z]+?)" + EOF);
    private static final Pattern PATTERN_TERM_SELECTIVE_EOF = Pattern.compile("^([_0-9a-zA-Z]+?)" + SELECTIVE_EOF);

    private static final Keyword[] BY_KEYWORDS = sortByValueLengthDesc(Keyword.getByOps());
    private static final Keyword[] ORDER_BY_KEYWORDS = sortByValueLengthDesc(Keyword.getOrderByOps());

    /* derivedValues必须要先从长到短排序 */
    private final String[] insertDerivedValues;
    private final String[] deleteDerivedValues;
    private final String[] updateDerivedValues;
    private final String[] findDerivedValues;
    private final String[] countDerivedValues;
    private final String[] pageDerivedValues;

    private String expression;
    private int offset;
    private List<Token> tokens;

    private Lexer(String[] insertDerivedValues, String[] deleteDerivedValues, String[] updateDerivedValues,
                 String[] findDerivedValues, String[] countDerivedValues, String[] pageDerivedValues) {
        this.insertDerivedValues = insertDerivedValues;
        this.deleteDerivedValues = deleteDerivedValues;
        this.updateDerivedValues = updateDerivedValues;
        this.findDerivedValues = findDerivedValues;
        this.countDerivedValues = countDerivedValues;
        this.pageDerivedValues = pageDerivedValues;
    }

    // 从长到短排序是为了防止歧义
    private static Keyword[] sortByValueLengthDesc(Keyword[] keywords) {
        Arrays.sort(keywords, Comparator.comparing((Keyword kw) -> kw.getValue().length()).reversed());
        return keywords;
    }

    /**
     * 将表达式分解为token
     *
     * @param expression
     * @return
     */
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
            switch (state) {
                case 0:
                    if (tryEatDerivedKeyword(Keyword.INSERT, insertDerivedValues)) {
                        state = 1;
                        break;
                    }

                    if (tryEatDerivedKeyword(Keyword.DELETE, deleteDerivedValues)) {
                        state = 2;
                        break;
                    }

                    if (tryEatDerivedKeyword(Keyword.UPDATE, updateDerivedValues)) {
                        state = 3;
                        break;
                    }

                    if (tryEatDerivedKeyword(Keyword.FIND, findDerivedValues)) {
                        state = 4;
                        break;
                    }

                    if (tryEatDerivedKeyword(Keyword.COUNT, countDerivedValues)) {
                        state = 2;
                        break;
                    }

                    if (tryEatDerivedKeyword(Keyword.PAGE, pageDerivedValues)) {
                        state = 4;
                        break;
                    }

                    throw new LexicalException("Expressions must begin with %s|%s|%s|%s|%s|%s",
                            Keyword.INSERT.getValue(), Keyword.DELETE.getValue(), Keyword.UPDATE.getValue(),
                            Keyword.FIND.getValue(), Keyword.COUNT.getValue(), Keyword.PAGE.getValue());

                case 1:
                    if (tryEatSelectiveEOF()) {
                        break;
                    }

                    if (tryEatEOF()) {
                        break;
                    }

                    if (tryEatKeyword(Keyword.ALL)) {
                        state = 5;
                        break;
                    }

                    throw new LexicalException("Expression failed to parse after %s",
                            tokens.get(tokens.size() - 1).getValue());

                case 2:
                    if (tryEatKeyword(Keyword.ALL)) {
                        if (tryEatEOF()) {
                            break;
                        }
                    } else if (tryEatKeyword(Keyword.BY)) {
                        state = 6;
                        break;
                    }

                    throw new LexicalException("Expression failed to parse after %s",
                            tokens.get(tokens.size() - 1).getValue());

                case 3:
                    if (tryEatKeyword(Keyword.BY)) {
                        state = 7;
                        break;
                    }

                    throw new LexicalException("Expression failed to parse after %s",
                            tokens.get(tokens.size() - 1).getValue());

                case 4:
                    if (tryEatKeyword(Keyword.ALL)) {
                        state = 8;
                        break;
                    }

                    if (tryEatUtilKeyword(Keyword.BY)) {
                        state = 9;
                        break;
                    }

                    throw new LexicalException("Expression failed to parse after %s",
                            tokens.get(tokens.size() - 1).getValue());

                case 5:
                    if (tryEatSelectiveEOF()) {
                        break;
                    }

                    if (tryEatEOF()) {
                        break;
                    }

                    throw new LexicalException("Expression failed to parse after %s",
                            tokens.get(tokens.size() - 1).getValue());

                case 6:
                    if (tryEatTermAndOr(this::parseByTerm)) {
                        break;
                    }

                    if (tryEatTermEOF(this::parseByTerm)) {
                        break;
                    }

                    throw new LexicalException("Expression failed to parse after %s",
                            tokens.get(tokens.size() - 1).getValue());

                case 7:
                    if (tryEatTermAndOr(this::parseByTerm)) {
                        break;
                    }

                    if (tryEatTermSelectiveEOF(this::parseByTerm)) {
                        break;
                    }

                    if (tryEatTermEOF(this::parseByTerm)) {
                        break;
                    }

                    throw new LexicalException("Expression failed to parse after %s",
                            tokens.get(tokens.size() - 1).getValue());

                case 8:
                    if (tryEatEOF()) {
                        break;
                    }

                    if (tryEatKeyword(Keyword.ORDER_BY)) {
                        state = 10;
                        break;
                    }

                    throw new LexicalException("Expression failed to parse after %s",
                            tokens.get(tokens.size() - 1).getValue());

                case 9:
                    int res = tryEatTermOrderByAndOr(this::parseByTerm);
                    if (res == 1) {
                        state = 10;
                        break;
                    }
                    if (res == 2) {
                        break;
                    }

                    if (tryEatTermEOF(this::parseByTerm)) {
                        break;
                    }

                    throw new LexicalException("Expression failed to parse after %s",
                            tokens.get(tokens.size() - 1).getValue());

                case 10:
                    if (tryEatTermAnd(this::parseOrderByTerm)) {
                        break;
                    }

                    if (tryEatTermEOF(this::parseOrderByTerm)) {
                        break;
                    }

                    throw new LexicalException("Expression failed to parse after %s",
                            tokens.get(tokens.size() - 1).getValue());

                default:
                    throw new AssertionError();
            }
        }

        return tokens;
    }

    private String appendEOF(String str) {
        return str + EOF;
    }

    /**
     * 尝试吃掉一个EOF符号，成功返回true，失败返回false
     *
     * @return
     */
    public boolean tryEatEOF() {
        if (expression.charAt(offset) == EOF) {
            offset++;
            return true;
        }

        return false;
    }

    /**
     * 尝试吃掉一个SelectiveEOF，成功返回true，失败返回false
     *
     * @return
     */
    public boolean tryEatSelectiveEOF() {
        if (SELECTIVE_EOF.equals(expression.substring(offset))) {
            tokens.add(new Token(TokenType.KEYWORD, SELECTIVE));
            offset += SELECTIVE_EOF.length();
            return true;
        }

        return false;
    }

    /**
     * 尝试吃掉一个支持派生的关键码，成功返回true，失败返回false
     *
     * @param kw
     * @return
     */
    private boolean tryEatDerivedKeyword(Keyword kw, String[] derivedValues) {
        for (String derivedValue : derivedValues) {
            if (expression.startsWith(derivedValue, offset)) {
                offset += derivedValue.length();
                tokens.add(new DerivedToken(TokenType.KEYWORD, kw.getValue(), derivedValue));
                return true;
            }
        }

        return false;
    }

    /**
     * 尝试吃掉一个指定的关键码，成功返回true，失败返回false
     *
     * @param kw
     * @return
     */
    private boolean tryEatKeyword(Keyword kw) {
        String startValue = kw.getValue();
        if (expression.startsWith(startValue, offset)) {
            offset += startValue.length();
            tokens.add(new Token(TokenType.KEYWORD, startValue));
            return true;
        }

        return false;
    }

    /**
     * 尝试吃掉[_0-9a-zA-Z]+?Keyword，成功返回true，失败返回false
     *
     * @param kw
     * @return
     */
    private boolean tryEatUtilKeyword(Keyword kw) {
        Pattern pattern = PATTERN_TERM_KEYWORD_MAP.computeIfAbsent(
            kw, k -> Pattern.compile("^([_0-9a-zA-Z]*?)" + k.getValue()));
        CharSequence expression = new StringCharSequenceView(this.expression, offset);
        Matcher m = pattern.matcher(expression);
        if (m.find()) {
            offset += m.group().length();
            tokens.add(new Token(TokenType.KEYWORD, kw.getValue()));
            return true;
        }
        return false;
    }

    /**
     * 尝试吃掉[_0-9a-zA-Z]+?(OrderBy|And|Or)，成功匹配到OrderBy返回1，成功匹配到And|Or返回2，失败返回0
     *
     * @param termParser
     * @return
     */
    private int tryEatTermOrderByAndOr(Consumer<String> termParser) {
        CharSequence expression = new StringCharSequenceView(this.expression, offset);
        Matcher m = PATTERN_TERM_ORDER_BY_AND_OR.matcher(expression);
        if (m.find()) {
            termParser.accept(m.group(1));
            tokens.add(new Token(TokenType.KEYWORD, m.group(2)));
            offset += m.group().length();
            return Keyword.ORDER_BY.getValue().equals(m.group(2)) ? 1 : 2;
        }
        return 0;
    }

    /**
     * 尝试吃掉[_0-9a-zA-Z]+?(And|Or)，成功返回true，失败返回false
     *
     * @param termParser
     * @return
     */
    private boolean tryEatTermAndOr(Consumer<String> termParser) {
        CharSequence expression = new StringCharSequenceView(this.expression, offset);
        Matcher m = PATTERN_TERM_AND_OR.matcher(expression);
        if (m.find()) {
            termParser.accept(m.group(1));
            tokens.add(new Token(TokenType.KEYWORD, m.group(2)));
            offset += m.group().length();
            return true;
        }
        return false;
    }

    /**
     * 尝试吃掉[_0-9a-zA-Z]+?And，成功返回true，失败返回false
     *
     * @param termParser
     * @return
     */
    private boolean tryEatTermAnd(Consumer<String> termParser) {
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

    /**
     * 尝试吃掉[_0-9a-zA-Z]+?EOF，成功返回true，失败返回false
     *
     * @param termParser
     * @return
     */
    private boolean tryEatTermEOF(Consumer<String> termParser) {
        CharSequence expression = new StringCharSequenceView(this.expression, offset);
        Matcher m = PATTERN_TERM_EOF.matcher(expression);
        if (m.find()) {
            termParser.accept(m.group(1));
            offset += m.group().length();
            return true;
        }
        return false;
    }

    /**
     * 尝试吃掉[_0-9a-zA-Z]+?SelectiveEOF，成功返回true，失败返回false
     *
     * @param termParser
     * @return
     */
    private boolean tryEatTermSelectiveEOF(Consumer<String> termParser) {
        CharSequence expression = new StringCharSequenceView(this.expression, offset);
        Matcher m = PATTERN_TERM_SELECTIVE_EOF.matcher(expression);
        if (m.find()) {
            termParser.accept(m.group(1));
            tokens.add(new Token(TokenType.KEYWORD, SELECTIVE));
            offset += m.group().length();
            return true;
        }
        return false;
    }

    /**
     * 解析By语句后的短语
     *
     * @param term
     */
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

    /**
     * 解析OrderBy语句后的短语
     *
     * @param term
     */
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

    public static class Builder {

        private final Set<String> insertDerivedValues = new HashSet<>();
        private final Set<String> deleteDerivedValues = new HashSet<>();
        private final Set<String> updateDerivedValues = new HashSet<>();
        private final Set<String> findDerivedValues = new HashSet<>();
        private final Set<String> countDerivedValues = new HashSet<>();
        private final Set<String> pageDerivedValues = new HashSet<>();

        public Builder() {
            deriveInsert(Keyword.INSERT.getValue());
            deriveDelete(Keyword.DELETE.getValue());
            deriveUpdate(Keyword.UPDATE.getValue());
            deriveFind(Keyword.FIND.getValue());
            deriveCount(Keyword.COUNT.getValue());
            derivePage(Keyword.PAGE.getValue());
        }

        public Builder deriveInsert(String derivedValue) {
            insertDerivedValues.add(derivedValue);
            return this;
        }

        public Builder deriveDelete(String derivedValue) {
            deleteDerivedValues.add(derivedValue);
            return this;
        }

        public Builder deriveUpdate(String derivedValue) {
            updateDerivedValues.add(derivedValue);
            return this;
        }

        public Builder deriveFind(String derivedValue) {
            findDerivedValues.add(derivedValue);
            return this;
        }

        public Builder deriveCount(String derivedValue) {
            countDerivedValues.add(derivedValue);
            return this;
        }

        public Builder derivePage(String derivedValue) {
            pageDerivedValues.add(derivedValue);
            return this;
        }

        public Lexer build() {
            return new Lexer(
                    sortByLengthDesc(insertDerivedValues),
                    sortByLengthDesc(deleteDerivedValues),
                    sortByLengthDesc(updateDerivedValues),
                    sortByLengthDesc(findDerivedValues),
                    sortByLengthDesc(countDerivedValues),
                    sortByLengthDesc(pageDerivedValues));
        }

        private String[] sortByLengthDesc(Set<String> set) {
            String[] strs = set.toArray(new String[0]);
            Arrays.sort(strs, Comparator.comparing(String::length).reversed());
            return strs;
        }

    }

}
