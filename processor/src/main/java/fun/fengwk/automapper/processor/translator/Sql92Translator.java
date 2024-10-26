package fun.fengwk.automapper.processor.translator;

import fun.fengwk.automapper.annotation.DynamicOrderBy;
import fun.fengwk.automapper.processor.lexer.Keyword;
import fun.fengwk.automapper.processor.lexer.Token;
import fun.fengwk.automapper.processor.parser.ast.*;
import fun.fengwk.automapper.processor.util.StringUtils;
import org.w3c.dom.Element;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 通用标准的的sql翻译器。
 *
 * @author fengwk
 */
public class Sql92Translator extends Translator {

    protected final Map<Keyword, ByTranslator> byTranslatorMap;
    protected final Map<Keyword, BiConsumer<String, AddTextNode>> orderByTranslatorMap;

    public Sql92Translator(TranslateContext translateContext) {
        super(translateContext);

        this.byTranslatorMap = buildByTranslatorMap();
        this.orderByTranslatorMap = buildOrderByTranslatorMap();
    }

    protected Map<Keyword, ByTranslator> buildByTranslatorMap() {
        Map<Keyword, ByTranslator> byTranslatorMap = new HashMap<>();
        byTranslatorMap.put(Keyword.IS, (nameEntry, addElement, addTextNode, isSingleParam, indent) -> addTextNode.accept(String.format("%s=#{%s}", nameEntry.getFieldName(), nameEntry.getName())));
        byTranslatorMap.put(Keyword.EQUALS, (nameEntry, addElement, addTextNode, isSingleParam, indent) -> addTextNode.accept(String.format("%s=#{%s}", nameEntry.getFieldName(), nameEntry.getName())));
        byTranslatorMap.put(Keyword.LESS_THAN, (nameEntry, addElement, addTextNode, isSingleParam, indent) -> addTextNode.accept(String.format("%s<#{%s}", nameEntry.getFieldName(), nameEntry.getName())));
        byTranslatorMap.put(Keyword.LESS_THAN_EQUALS, (nameEntry, addElement, addTextNode, isSingleParam, indent) -> addTextNode.accept(String.format("%s<=#{%s}", nameEntry.getFieldName(), nameEntry.getName())));
        byTranslatorMap.put(Keyword.GREATER_THAN, (nameEntry, addElement, addTextNode, isSingleParam, indent) -> addTextNode.accept(String.format("%s>#{%s}", nameEntry.getFieldName(), nameEntry.getName())));
        byTranslatorMap.put(Keyword.GREATER_THAN_EQUALS, (nameEntry, addElement, addTextNode, isSingleParam, indent) -> addTextNode.accept(String.format("%s>=#{%s}", nameEntry.getFieldName(), nameEntry.getName())));
        byTranslatorMap.put(Keyword.AFTER, (nameEntry, addElement, addTextNode, isSingleParam, indent) -> addTextNode.accept(String.format("%s>#{%s}", nameEntry.getFieldName(), nameEntry.getName())));
        byTranslatorMap.put(Keyword.BEFORE, (nameEntry, addElement, addTextNode, isSingleParam, indent) -> addTextNode.accept(String.format("%s<#{%s}", nameEntry.getFieldName(), nameEntry.getName())));
        byTranslatorMap.put(Keyword.IS_NULL, (nameEntry, addElement, addTextNode, isSingleParam, indent) -> addTextNode.accept(String.format("%s is null", nameEntry.getFieldName())));
        byTranslatorMap.put(Keyword.IS_NOT_NULL, (nameEntry, addElement, addTextNode, isSingleParam, indent) -> addTextNode.accept(String.format("%s is not null", nameEntry.getFieldName())));
        byTranslatorMap.put(Keyword.NOT_NULL, (nameEntry, addElement, addTextNode, isSingleParam, indent) -> addTextNode.accept(String.format("%s is not null", nameEntry.getFieldName())));
        byTranslatorMap.put(Keyword.LIKE, (nameEntry, addElement, addTextNode, isSingleParam, indent) -> addTextNode.accept(String.format("%s like #{%s}", nameEntry.getFieldName(), nameEntry.getName())));
        byTranslatorMap.put(Keyword.NOT_LIKE, (nameEntry, addElement, addTextNode, isSingleParam, indent) -> addTextNode.accept(String.format("%s not like #{%s}", nameEntry.getFieldName(), nameEntry.getName())));
        byTranslatorMap.put(Keyword.STARTING_WITH, (nameEntry, addElement, addTextNode, isSingleParam, indent) -> addTextNode.accept(String.format("%s like '${%s}%%'", nameEntry.getFieldName(), nameEntry.getName())));
        byTranslatorMap.put(Keyword.ENDING_WITH, (nameEntry, addElement, addTextNode, isSingleParam, indent) -> addTextNode.accept(String.format("%s like '%%${%s}'", nameEntry.getFieldName(), nameEntry.getName())));
        byTranslatorMap.put(Keyword.CONTAINING, (nameEntry, addElement, addTextNode, isSingleParam, indent) -> addTextNode.accept(String.format("%s like '%%${%s}%%'", nameEntry.getFieldName(), nameEntry.getName())));
        byTranslatorMap.put(Keyword.NOT, (nameEntry, addElement, addTextNode, isSingleParam, indent) -> addTextNode.accept(String.format("%s != #{%s}", nameEntry.getFieldName(), nameEntry.getName())));
        byTranslatorMap.put(Keyword.IN, (nameEntry, addElement, addTextNode, isSingleParam, indent) -> {
            addTextNode.accept(nameEntry.getFieldName(), " in", LF, indent(indent));
            String itemName = "item".equals(nameEntry.getName()) ? "item0" : "item";
            Element foreachElement = addElement.apply("foreach");
            foreachElement.setAttribute("collection", isSingleParam ? "collection" : nameEntry.getName());
            foreachElement.setAttribute("item", itemName);
            foreachElement.setAttribute("separator", ",");
            foreachElement.setAttribute("open", "(");
            foreachElement.setAttribute("close", ")");
            foreachElement.setTextContent(String.format("%s%s#{%s}%s%s", LF, indent(indent + 1), itemName, LF, indent(indent)));
        });
        byTranslatorMap.put(Keyword.NOT_IN, (nameEntry, addElement, addTextNode, isSingleParam, indent) -> {
            addTextNode.accept(nameEntry.getFieldName(), " not in", LF, indent(indent));
            String itemName = "item".equals(nameEntry.getName()) ? "item0" : "item";
            Element foreachElement = addElement.apply("foreach");
            foreachElement.setAttribute("collection", isSingleParam ? "collection" : nameEntry.getName());
            foreachElement.setAttribute("item", itemName);
            foreachElement.setAttribute("separator", ",");
            foreachElement.setAttribute("open", "(");
            foreachElement.setAttribute("close", ")");
            foreachElement.setTextContent(String.format("%s%s#{%s}%s%s", LF, indent(indent + 1), itemName, LF, indent(indent)));
        });

        return byTranslatorMap;
    }

    protected Map<Keyword, BiConsumer<String, AddTextNode>> buildOrderByTranslatorMap() {
        Map<Keyword, BiConsumer<String, AddTextNode>> orderByTranslatorMap = new HashMap<>();
        orderByTranslatorMap.put(Keyword.ASC, (fieldName, addTextNode) -> addTextNode.accept(String.format("%s", fieldName)));
        orderByTranslatorMap.put(Keyword.DESC, (fieldName, addTextNode) -> addTextNode.accept(String.format("%s desc", fieldName)));
        return orderByTranslatorMap;
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
            if (isSelective(node)) {
                translateUpdateSelective((Update) node, methodName, params);
            } else {
                translateUpdate((Update) node, methodName, params);
            }
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
        // insert语句应当只有一个参数或是一个被@EntityParam标记的参数，是JavaBean或Iterable<JavaBean>
        Param param = getEntityParamRequired(params, methodName);

        ASTNode child;
        if (insert.childrenSize() > 0
                && (child = insert.getChild(0)) != null
                && child.getLexeme().isKeyword(Keyword.ALL)) {
            if (isSelective(insert)) {
                translateInsertAllSelective(insert, methodName, param);
            } else {
                translateInsertAll(insert, methodName, param);
            }
        } else {
            if (isSelective(insert)) {
                translateInsertOneSelective(insert, methodName, param);
            } else {
                translateInsertOne(insert, methodName, param);
            }
        }
    }

    private void translateInsertAll(Insert insert, String methodName, Param param) {
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
        foreachElement.setAttribute("collection", param instanceof DetectEntityParam ? param.getName() : "collection");
        foreachElement.setAttribute("item", "item");
        foreachElement.setAttribute("separator", ",");

        String foreachText = LF + INDENT + INDENT + "(" + param.getBeanFields().stream()
                .filter(bf -> !bf.isUseGeneratedKeys())
                .map(bf -> String.format("#{item.%s}",
                    bf instanceof DetectEntityBeanField ? ((DetectEntityBeanField) bf).getCollectionItemVariableName()
                        : bf.getVariableName()))
                .collect(Collectors.joining(", ")) + ")" + LF + INDENT;
        addTextNode(foreachElement, foreachText);

        // for subclass
        postProcessInsert(insert, insertElement, param);

        insertStmtElement.append();
    }

    private void translateInsertAllSelective(Insert insert, String methodName, Param param) {
        if (!param.isIterable()) {
            throw new TranslateException("%s should have iterable param", methodName);
        }
        if (!param.isJavaBean()) {
            throw new TranslateException("%s should have java bean param", methodName);
        }

        StmtElement insertStmtElement = addInsertElement(methodName, param.getType(), param.findUseGeneratedKeysField());
        Element insertElement = insertStmtElement.getElement();

        addTextNode(insertElement, LF, INDENT);
        Element foreachElement = addElement(insertElement, "foreach");
        boolean isDetectEntityParam = param instanceof DetectEntityParam;
        foreachElement.setAttribute("collection", isDetectEntityParam ? param.getName() : "collection");
        foreachElement.setAttribute("item", "item");
        foreachElement.setAttribute("separator", ";");
        addTextNode(foreachElement, LF, INDENT, INDENT, "insert into ", tableName, LF, INDENT, INDENT);
        Element trimElement = addElement(foreachElement, "trim");
        trimElement.setAttribute("prefix", "(");
        trimElement.setAttribute("suffix", ")");
        trimElement.setAttribute("suffixOverrides", ",");
        for (BeanField bf : param.getBeanFields()) {
            if (!bf.isUseGeneratedKeys()) {
                addTextNode(trimElement, LF, INDENT, INDENT, INDENT);
                Element ifElement = addElement(trimElement, "if");
                ifElement.setAttribute("test", String.format("item.%s != null",
                    bf instanceof DetectEntityBeanField ? ((DetectEntityBeanField) bf).getCollectionItemName()
                        : bf.getName()));
                addTextNode(ifElement, bf.getFieldName(), ",");
            }
        }
        addTextNode(trimElement, LF, INDENT, INDENT);
        addTextNode(foreachElement, LF, INDENT, INDENT, "values", LF, INDENT, INDENT);

        trimElement = addElement(foreachElement, "trim");
        trimElement.setAttribute("prefix", "(");
        trimElement.setAttribute("suffix", ")");
        trimElement.setAttribute("suffixOverrides", ",");
        for (BeanField bf : param.getBeanFields()) {
            if (!bf.isUseGeneratedKeys()) {
                addTextNode(trimElement, LF, INDENT, INDENT, INDENT);
                Element ifElement = addElement(trimElement, "if");
                ifElement.setAttribute("test", String.format("item.%s != null",
                    bf instanceof DetectEntityBeanField ? ((DetectEntityBeanField) bf).getCollectionItemName()
                        : bf.getName()));
                addTextNode(ifElement, "#{item.",
                    bf instanceof DetectEntityBeanField ? ((DetectEntityBeanField) bf).getCollectionItemVariableName()
                        : bf.getVariableName(),
                    "},");
            }
        }
        addTextNode(trimElement, LF, INDENT, INDENT);
        addTextNode(foreachElement, LF, INDENT);

        // for subclass
        postProcessInsert(insert, insertElement, param);

        insertStmtElement.append();
    }

    private void translateInsertOne(Insert insert, String methodName, Param param) {
        if (!param.isJavaBean()) {
            throw new TranslateException("% should have java bean param", methodName);
        }
        if (param.isIterable()) {
            throw new TranslateException("%s can not be iterable param", methodName);
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
                        .map(bf -> String.format("#{%s}", bf.getVariableName()))
                        .collect(Collectors.joining(", ")),
                ")", LF);

        // for subclass
        postProcessInsert(insert, insertElement, param);

        insertStmtElement.append();
    }

    private void translateInsertOneSelective(Insert insert, String methodName, Param param) {
        if (!param.isJavaBean()) {
            throw new TranslateException("% should have java bean param", methodName);
        }
        if (param.isIterable()) {
            throw new TranslateException("%s can not be iterable param", methodName);
        }

        StmtElement insertStmtElement = addInsertElement(methodName, param.getType(), param.findUseGeneratedKeysField());
        Element insertElement = insertStmtElement.getElement();

        addTextNode(insertElement, LF, INDENT, "insert into ", tableName, LF, INDENT);
        Element trimElement = addElement(insertElement, "trim");
        trimElement.setAttribute("prefix", "(");
        trimElement.setAttribute("suffix", ")");
        trimElement.setAttribute("suffixOverrides", ",");
        for (BeanField bf : param.getBeanFields()) {
            if (!bf.isUseGeneratedKeys()) {
                addTextNode(trimElement, LF, INDENT, INDENT);
                Element ifElement = addElement(trimElement, "if");
                ifElement.setAttribute("test", String.format("%s != null", bf.getName()));
                addTextNode(ifElement, bf.getFieldName(), ",");
            }
        }
        addTextNode(trimElement, LF, INDENT);
        addTextNode(insertElement, LF, INDENT, "values", LF, INDENT);
        trimElement = addElement(insertElement, "trim");
        trimElement.setAttribute("prefix", "(");
        trimElement.setAttribute("suffix", ")");
        trimElement.setAttribute("suffixOverrides", ",");
        for (BeanField bf : param.getBeanFields()) {
            if (!bf.isUseGeneratedKeys()) {
                addTextNode(trimElement, LF, INDENT, INDENT);
                Element ifElement = addElement(trimElement, "if");
                ifElement.setAttribute("test", String.format("%s != null", bf.getName()));
                addTextNode(ifElement, "#{", bf.getVariableName(), "},");
            }
        }
        addTextNode(trimElement, LF, INDENT);
        addTextNode(insertElement, LF);

        // for subclass
        postProcessInsert(insert, insertElement, param);

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
            translateDeleteAll(delete, methodName);
        } else {
            translateDeleteBy(delete, methodName, params);
        }
    }

    private void translateDeleteAll(Delete delete, String methodName) {
        /*
         * <delete id="deleteAll">
         *     delete from {table}
         * </delete>
         */
        StmtElement deleteStmtElement = addDeleteElement(methodName, null);
        Element deleteElement = deleteStmtElement.getElement();

        addTextNode(deleteElement, LF, INDENT, "delete from ", tableName, LF);

        // for subclass
        postProcessDelete(delete, deleteElement);

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

        // for subclass
        postProcessDelete(delete, deleteElement);

        deleteStmtElement.append();
    }

    private void translateUpdate(Update update, String methodName, List<Param> params) {
        Param param = getEntityParamRequired(params, methodName);

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
                        .map(f -> {
                            if (f.getUpdateIncrement() == null || f.getUpdateIncrement().isEmpty()) {
                                return String.format("%s=#{%s}", f.getFieldName(), f.getVariableName());
                            } else {
                                return String.format("%s=%s+%s", f.getFieldName(), f.getFieldName(), f.getUpdateIncrement());
                            }
                        })
                        .collect(Collectors.joining(", ")),
                LF, INDENT
                );
        translateBy(updateElement, (By) update.getChild(0), asNameMap(param.getBeanFields()));
        addTextNode(updateElement, LF);

        // for subclass
        postProcessUpdate(update, updateElement);

        updateStmtElement.append();
    }

    private void translateUpdateSelective(Update update, String methodName, List<Param> params) {
        Param param = getEntityParamRequired(params, methodName);

        if (!param.isJavaBean()) {
            throw new TranslateException("%s should have java bean param", methodName);
        }

        if (param.getBeanFields().isEmpty()) {
            throw new TranslateException("can not found update field in ", param);
        }

        StmtElement updateStmtElement = addUpdateElement(methodName, param.getType());
        Element updateElement = updateStmtElement.getElement();

        addTextNode(updateElement, LF, INDENT, "update ", tableName, " set ", LF, INDENT);
        Element trimElement = addElement(updateElement, "trim");
        trimElement.setAttribute("suffixOverrides", ",");
        List<BeanField> bfs = param.getBeanFields();
        for (int i = 0; i < bfs.size(); i++) {
            BeanField bf = bfs.get(i);
            addTextNode(trimElement, LF, INDENT, INDENT);
            if (bf.getUpdateIncrement() == null || bf.getUpdateIncrement().isEmpty()) {
                Element ifElement = addElement(trimElement, "if");
                ifElement.setAttribute("test", String.format("%s != null", bf.getName()));
                addTextNode(ifElement, String.format("%s=#{%s},", bf.getFieldName(), bf.getVariableName()));
            } else {
                addTextNode(trimElement, String.format("%s=%s+%s,", bf.getFieldName(), bf.getFieldName(), bf.getUpdateIncrement()));
            }
        }
        addTextNode(trimElement, LF, INDENT);
        addTextNode(updateElement, LF, INDENT);

        translateBy(updateElement, (By) update.getChild(0), asNameMap(param.getBeanFields()));
        addTextNode(updateElement, LF);

        // for subclass
        postProcessUpdate(update, updateElement);

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
        StmtElement selectStmtElement = addSelectElement(methodName, parameterType, ret);
        Element selectElement = selectStmtElement.getElement();

        addTextNode(selectElement, LF, INDENT, "select ",
                ret.getBeanFields().stream()
                        .map(f -> f.getFieldName().equals(f.getName()) ? f.getFieldName() : String.format("%s as `%s`", f.getFieldName(), f.getName()))
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
        } else {
            List<Param> dynamicOrderByParams = params.stream()
                .filter(Param::isDynamicOrderBy).collect(Collectors.toList());
            if (dynamicOrderByParams.size() > 1) {
                throw new TranslateException(
                    "Only one param is allowed to use @%s", DynamicOrderBy.class.getSimpleName());
            } else if (dynamicOrderByParams.size() == 1) {
                Param dynamicOrderByParam = dynamicOrderByParams.get(0);
                if (dynamicOrderByParam.isSelective()) {
                    addTextNode(selectElement, INDENT);
                    Element ifElement = addElement(selectElement, "if");
                    ifElement.setAttribute("test", String.format("%s != null", dynamicOrderByParam.getName()));
                    addTextNode(ifElement, LF, INDENT, INDENT, "order by ${", dynamicOrderByParam.getName(), "}", LF, INDENT);
                    addTextNode(selectElement, LF);
                } else {
                    addTextNode(selectElement, INDENT, "order by ${", dynamicOrderByParam.getName(), "}", LF);
                }
            }
        }

        // for subclass
        postProcessFind(find, selectElement);

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
        StmtElement selectStmtElement = addSelectElement(methodName, parameterType, ret);
        Element selectElement = selectStmtElement.getElement();

        addTextNode(selectElement, LF, INDENT, "select count(*)", LF, INDENT, "from ", tableName, LF);

        ASTNode child = count.getChild(0);
        if (!child.getLexeme().isKeyword(Keyword.ALL)) {
            addTextNode(selectElement, INDENT);
            translateBy(selectElement, (By) child, asNameMap(params));
            addTextNode(selectElement, LF);
        }

        // for subclass
        postProcessCount(count, selectElement);

        selectStmtElement.append();
    }

    private void translatePage(Page page, String methodName, List<Param> params, Return ret) {
        if (!ret.isJavaBean()) {
            throw new TranslateException("%s should have java bean return", methodName);
        }

        Map<String, SelectiveNameEntry> nameMap = asNameMap(params);
        SelectiveNameEntry offset = nameMap.get(OFFSET);
        SelectiveNameEntry limit = nameMap.get(LIMIT);
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
        StmtElement selectStmtElement = addSelectElement(methodName, parameterType, ret);
        Element selectElement = selectStmtElement.getElement();

        addTextNode(selectElement, LF, INDENT, "select ",
                ret.getBeanFields().stream()
                        .map(f -> f.getFieldName().equals(f.getName()) ? f.getFieldName() : String.format("%s as `%s`", f.getFieldName(), f.getName()))
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
        } else {
            List<Param> dynamicOrderByParams = params.stream()
                .filter(Param::isDynamicOrderBy).collect(Collectors.toList());
            if (dynamicOrderByParams.size() > 1) {
                throw new TranslateException(
                    "Only one param is allowed to use @%s", DynamicOrderBy.class.getSimpleName());
            } else if (dynamicOrderByParams.size() == 1) {
                Param dynamicOrderByParam = dynamicOrderByParams.get(0);
                if (dynamicOrderByParam.isSelective()) {
                    addTextNode(selectElement, INDENT);
                    Element ifElement = addElement(selectElement, "if");
                    ifElement.setAttribute("test", String.format("%s != null", dynamicOrderByParam.getName()));
                    addTextNode(ifElement, LF, INDENT, INDENT, "order by ${", dynamicOrderByParam.getName(), "}", LF, INDENT);
                    addTextNode(selectElement, LF);
                } else {
                    addTextNode(selectElement, INDENT, "order by ${", dynamicOrderByParam.getName(), "}", LF);
                }
            }
        }

        addTextNode(selectElement, INDENT, "limit ");
        if (offset != null) {
            addTextNode(selectElement, "#{offset},");
        }
        addTextNode(selectElement, "#{limit}", LF);

        // for subclass
        postProcessPage(page, selectElement);

        selectStmtElement.append();
    }

    private boolean isIntOrLong(String type) {
        return "int".equals(type)
                || "long".equals(type)
                || "java.lang.Integer".equals(type)
                || "java.lang.Long".equals(type);
    }

    private Map<String, SelectiveNameEntry> asNameMap(List<? extends SelectiveNameEntry> list) {
        Map<String, SelectiveNameEntry> map = new HashMap<>();
        for (SelectiveNameEntry nameEntry : list) {
            map.put(StringUtils.upperCamelToLowerCamel(nameEntry.getName()), nameEntry);
        }
        return map;
    }

    private void translateBy(Element parent, By by, Map<String, SelectiveNameEntry> nameMap) {
//        addTextNode(parent, "where", BLANK);
        Element whereElement = addElement(parent, "where");
        doTranslateBy(whereElement, by.getChild(0), nameMap, new LinkedList<>());
        addTextNode(whereElement, LF, INDENT);
    }

    private void doTranslateBy(Element parent, ASTNode node, Map<String, SelectiveNameEntry> nameMap, LinkedList<String> connStack) {
        if (node instanceof ConnectOp) {
            doTranslateBy(parent, node.getChild(0), nameMap, connStack);
//            addTextNode(parent, LF, INDENT, node.getLexeme().getValue().toLowerCase(), BLANK);
            connStack.push(node.getLexeme().getValue().toLowerCase());
            doTranslateBy(parent, node.getChild(1), nameMap, connStack);
        } else if (node instanceof ByOp) {
            translateByOp(parent, (ByOp) node, nameMap, connStack);
        } else {
            throw new TranslateException("Translate error, %s", node);
        }
    }

    private void translateByOp(Element parent, ByOp byOp, Map<String, SelectiveNameEntry> nameMap, LinkedList<String> connStack) {
        Token lexeme = byOp.getLexeme();
        Keyword kw = Keyword.of(lexeme.getValue());
        ByTranslator translator = byTranslatorMap.get(kw);
        if (translator == null) {
            throw new TranslateException("Translate error, %s", byOp);
        }

        Variable variable = (Variable) byOp.getChild(0);
        SelectiveNameEntry nameEntry = nameMap.get(StringUtils.upperCamelToLowerCamel(variable.getLexeme().getValue()));
        boolean isSingleParam = nameMap.size() == 1;
        if (nameEntry == null) {
            if (isSingleParam) {
                // 采用降级策略，使用该策略必须确保在之前的代码中设置了parameterType
                nameEntry = nameMap.entrySet().iterator().next().getValue();
            } else {
                throw new TranslateException("Can not found name entry %s", variable.getLexeme().getValue());
            }
        }
        // 如果name和fieldName是推断的，使用LexemeValue替换
        if (nameEntry.isInferredName()) {
            nameEntry = new ImmutableSelectiveNameEntry(
                StringUtils.upperCamelToLowerCamel(variable.getLexeme().getValue()), nameEntry.getFieldName(),
                nameEntry.isInferredName(), nameEntry.isInferredFieldName(), nameEntry.isSelective());
        }
        if (nameEntry.isInferredFieldName()) {
            nameEntry = new ImmutableSelectiveNameEntry(
                nameEntry.getName(), fieldNamingConverter.convert(StringUtils.upperCamelToLowerCamel(nameEntry.getName())),
                nameEntry.isInferredName(), nameEntry.isInferredFieldName(), nameEntry.isSelective());
        }

        if (nameEntry.isSelective()) {
            addTextNode(parent, LF, INDENT, INDENT);
            Element ifElement = addElement(parent, "if");
            ifElement.setAttribute("test", String.format("%s != null", nameEntry.getName()));
            addTextNode(ifElement, LF, INDENT, INDENT, INDENT);
            if (!connStack.isEmpty()) {
                addTextNode(ifElement, connStack.pop(), BLANK);
            }
            translator.translate(nameEntry, tagName -> addElement(ifElement, tagName), texts -> addTextNode(ifElement, texts), isSingleParam, 3);
            addTextNode(ifElement, LF, INDENT, INDENT);
        } else {
            addTextNode(parent, LF, INDENT, INDENT);
            if (!connStack.isEmpty()) {
                addTextNode(parent, connStack.pop(), BLANK);
            }
            translator.translate(nameEntry, tagName -> addElement(parent, tagName), texts -> addTextNode(parent, texts), isSingleParam, 2);
        }
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
        BiConsumer<String, AddTextNode> translator = orderByTranslatorMap.get(kw);
        if (translator == null) {
            throw new TranslateException("Translate error, %s", orderByOp);
        }

        Variable variable = (Variable) orderByOp.getChild(0);
        translator.accept(fieldNamingConverter.convert(StringUtils.upperCamelToLowerCamel(variable.getLexeme().getValue())), texts -> addTextNode(parent, texts));
    }

    private Param getEntityParamRequired(List<Param> params, String methodName) {
        // 获取JavaBean或Iterable<JavaBean>
        Param param;
        if (params.size() != 1) {
            param = detectEntityParam(params);
            if (param == null) {
                throw new TranslateException("%s can not infer entity param", methodName);
            }
        } else {
            param = params.get(0);
        }
        return param;
    }

    private Param detectEntityParam(List<Param> params) {
        // 如果只有一个javaBean则推测该javaBean为param
        List<Param> javaBeanParams = new ArrayList<>();
        for (Param param : params) {
            if (param.isJavaBean() && param.getName() != null && !param.isInferredName()) {
                javaBeanParams.add(param);
            }
        }
        if (javaBeanParams.size() == 1) {
            return new DetectEntityParam(javaBeanParams.get(0));
        }
        return null;
    }

    @FunctionalInterface
    public interface ByTranslator {

        void translate(SelectiveNameEntry nameEntry, Function<String, Element> addElement, AddTextNode addTextNode, boolean isSingleParam, int indent);

    }

    @FunctionalInterface
    public interface AddTextNode {

        void accept(CharSequence... texts);

    }

    protected void postProcessInsert(Insert insert, Element insertElement, Param param) {
        // subclass extension point
    }

    protected void postProcessDelete(Delete delete, Element deleteElement) {
        // subclass extension point
    }

    protected void postProcessUpdate(Update update, Element updateElement) {
        // subclass extension point
    }

    protected void postProcessFind(Find find, Element selectElement) {
        // subclass extension point
    }

    protected void postProcessCount(Count count, Element countElement) {
        // subclass extension point
    }

    protected void postProcessPage(Page page, Element pageElement) {
        // subclass extension point
    }

}
