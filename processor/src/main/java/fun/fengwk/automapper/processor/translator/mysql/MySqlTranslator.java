package fun.fengwk.automapper.processor.translator.mysql;

import fun.fengwk.automapper.processor.lexer.DerivedToken;
import fun.fengwk.automapper.processor.lexer.Keyword;
import fun.fengwk.automapper.processor.lexer.Lexer;
import fun.fengwk.automapper.processor.lexer.Token;
import fun.fengwk.automapper.processor.parser.ast.Find;
import fun.fengwk.automapper.processor.parser.ast.Insert;
import fun.fengwk.automapper.processor.translator.BeanField;
import fun.fengwk.automapper.processor.translator.Param;
import fun.fengwk.automapper.processor.translator.Sql92Translator;
import fun.fengwk.automapper.processor.translator.TranslateContext;
import org.w3c.dom.Element;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author fengwk
 */
public class MySqlTranslator extends Sql92Translator {

    /* mysql语法衍生 */
    private static final String DERIVED_INSERT_IGNORE = "insertIgnore";
    private static final String DERIVED_REPLACE = "replace";
    private static final String DERIVED_INSERT_ON_DUPLICATE_KEY_UPDATE = "insertOnDuplicateKeyUpdate";

    private static final String DERIVED_FIND_LOCK_IN_SHARE_MODE = "findLockInShareMode";
    private static final String DERIVED_FIND_FOR_UPDATE = "findForUpdate";

    public MySqlTranslator(TranslateContext translateContext) {
        super(translateContext);
    }

    @Override
    protected void configureLexer(Lexer.Builder lexerBuilder) {
        super.configureLexer(lexerBuilder);
        lexerBuilder
            .deriveInsert(DERIVED_INSERT_IGNORE)
            .deriveInsert(DERIVED_REPLACE)
            .deriveInsert(DERIVED_INSERT_ON_DUPLICATE_KEY_UPDATE)
            .deriveFind(DERIVED_FIND_LOCK_IN_SHARE_MODE)
            .deriveFind(DERIVED_FIND_FOR_UPDATE);
    }

    @Override
    protected Map<Keyword, Sql92Translator.ByTranslator> buildByTranslatorMap() {
        Map<Keyword, Sql92Translator.ByTranslator> byTranslatorMap = super.buildByTranslatorMap();
        byTranslatorMap.put(Keyword.STARTING_WITH, (nameEntry, addElement, addTextNode, isSingleParam, indent) -> addTextNode.accept(String.format("%s like concat(#{%s}, '%%')", nameEntry.getFieldName(), nameEntry.getName())));
        byTranslatorMap.put(Keyword.ENDING_WITH, (nameEntry, addElement, addTextNode, isSingleParam, indent) -> addTextNode.accept(String.format("%s like concat('%%', #{%s})", nameEntry.getFieldName(), nameEntry.getName())));
        byTranslatorMap.put(Keyword.CONTAINING, (nameEntry, addElement, addTextNode, isSingleParam, indent) -> addTextNode.accept(String.format("%s like concat('%%', #{%s}, '%%')", nameEntry.getFieldName(), nameEntry.getName())));
        return byTranslatorMap;
    }

    @Override
    protected void postProcessInsert(Insert insert, Element insertElement, Param param) {
        super.postProcessInsert(insert, insertElement, param);
        Token lexeme = insert.getLexeme();
        if (lexeme instanceof DerivedToken) {
            String derivedValue = ((DerivedToken) lexeme).getDerivedValue();
            if (Objects.equals(derivedValue, DERIVED_INSERT_IGNORE)) {
                insertElement.getFirstChild().setTextContent(
                        insertElement.getFirstChild().getTextContent().replace("insert into", "insert ignore into"));
            } else if (Objects.equals(derivedValue, DERIVED_REPLACE)) {
                insertElement.getFirstChild().setTextContent(
                        insertElement.getFirstChild().getTextContent().replace("insert into", "replace into"));
            } else if (Objects.equals(derivedValue, DERIVED_INSERT_ON_DUPLICATE_KEY_UPDATE)) {
                if (isSelective(insert)) {
                    addTextNode(insertElement, INDENT);
                    Element trimElement = addElement(insertElement, "trim");
                    trimElement.setAttribute("prefix", "on duplicate key update ");
                    trimElement.setAttribute("suffixOverrides", ",");
                    for (BeanField bf : param.getBeanFields()) {
                        if (!bf.isUseGeneratedKeys() && !bf.isOnDuplicateKeyUpdateIgnore()) {
                            addTextNode(trimElement, LF, INDENT, INDENT);
                            Element ifElement = addElement(trimElement, "if");
                            ifElement.setAttribute("test", String.format("%s != null", bf.getName()));
                            addTextNode(ifElement, String.format("%s=#{%s},", bf.getFieldName(), bf.getVariableName()));
                        }
                    }
                    addTextNode(trimElement, LF, INDENT);
                    addTextNode(insertElement, LF);
                } else {
                    addTextNode(insertElement, INDENT,  "on duplicate key update ",
                        param.getBeanFields().stream()
                            .filter(bf -> !bf.isUseGeneratedKeys() && !bf.isOnDuplicateKeyUpdateIgnore())
                            .map(bf -> String.format("%s=#{%s}", bf.getFieldName(), bf.getVariableName()))
                            .collect(Collectors.joining(", ")),
                        LF, INDENT);
                }
            }
        }
    }

    @Override
    protected void postProcessFind(Find find, Element selectElement) {
        super.postProcessFind(find, selectElement);
        Token lexeme = find.getLexeme();
        if (lexeme instanceof DerivedToken) {
            String derivedValue = ((DerivedToken) lexeme).getDerivedValue();
            if (Objects.equals(derivedValue, DERIVED_FIND_LOCK_IN_SHARE_MODE)) {
                addTextNode(selectElement, INDENT, "lock in share mode", LF);
            } else if (Objects.equals(derivedValue, DERIVED_FIND_FOR_UPDATE)) {
                addTextNode(selectElement, INDENT, "for update", LF);
            }
        }
    }

}
