package fun.fengwk.automapper.processor.translator;

import fun.fengwk.automapper.processor.lexer.Lexer;
import fun.fengwk.automapper.processor.lexer.Token;
import fun.fengwk.automapper.processor.naming.NamingConverter;
import fun.fengwk.automapper.processor.parser.ParseException;
import fun.fengwk.automapper.processor.parser.Parser;
import fun.fengwk.automapper.processor.parser.TokenIterator;
import fun.fengwk.automapper.processor.parser.ast.ASTNode;
import fun.fengwk.automapper.processor.parser.ast.Selective;
import fun.fengwk.automapper.processor.util.LocalEntityResolver;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author fengwk
 */
public abstract class Translator {


    protected static final String LIMIT_PAGE = "limit";
    protected static final String ADD = "add";
    protected static final String REMOVE = "remove";
    protected static final String GET = "get";
    protected static final String LIST = "list";
    protected static final String SELECT = "select";
    protected static final String OFFSET = "offset";
    protected static final String LIMIT = "limit";

    protected static final String BLANK = " ";
    protected static final String INDENT = "    ";
    protected static final String LF = "\n";
    protected static final String LF_LF = "\n\n";

    protected static final String TAG_INSERT = "insert";
    protected static final String TAG_DELETE = "delete";
    protected static final String TAG_UPDATE = "update";
    protected static final String TAG_SELECT = "select";

    protected final String tableName;
    protected final Document document;
    protected final Element mapperElement;
    protected final NamingConverter fieldNamingConverter;

    protected Set<String> existingIdsCache;
    protected Map<String, Element> appendedIdMap = new HashMap<>();

    protected Lexer lexer = newLexer();
    protected Parser parser = new Parser();

    public Translator(TranslateContext translateContext) {
        this.tableName = translateContext.getTableName();
        if (translateContext.getInput() == null) {
            this.document = newDocument(translateContext.getNamespace());
        } else {
            this.document = parse(translateContext.getInput());
        }
        this.mapperElement = getMapperElement(translateContext.getNamespace());
        this.fieldNamingConverter = translateContext.getFieldNamingConverter();
    }

    /**
     * 构建词法分析器。
     *
     * @return
     */
    protected Lexer newLexer() {
        Lexer.Builder builder = new Lexer.Builder()
            .deriveInsert(ADD)
            .deriveDelete(REMOVE)
            .deriveFind(GET)
            .deriveFind(LIST)
            .deriveFind(SELECT)
            .derivePage(LIMIT_PAGE);
        configureLexer(builder);
        return builder.build();
    }

    protected void configureLexer(Lexer.Builder lexerBuilder) {
        // configure hook
    }

    /**
     * 将方法信息翻译为 mybatis xml，如果方法已存在无需翻译返回false，无法翻译的情况将抛出{@link TranslateException}。
     *
     * @param methodInfo
     * @return
     */
    public boolean translate(MethodInfo methodInfo) {
        if (existsStmtElement(methodInfo.getMethodName())) {
            return false;
        }

        ASTNode node = parse(methodInfo.getMethodExpr());
        doTranslate(node, methodInfo);
        return true;
    }

    /**
     * 将抽象预发树翻译为 mybatis xml，无法翻译的情况将抛出{@link TranslateException}。
     *
     * @param node
     * @param methodInfo
     * @throws TranslateException
     */
    protected abstract void doTranslate(ASTNode node, MethodInfo methodInfo);

    private ASTNode parse(String methodName) {
        List<Token> tokens = lexer.analyse(methodName);
        TokenIterator tokenIterator = new TokenIterator(tokens.iterator());
        ASTNode node = parser.parse(tokenIterator);
        return node;
    }

    private Document parse(InputStream input) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            db.setEntityResolver(LocalEntityResolver.getInstance());
            return db.parse(input);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new ParseException(e);
        }
    }

    private Document newDocument(String namespace) {
        try {
            DocumentBuilderFactory dbf= DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            db.setEntityResolver(LocalEntityResolver.getInstance());
            Document document = db.newDocument();
            DocumentType docType = document.getImplementation().createDocumentType("mapper", "-//mybatis.org//DTD Mapper 3.0//EN", "http://mybatis.org/dtd/mybatis-3-mapper.dtd");
            document.appendChild(docType);
            Element mapperElement = document.createElement("mapper");
            mapperElement.setAttribute("namespace", namespace);
            document.appendChild(mapperElement);
            return document;
        } catch (ParserConfigurationException e) {
            throw new ParseException(e);
        }
    }

    private Element getMapperElement(String namespace) {
        Element documentElement = document.getDocumentElement();
        if (documentElement == null || !"mapper".equals(documentElement.getTagName())) {
            throw new ParseException("Mapper element is not exists");
        }

        String docNs = documentElement.getAttribute("namespace");
        if (!namespace.equals(docNs)) {
            throw new ParseException("Namespace[%s] does not match %s", namespace, docNs);
        }

        return documentElement;
    }

    public Document getDocument() {
        return document;
    }

    protected boolean isSelective(ASTNode node) {
        for (int i = 0; i < node.childrenSize(); i++) {
            if (node.getChild(i) instanceof Selective) {
                return true;
            }
        }
        return false;
    }

    protected boolean existsStmtElement(String id) {
        if (existingIdsCache == null) {
            existingIdsCache = new HashSet<>();
            NodeList childNodes = mapperElement.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node node = childNodes.item(i);
                String tagName;
                if (node != null && (
                        TAG_INSERT.equals(tagName = node.getNodeName())
                                || TAG_DELETE.equals(tagName)
                                || TAG_UPDATE.equals(tagName)
                                || TAG_SELECT.equals(tagName))) {
                    NamedNodeMap attributes = node.getAttributes();
                    Node idAttr = attributes.getNamedItem("id");
                    if (idAttr != null) {
                        existingIdsCache.add(idAttr.getNodeValue());
                    }
                }
            }
        }

        return existingIdsCache.contains(id);
    }

    protected StmtElement addInsertElement(String id, String parameterType, BeanField useGeneratedKeysField) {
        StmtElement insertStmtElement = addStmtElement(TAG_INSERT, id);
        Element insertElement = insertStmtElement.getElement();
        insertElement.setAttribute("parameterType", parameterType);
        if (useGeneratedKeysField != null) {
            insertElement.setAttribute("useGeneratedKeys", "true");
            insertElement.setAttribute("keyProperty", useGeneratedKeysField.getName());
        }
        return insertStmtElement;
    }

    protected StmtElement addDeleteElement(String id, String parameterType) {
        StmtElement deleteStmtElement = addStmtElement(TAG_DELETE, id);
        if (parameterType != null) {
            deleteStmtElement.getElement().setAttribute("parameterType", parameterType);
        }
        return deleteStmtElement;
    }

    protected StmtElement addUpdateElement(String id, String parameterType) {
        StmtElement updateStmtElement = addStmtElement(TAG_UPDATE, id);
        updateStmtElement.getElement().setAttribute("parameterType", parameterType);
        return updateStmtElement;
    }

    protected StmtElement addSelectElement(String id, String parameterType, Return ret) {
        String retType = ret.getType();
        String resultMapId;
        if (ret.hasTypeHandler()) {
            // if result map stmt not exists, create it
            if (!appendedIdMap.containsKey(resultMapId = buildResultMapId(retType))) {
                StmtElement resultMapStmtElement = addStmtElement("resultMap", resultMapId);
                Element resultMapElement = resultMapStmtElement.getElement();
                resultMapElement.setAttribute("type", ret.getType());
                // fix mybatis schema (constructor?,id*,result*,association*,collection*,discriminator?)
                List<BeanField> sortedBfs = ret.getBeanFields().stream()
                    .sorted(Comparator.comparing(bf -> bf.isId() ? 0 : 1)).collect(Collectors.toList());
                for (BeanField bf : sortedBfs) {
                    Element itemElement;
                    if (bf.isId()) {
                        addTextNode(resultMapElement, LF, INDENT);
                        itemElement = addElement(resultMapElement, "id");
                    } else {
                        addTextNode(resultMapElement, LF, INDENT);
                        itemElement = addElement(resultMapElement, "result");
                    }
                    itemElement.setAttribute("property", bf.getName());
                    // fix column has been converted using "as", so we must use "getName" instead of "getFieldName".
                    itemElement.setAttribute("column", bf.getName());
                    if (bf.getTypeHandler() != null) {
                        itemElement.setAttribute("typeHandler", bf.getTypeHandler());
                    }
                }
                resultMapStmtElement.append();
            }
            StmtElement selectStmtElement = addStmtElement(TAG_SELECT, id);
            if (parameterType != null) {
                selectStmtElement.getElement().setAttribute("parameterType", parameterType);
            }
            selectStmtElement.getElement().setAttribute("resultMap", resultMapId);
            return selectStmtElement;
        } else {
            return addSelectElement(id, parameterType, ret.getType());
        }
    }

    protected StmtElement addSelectElement(String id, String parameterType, String resultType) {
        StmtElement selectStmtElement = addStmtElement(TAG_SELECT, id);
        if (parameterType != null) {
            selectStmtElement.getElement().setAttribute("parameterType", parameterType);
        }
        selectStmtElement.getElement().setAttribute("resultType", resultType);
        return selectStmtElement;
    }

    protected void addTextNode(Element parent, CharSequence... texts) {
        StringBuilder sb = new StringBuilder();
        for (CharSequence cs : texts) {
            sb.append(cs);
        }

        Text textNode = document.createTextNode(sb.toString());
        parent.appendChild(textNode);
    }

    protected Element addElement(Element parent, String tagName) {
        Element element = document.createElement(tagName);
        parent.appendChild(element);
        return element;
    }

    private String buildResultMapId(String retType) {
        return retType.replace(".", "_");
    }

    private StmtElement addStmtElement(String tagName, String id) {
        Comment comment = document.createComment("auto mapper generate");
        Element element = document.createElement(tagName);
        element.setAttribute("id", id);
        return new StmtElement(element, () -> {
            if (appendedIdMap.containsKey(id)) {
                throw new TranslateException("'%s' id duplicated", id);
            }
            mapperElement.appendChild(document.createTextNode(LF_LF));
            mapperElement.appendChild(comment);
            mapperElement.appendChild(document.createTextNode(LF));
            mapperElement.appendChild(element);
            appendedIdMap.put(id, element);
        });
    }

    protected String indent(int indentCount) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indentCount; i++) {
            sb.append(INDENT);
        }
        return sb.toString();
    }

    protected class StmtElement {

        private final Element element;
        private final Runnable lazyAppend;

        public StmtElement(Element element, Runnable lazyAppend) {
            this.element = element;
            this.lazyAppend = lazyAppend;
        }

        public Element getElement() {
            return element;
        }

        public void append() {
            lazyAppend.run();
        }
    }

}
