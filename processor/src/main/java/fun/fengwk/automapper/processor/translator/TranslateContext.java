package fun.fengwk.automapper.processor.translator;

import fun.fengwk.automapper.processor.naming.NamingConverter;

import java.io.InputStream;

/**
 * @author fengwk
 */
public class TranslateContext {

    private final String namespace;
    private final String tableName;
    private final NamingConverter fieldNamingConverter;
    private final InputStream input;

    public TranslateContext(String namespace, String tableName, NamingConverter fieldNamingConverter) {
        this(namespace, tableName, fieldNamingConverter, null);
    }

    public TranslateContext(String namespace, String tableName, NamingConverter fieldNamingConverter, InputStream input) {
        this.namespace = namespace;
        this.tableName = tableName;
        this.fieldNamingConverter = fieldNamingConverter;
        this.input = input;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getTableName() {
        return tableName;
    }

    public NamingConverter getFieldNamingConverter() {
        return fieldNamingConverter;
    }

    public InputStream getInput() {
        return input;
    }
}
