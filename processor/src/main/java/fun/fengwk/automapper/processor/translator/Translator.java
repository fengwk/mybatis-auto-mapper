package fun.fengwk.automapper.processor.translator;

import fun.fengwk.automapper.processor.lexer.Lexer;
import fun.fengwk.automapper.processor.lexer.Token;
import fun.fengwk.automapper.processor.naming.NamingConverter;
import fun.fengwk.automapper.processor.parser.ParseException;
import fun.fengwk.automapper.processor.parser.Parser;
import fun.fengwk.automapper.processor.parser.TokenIterator;
import fun.fengwk.automapper.processor.parser.ast.ASTNode;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author fengwk
 */
public abstract class Translator {

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

    private Set<String> existingIdsCache;

    private Lexer lexer = new Lexer();
    private Parser parser = new Parser();

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
     * 将方法信息翻译为 mybatis xml，无法翻译的情况将抛出{@link TranslateException}。
     *
     * @param methodInfo
     */
    public void translate(MethodInfo methodInfo) {
        ASTNode node = parse(methodInfo.getMethodName());
        doTranslate(node, methodInfo);
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
            return db.parse(input);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new ParseException(e);
        }
    }

    private Document newDocument(String namespace) {
        try {
            DocumentBuilderFactory dbf= DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
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

    protected boolean existsStmtElement(String id) {
        if (existingIdsCache == null) {
            existingIdsCache = new HashSet<>();
            NodeList childNodes = mapperElement.getChildNodes();
            if (childNodes != null) {
                for (int i = 0; i < childNodes.getLength(); i++) {
                    Node node = childNodes.item(i);
                    String tagName;
                    if (node != null && (
                            TAG_INSERT.equals(tagName = node.getNodeName())
                                    || TAG_DELETE.equals(tagName)
                                    || TAG_UPDATE.equals(tagName)
                                    || TAG_SELECT.equals(tagName))) {
                        NamedNodeMap attributes = node.getAttributes();
                        if (attributes != null) {
                            Node idAttr = attributes.getNamedItem("id");
                            if (idAttr != null) {
                                existingIdsCache.add(idAttr.getNodeValue());
                            }
                        }
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

    private StmtElement addStmtElement(String tagName, String id) {
        Comment comment = document.createComment("auto mapper generate");
        Element element = document.createElement(tagName);
        element.setAttribute("id", id);
        return new StmtElement(element, () -> {
            mapperElement.appendChild(document.createTextNode(LF_LF));
            mapperElement.appendChild(comment);
            mapperElement.appendChild(document.createTextNode(LF));
            mapperElement.appendChild(element);
        });
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
