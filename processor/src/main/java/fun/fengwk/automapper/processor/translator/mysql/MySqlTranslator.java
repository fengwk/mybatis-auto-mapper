package fun.fengwk.automapper.processor.translator.mysql;

import fun.fengwk.automapper.processor.lexer.Keyword;
import fun.fengwk.automapper.processor.lexer.Token;
import fun.fengwk.automapper.processor.parser.ast.*;
import fun.fengwk.automapper.processor.translator.*;
import fun.fengwk.automapper.processor.util.StringUtils;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author fengwk
 */
public class MySqlTranslator extends Translator {

    private static final Map<Keyword, ByTranslator> BY_TRANSLATOR_MAP;
    private static final Map<Keyword, BiConsumer<String, AddTextNode>> ORDER_BY_TRANSLATOR_MAP;

    static {
        Map<Keyword, ByTranslator> byTranslatorMap = new HashMap<>();
        byTranslatorMap.put(Keyword.IS, (nameEntry, addElement, addTextNode) -> addTextNode.accept(String.format("%s=#{%s}", nameEntry.getFieldName(), nameEntry.getName())));
        byTranslatorMap.put(Keyword.EQUALS, (nameEntry, addElement, addTextNode) -> addTextNode.accept(String.format("%s=#{%s}", nameEntry.getFieldName(), nameEntry.getName())));
        byTranslatorMap.put(Keyword.LESS_THAN, (nameEntry, addElement, addTextNode) -> addTextNode.accept(String.format("%s<#{%s}", nameEntry.getFieldName(), nameEntry.getName())));
        byTranslatorMap.put(Keyword.LESS_THAN_EQUALS, (nameEntry, addElement, addTextNode) -> addTextNode.accept(String.format("%s<=#{%s}", nameEntry.getFieldName(), nameEntry.getName())));
        byTranslatorMap.put(Keyword.GREATER_THAN, (nameEntry, addElement, addTextNode) -> addTextNode.accept(String.format("%s>#{%s}", nameEntry.getFieldName(), nameEntry.getName())));
        byTranslatorMap.put(Keyword.GREATER_THAN_EQUALS, (nameEntry, addElement, addTextNode) -> addTextNode.accept(String.format("%s>=#{%s}", nameEntry.getFieldName(), nameEntry.getName())));
        byTranslatorMap.put(Keyword.AFTER, (nameEntry, addElement, addTextNode) -> addTextNode.accept(String.format("%s>#{%s}", nameEntry.getFieldName(), nameEntry.getName())));
        byTranslatorMap.put(Keyword.BEFORE, (nameEntry, addElement, addTextNode) -> addTextNode.accept(String.format("%s<#{%s}", nameEntry.getFieldName(), nameEntry.getName())));
        byTranslatorMap.put(Keyword.IS_NULL, (nameEntry, addElement, addTextNode) -> addTextNode.accept(String.format("%s is null", nameEntry.getFieldName())));
        byTranslatorMap.put(Keyword.IS_NOT_NULL, (nameEntry, addElement, addTextNode) -> addTextNode.accept(String.format("%s is not null", nameEntry.getFieldName())));
        byTranslatorMap.put(Keyword.NOT_NULL, (nameEntry, addElement, addTextNode) -> addTextNode.accept(String.format("%s is not null", nameEntry.getFieldName())));
        byTranslatorMap.put(Keyword.LIKE, (nameEntry, addElement, addTextNode) -> addTextNode.accept(String.format("%s like #{%s}", nameEntry.getFieldName(), nameEntry.getName())));
        byTranslatorMap.put(Keyword.NOT_LIKE, (nameEntry, addElement, addTextNode) -> addTextNode.accept(String.format("%s not like #{%s}", nameEntry.getFieldName(), nameEntry.getName())));
        byTranslatorMap.put(Keyword.STARTING_WITH, (nameEntry, addElement, addTextNode) -> addTextNode.accept(String.format("%s like concat(#{%s}, '%%')", nameEntry.getFieldName(), nameEntry.getName())));
        byTranslatorMap.put(Keyword.ENDING_WITH, (nameEntry, addElement, addTextNode) -> addTextNode.accept(String.format("%s like concat('%%', #{%s})", nameEntry.getFieldName(), nameEntry.getName())));
        byTranslatorMap.put(Keyword.CONTAINING, (nameEntry, addElement, addTextNode) -> addTextNode.accept(String.format("%s like concat('%%', #{%s}, '%%')", nameEntry.getFieldName(), nameEntry.getName())));
        byTranslatorMap.put(Keyword.NOT, (nameEntry, addElement, addTextNode) -> addTextNode.accept(String.format("%s != #{%s}", nameEntry.getFieldName(), nameEntry.getName())));
        byTranslatorMap.put(Keyword.IN, (nameEntry, addElement, addTextNode) -> {
            addTextNode.accept(nameEntry.getFieldName(), " in", LF, INDENT);
            String itemName = "item".equals(nameEntry.getName()) ? "item0" : "item";
            Element foreachElement = addElement.apply("foreach");
            foreachElement.setAttribute("collection", nameEntry.getName());
            foreachElement.setAttribute("item", itemName);
            foreachElement.setAttribute("separator", ",");
            foreachElement.setAttribute("open", "(");
            foreachElement.setAttribute("close", ")");
            foreachElement.setTextContent(String.format("%s%s%s#{%s}%s%s", LF, INDENT, INDENT, itemName, LF, INDENT));
        });
        byTranslatorMap.put(Keyword.NOT_IN, (nameEntry, addElement, addTextNode) -> {
            addTextNode.accept(nameEntry.getFieldName(), " not in", LF, INDENT);
            String itemName = "item".equals(nameEntry.getName()) ? "item0" : "item";
            Element foreachElement = addElement.apply("foreach");
            foreachElement.setAttribute("collection", nameEntry.getName());
            foreachElement.setAttribute("item", itemName);
            foreachElement.setAttribute("separator", ",");
            foreachElement.setAttribute("open", "(");
            foreachElement.setAttribute("close", ")");
            foreachElement.setTextContent(String.format("%s%s%s#{%s}%s%s", LF, INDENT, INDENT, itemName, LF, INDENT));
        });
        BY_TRANSLATOR_MAP = byTranslatorMap;

        Map<Keyword, BiConsumer<String, AddTextNode>> orderByTranslatorMap = new HashMap<>();
        orderByTranslatorMap.put(Keyword.ASC, (fieldName, addTextNode) -> addTextNode.accept(String.format("%s", fieldName)));
        orderByTranslatorMap.put(Keyword.DESC, (fieldName, addTextNode) -> addTextNode.accept(String.format("%s desc", fieldName)));
        ORDER_BY_TRANSLATOR_MAP = orderByTranslatorMap;
    }

    public MySqlTranslator(TranslateContext translateContext) {
        super(translateContext);
    }

    @Override
    public void doTranslate(ASTNode node, MethodInfo methodInfo) {
        String methodName = methodInfo.getMethodName();
        List<Param> params = methodInfo.getParams();
        Return ret = methodInfo.getRet();

        if (existsStmtElement(methodName)) {
            throw new TranslateException("%s is exists", methodName);
        } if (node instanceof Insert) {
            translateInsert((Insert) node, methodName, params);
        } else if (node instanceof Delete) {
            translateDelete((Delete) node, methodName, params);
        } else if (node instanceof Update) {
            translateUpdate((Update) node, methodName, params);
        } else if (node instanceof Find) {
            translateFind((Find) node, methodName, params, ret);
        } else if (node instanceof Count) {
            translateCount((Count) node, methodName, params, ret);
        } else if (node instanceof Page) {
            translatePage((Page) node, methodName, params, ret);
        } else {
            throw new TranslateException("Can not translate");
        }
    }

    private void translateInsert(Insert insert, String methodName, List<Param> params) {
        // insert语句应当只有一个参数，是JavaBean或Iterable<JavaBean>
        if (params.size() != 1) {
            throw new TranslateException("%s should have only one param", methodName);
        }

        Param param = params.get(0);
        ASTNode child;
        if (insert.childrenSize() > 0
                && (child = insert.getChild(0)) != null
                && child.getLexeme().isKeyword(Keyword.ALL)) {
            translateInsertAll(methodName, param);
        } else {
            translateInsertOne(methodName, param);
        }
    }

    private void translateInsertAll(String methodName, Param param) {
        if (!param.isIterable()) {
            throw new TranslateException("%s should have iterable param", methodName);
        }
        if (!param.isJavaBean()) {
            throw new TranslateException("%s should have java bean param", methodName);
        }

        /*
         * <insert id="insertAll" useGeneratedKeys="true" keyProperty="id">
         *     insert into {table} (f1, f2, f3...) values
         *     <foreach collection="collection" item="item" separator=",">
         *         (#{item.jf1}, #{item.jf2}, #{item.jf3}...)
         *     </foreach>
         * </insert>
         */
        StmtElement insertStmtElement = addInsertElement(methodName, param.getType(), param.findUseGeneratedKeysField());
        Element insertElement = insertStmtElement.getElement();

        addTextNode(insertElement,
                LF,
                INDENT,
                "insert into ",
                tableName,
                " (",
                param.getBeanFields().stream()
                        .filter(bf -> !bf.isUseGeneratedKeys())
                        .map(BeanField::getFieldName)
                        .collect(Collectors.joining(", ")),
                ") values",
                LF,
                INDENT);

        Element foreachElement = addElement(insertElement, "foreach");
        foreachElement.setAttribute("collection", "collection");
        foreachElement.setAttribute("item", "item");
        foreachElement.setAttribute("separator", ",");

        String foreachText = LF + INDENT + INDENT + "(" + param.getBeanFields().stream()
                .filter(bf -> !bf.isUseGeneratedKeys())
                .map(bf -> String.format("#{item.%s}", bf.getName()))
                .collect(Collectors.joining(", ")) + ")" + LF + INDENT;
        addTextNode(foreachElement, foreachText);

        insertStmtElement.append();
    }

    private void translateInsertOne(String methodName, Param param) {
        if (!param.isJavaBean()) {
            throw new TranslateException("% should have java bean param", methodName);
        }

        /*
         * <insert id="insertAll" useGeneratedKeys="true" keyProperty="id">
         *     insert into {table} (f1, f2, f3...) values (#{jf1}, #{jf2}, #{jf3}...)
         * </insert>
         */
        StmtElement insertStmtElement = addInsertElement(methodName, param.getType(), param.findUseGeneratedKeysField());
        Element insertElement = insertStmtElement.getElement();

        addTextNode(insertElement, LF, INDENT, "insert into ", tableName, " (",
                param.getBeanFields().stream()
                        .filter(bf -> !bf.isUseGeneratedKeys())
                        .map(BeanField::getFieldName)
                        .collect(Collectors.joining(", ")),
                ") values", LF, INDENT, "(",
                param.getBeanFields().stream()
                        .filter(bf -> !bf.isUseGeneratedKeys())
                        .map(bf -> String.format("#{%s}", bf.getName()))
                        .collect(Collectors.joining(", ")),
                ")", LF);

        insertStmtElement.append();
    }

    private void translateDelete(Delete delete, String methodName, List<Param> params) {
        ASTNode child;
        if (delete.childrenSize() > 0
                && (child = delete.getChild(0)) != null
                && child.getLexeme().isKeyword(Keyword.ALL)) {
            if (params != null && !params.isEmpty()) {
                throw new TranslateException("%s should have should have no params", methodName);
            }
            translateDeleteAll(methodName);
        } else {
            translateDeleteBy(delete, methodName, params);
        }
    }

    private void translateDeleteAll(String methodName) {
        /*
         * <delete id="deleteAll">
         *     delete from {table}
         * </delete>
         */
        StmtElement deleteStmtElement = addDeleteElement(methodName, null);
        Element deleteElement = deleteStmtElement.getElement();

        addTextNode(deleteElement, LF, INDENT, "delete from ", tableName, LF);

        deleteStmtElement.append();
    }

    private void translateDeleteBy(Delete delete, String methodName, List<Param> params) {
        /*
         * <delete id="deleteBy...">
         *     delete from {table} where ...
         * </delete>
         */
        String parameterType = params.size() == 1 ? params.get(0).getType() : null;
        StmtElement deleteStmtElement = addDeleteElement(methodName, parameterType);
        Element deleteElement = deleteStmtElement.getElement();

        addTextNode(deleteElement, LF, INDENT, "delete from ", tableName, LF, INDENT);
        translateBy(deleteElement, (By) delete.getChild(0), asNameMap(params));
        addTextNode(deleteElement, LF);

        deleteStmtElement.append();
    }

    private void translateUpdate(Update update, String methodName, List<Param> params) {
        if (params.size() != 1) {
            throw new TranslateException("%s should have only one param", methodName);
        }

        Param param = params.get(0);
        if (!param.isJavaBean()) {
            throw new TranslateException("%s should have java bean param", methodName);
        }

        if (param.getBeanFields().isEmpty()) {
            throw new TranslateException("can not found update field in ", param);
        }

        /*
         * <update id="updateBy...">
         *     update {table} set ... where ...
         * </update>
         */
        StmtElement updateStmtElement = addUpdateElement(methodName, param.getType());
        Element updateElement = updateStmtElement.getElement();

        addTextNode(updateElement, LF, INDENT, "update ", tableName, " set ",
                param.getBeanFields().stream()
                        .map(f -> String.format("%s=#{%s}", f.getFieldName(), f.getName()))
                        .collect(Collectors.joining(", ")),
                LF, INDENT
                );
        translateBy(updateElement, (By) update.getChild(0), asNameMap(param.getBeanFields()));
        addTextNode(updateElement, LF);

        updateStmtElement.append();
    }

    private void translateFind(Find find, String methodName, List<Param> params, Return ret) {
        if (ret == null || !ret.isJavaBean()) {
            throw new TranslateException("%s should have java bean return", methodName);
        }
        if (ret.getBeanFields() == null || ret.getBeanFields().isEmpty()) {
            throw new TranslateException("%s must be have field", ret.getType());
        }

        /*
         * <select id="find...">
         *     select ... from {table} where ... order by ...
         * </select>
         */
        String parameterType = params.size() == 1 ? params.get(0).getType() : null;
        StmtElement selectStmtElement = addSelectElement(methodName, parameterType, ret.getType());
        Element selectElement = selectStmtElement.getElement();

        addTextNode(selectElement, LF, INDENT, "select ",
                ret.getBeanFields().stream()
                        .map(f -> f.getFieldName().equals(f.getName()) ? f.getFieldName() : String.format("%s as %s", f.getFieldName(), f.getName()))
                        .collect(Collectors.joining(", ")),
                LF, INDENT, "from ", tableName, LF
                );

        ASTNode child = find.getChild(0);
        if (!child.getLexeme().isKeyword(Keyword.ALL)) {
            addTextNode(selectElement, INDENT);
            translateBy(selectElement, (By) child, asNameMap(params));
            addTextNode(selectElement, LF);
        }

        if (find.childrenSize() > 1) {
            addTextNode(selectElement, INDENT);
            translateOrderBy(selectElement, (OrderBy) find.getChild(1));
            addTextNode(selectElement, LF);
        }

        selectStmtElement.append();
    }

    private void translateCount(Count count, String methodName, List<Param> params, Return ret) {
        if (!isIntOrLong(ret.getType())) {
            throw new TranslateException("%s's return should be int or long or Integer or Long", methodName);
        }

        /*
         * <select id="count...">
         *     select count(*) from {table} where ... order by ...
         * </select>
         */
        String parameterType = params.size() == 1 ? params.get(0).getType() : null;
        StmtElement selectStmtElement = addSelectElement(methodName, parameterType, ret.getType());
        Element selectElement = selectStmtElement.getElement();

        addTextNode(selectElement, LF, INDENT, "select count(*)", LF, INDENT, "from ", tableName, LF);

        ASTNode child = count.getChild(0);
        if (!child.getLexeme().isKeyword(Keyword.ALL)) {
            addTextNode(selectElement, INDENT);
            translateBy(selectElement, (By) child, asNameMap(params));
            addTextNode(selectElement, LF);
        }

        selectStmtElement.append();
    }

    private void translatePage(Page page, String methodName, List<Param> params, Return ret) {
        if (!ret.isJavaBean()) {
            throw new TranslateException("%s should have java bean return", methodName);
        }

        Map<String, NameEntry> nameMap = asNameMap(params);
        NameEntry offset = nameMap.get(OFFSET);
        NameEntry limit = nameMap.get(LIMIT);
        if (limit == null) {
            throw new TranslateException("%s should have limit params", methodName);
        }
        if (offset != null && !isIntOrLong(((Param) offset).getType())) {
            throw new TranslateException("%s's offset should be int or long or Integer or Long", methodName);
        }
        if (!isIntOrLong(((Param) limit).getType())) {
            throw new TranslateException("%s's limit should be int or long or Integer or Long", methodName);
        }

        /*
         * <select id="page...">
         *     select ... from {table} where ... order by ...
         * </select>
         */
        String parameterType = params.size() == 1 ? params.get(0).getType() : null;
        StmtElement selectStmtElement = addSelectElement(methodName, parameterType, ret.getType());
        Element selectElement = selectStmtElement.getElement();

        addTextNode(selectElement, LF, INDENT, "select ",
                ret.getBeanFields().stream()
                        .map(f -> f.getFieldName().equals(f.getName()) ? f.getFieldName() : String.format("%s as %s", f.getFieldName(), f.getName()))
                        .collect(Collectors.joining(", ")),
                LF, INDENT, "from ", tableName, LF);

        ASTNode child = page.getChild(0);
        if (!child.getLexeme().isKeyword(Keyword.ALL)) {
            addTextNode(selectElement, INDENT);
            translateBy(selectElement, (By) child, nameMap);
            addTextNode(selectElement, LF);
        }

        if (page.childrenSize() > 1) {
            addTextNode(selectElement, INDENT);
            translateOrderBy(selectElement, (OrderBy) page.getChild(1));
            addTextNode(selectElement, LF);
        }

        addTextNode(selectElement, INDENT, "limit ");
        if (offset != null) {
            addTextNode(selectElement, "#{offset},");
        }
        addTextNode(selectElement, "#{limit}", LF);

        selectStmtElement.append();
    }

    private boolean isIntOrLong(String type) {
        return "int".equals(type)
                || "long".equals(type)
                || "java.lang.Integer".equals(type)
                || "java.lang.Long".equals(type);
    }

    private Map<String, NameEntry> asNameMap(List<? extends NameEntry> list) {
        Map<String, NameEntry> map = new HashMap<>();
        for (NameEntry nameEntry : list) {
            map.put(StringUtils.upperCamelToLowerCamel(nameEntry.getName()), nameEntry);
        }
        return map;
    }

    private void translateBy(Element parent, By by, Map<String, NameEntry> nameMap) {
        addTextNode(parent, "where", BLANK);
        doTranslateBy(parent, by.getChild(0), nameMap);
    }

    private void doTranslateBy(Element parent, ASTNode node, Map<String, NameEntry> nameMap) {
        if (node instanceof ConnectOp) {
            doTranslateBy(parent, node.getChild(0), nameMap);
            addTextNode(parent, LF, INDENT, node.getLexeme().getValue().toLowerCase(), BLANK);
            doTranslateBy(parent, node.getChild(1), nameMap);
        } else if (node instanceof ByOp) {
            translateByOp(parent, (ByOp) node, nameMap);
        } else {
            throw new TranslateException("Translate error, %s", node);
        }
    }

    private void translateByOp(Element parent, ByOp byOp, Map<String, NameEntry> nameMap) {
        Token lexeme = byOp.getLexeme();
        Keyword kw = Keyword.of(lexeme.getValue());
        ByTranslator translator = BY_TRANSLATOR_MAP.get(kw);
        if (translator == null) {
            throw new TranslateException("Translate error, %s", byOp);
        }

        Variable variable = (Variable) byOp.getChild(0);
        NameEntry nameEntry = nameMap.get(StringUtils.upperCamelToLowerCamel(variable.getLexeme().getValue()));
        if (nameEntry == null) {
            if (nameMap.size() != 1) {
                throw new TranslateException("Can not found name entry %s", variable.getLexeme().getValue());
            } else {
                // 采用降级策略，使用该策略必须确保在之前的代码中设置了parameterType
                nameEntry = nameMap.entrySet().iterator().next().getValue();
            }
        }

        translator.translate(nameEntry, tagName -> addElement(parent, tagName), texts -> addTextNode(parent, texts));
    }

    private void translateOrderBy(Element parent, OrderBy orderBy) {
        addTextNode(parent, "order by", BLANK);
        doTranslateOrderBy(parent, orderBy.getChild(0));
    }

    private void doTranslateOrderBy(Element parent, ASTNode node) {
        if (node instanceof ConnectOp) {
            doTranslateOrderBy(parent, node.getChild(0));
            addTextNode(parent, ", ");
            doTranslateOrderBy(parent, node.getChild(1));
        } else if (node instanceof OrderByOp) {
            translateOrderByOp(parent, (OrderByOp) node);
        } else {
            throw new TranslateException("Translate error, %s", node);
        }
    }

    private void translateOrderByOp(Element parent, OrderByOp orderByOp) {
        Token lexeme = orderByOp.getLexeme();
        Keyword kw = Keyword.of(lexeme.getValue());
        BiConsumer<String, AddTextNode> translator = ORDER_BY_TRANSLATOR_MAP.get(kw);
        if (translator == null) {
            throw new TranslateException("Translate error, %s", orderByOp);
        }

        Variable variable = (Variable) orderByOp.getChild(0);
        translator.accept(fieldNamingConverter.convert(StringUtils.upperCamelToLowerCamel(variable.getLexeme().getValue())), texts -> addTextNode(parent, texts));
    }

    @FunctionalInterface
    interface ByTranslator {

        void translate(NameEntry nameEntry, Function<String, Element> addElement, AddTextNode addTextNode);

    }

    @FunctionalInterface
    interface AddTextNode {

        void accept(CharSequence... texts);

    }

}
