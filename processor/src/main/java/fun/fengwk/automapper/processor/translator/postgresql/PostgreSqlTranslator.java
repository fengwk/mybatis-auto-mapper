package fun.fengwk.automapper.processor.translator.postgresql;

import fun.fengwk.automapper.annotation.DBType;
import fun.fengwk.automapper.processor.lexer.Keyword;
import fun.fengwk.automapper.processor.parser.ast.Insert;
import fun.fengwk.automapper.processor.translator.BeanField;
import fun.fengwk.automapper.processor.translator.DetectEntityBeanField;
import fun.fengwk.automapper.processor.translator.DetectEntityParam;
import fun.fengwk.automapper.processor.translator.Param;
import fun.fengwk.automapper.processor.translator.Sql92Translator;
import fun.fengwk.automapper.processor.translator.TranslateContext;
import fun.fengwk.automapper.processor.translator.TranslateException;
import org.w3c.dom.Element;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author fengwk
 */
public class PostgreSqlTranslator extends Sql92Translator {

    public PostgreSqlTranslator(TranslateContext translateContext) {
        super(translateContext);
    }

    @Override
    protected Map<Keyword, ByTranslator> buildByTranslatorMap() {
        Map<Keyword, ByTranslator> byTranslatorMap = super.buildByTranslatorMap();
        byTranslatorMap.put(Keyword.STARTING_WITH, (nameEntry, addElement, addTextNode, isSingleParam, indent) ->
            addTextNode.accept(String.format("%s like #{%s} || '%%'", nameEntry.getFieldName(), nameEntry.getName())));
        byTranslatorMap.put(Keyword.ENDING_WITH, (nameEntry, addElement, addTextNode, isSingleParam, indent) ->
            addTextNode.accept(String.format("%s like '%%' || #{%s}", nameEntry.getFieldName(), nameEntry.getName())));
        byTranslatorMap.put(Keyword.CONTAINING, (nameEntry, addElement, addTextNode, isSingleParam, indent) ->
            addTextNode.accept(String.format("%s like '%%' || #{%s} || '%%'", nameEntry.getFieldName(), nameEntry.getName())));
        return byTranslatorMap;
    }

    @Override
    protected String quoteIdentifier(String identifier) {
        String quote = resolveIdentifierQuote(DBType.POSTGRESQL);
        return quote + identifier + quote;
    }

    @Override
    protected void translateInsertAllSelective(Insert insert, String methodName, Param param) {
        if (!param.isIterable()) {
            throw new TranslateException("%s should have iterable param", methodName);
        }
        if (!param.isJavaBean()) {
            throw new TranslateException("%s should have java bean param", methodName);
        }

        List<BeanField> insertFields = param.getBeanFields().stream()
            .filter(bf -> !bf.isUseGeneratedKeys())
            .collect(Collectors.toList());
        if (insertFields.isEmpty()) {
            throw new TranslateException("%s should have insert field", methodName);
        }

        StmtElement insertStmtElement = addInsertElement(methodName, param.getType(), param.findUseGeneratedKeysField());
        Element insertElement = insertStmtElement.getElement();

        addTextNode(insertElement,
            LF,
            INDENT,
            "insert into ",
            tableName,
            " (",
            insertFields.stream().map(BeanField::getFieldName).collect(Collectors.joining(", ")),
            ") values",
            LF,
            INDENT);

        Element foreachElement = addElement(insertElement, "foreach");
        boolean isDetectEntityParam = param instanceof DetectEntityParam;
        foreachElement.setAttribute("collection", isDetectEntityParam ? param.getName() : "collection");
        foreachElement.setAttribute("item", "item");
        foreachElement.setAttribute("separator", ",");

        addTextNode(foreachElement, LF, INDENT, INDENT, "(");
        for (int i = 0; i < insertFields.size(); i++) {
            BeanField insertField = insertFields.get(i);
            if (i > 0) {
                addTextNode(foreachElement, ", ");
            }

            String collectionItemName;
            String collectionItemVariableName;
            if (insertField instanceof DetectEntityBeanField) {
                DetectEntityBeanField detectEntityBeanField = (DetectEntityBeanField) insertField;
                collectionItemName = detectEntityBeanField.getCollectionItemName();
                collectionItemVariableName = detectEntityBeanField.getCollectionItemVariableName();
            } else {
                collectionItemName = insertField.getName();
                collectionItemVariableName = insertField.getVariableName();
            }

            Element chooseElement = addElement(foreachElement, "choose");
            Element whenElement = addElement(chooseElement, "when");
            whenElement.setAttribute("test", String.format("item.%s != null", collectionItemName));
            addTextNode(whenElement, "#{item.", collectionItemVariableName, "}");
            Element otherwiseElement = addElement(chooseElement, "otherwise");
            addTextNode(otherwiseElement, "default");
        }
        addTextNode(foreachElement, ")", LF, INDENT);

        postProcessInsert(insert, insertElement, param);

        insertStmtElement.append();
    }

}
