package fun.fengwk.automapper.processor.naming;

import fun.fengwk.automapper.processor.util.StringUtils;

/**
 * @author fengwk
 */
public class TableNamingConvert implements NamingConverter {

    private final NamingConverter delegate;
    private final String mapperName;
    private final String mapperSuffix;
    private final String tableNamePrefix;
    private final String tableNameSuffix;
    private final String quote;

    public TableNamingConvert(NamingConverter delegate, String mapperName, String mapperSuffix,
                              String tableNamePrefix, String tableNameSuffix, String quote) {
        this.delegate = delegate;
        this.mapperName = mapperName;
        this.mapperSuffix = mapperSuffix;
        this.tableNamePrefix = tableNamePrefix;
        this.tableNameSuffix = tableNameSuffix;
        this.quote = quote;
    }

    @Override
    public String convert(String tableName) {
        // 如果没有指定表名那么通过Mapper名称生成表名
        if (tableName == null || tableName.isEmpty()) {
            tableName = mapperNameToTableName();
        }

        // 表名转换
        tableName = delegate.convert(tableName);

        // 添加前缀
        if (tableNamePrefix != null && !tableNamePrefix.isEmpty()) {
            tableName = tableNamePrefix + tableName;
        }
        // 添加后缀
        if (tableNameSuffix != null && !tableNameSuffix.isEmpty()) {
            tableName = tableName + tableNameSuffix;
        }

        return quoteQualifiedIdentifier(tableName);
    }

    // Mapper名称转表名
    private String mapperNameToTableName() {
        String tableName = StringUtils.trimSuffix(mapperName, mapperSuffix);
        tableName = StringUtils.upperCamelToLowerCamel(tableName);
        return delegate.convert(tableName);
    }

    private String quoteQualifiedIdentifier(String identifier) {
        if (identifier == null || identifier.isEmpty()) {
            return identifier;
        }
        if (isQuoted(identifier)) {
            return identifier;
        }

        String[] identifierParts = identifier.split("\\.", -1);
        if (identifierParts.length == 1) {
            return quoteIdentifier(identifier);
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < identifierParts.length; i++) {
            if (i > 0) {
                sb.append('.');
            }

            String identifierPart = identifierParts[i];
            if (!identifierPart.isEmpty()) {
                sb.append(quoteIdentifier(identifierPart));
            }
        }
        return sb.toString();
    }

    private String quoteIdentifier(String identifier) {
        return isQuoted(identifier) ? identifier : quote + identifier + quote;
    }

    private boolean isQuoted(String identifier) {
        return (identifier.startsWith("`") && identifier.endsWith("`"))
            || (identifier.startsWith("\"") && identifier.endsWith("\""));
    }

}
