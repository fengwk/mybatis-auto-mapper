package fun.fengwk.automapper.processor;

import fun.fengwk.automapper.annotation.AutoMapper;
import fun.fengwk.automapper.annotation.DBType;
import fun.fengwk.automapper.annotation.NamingStyle;
import fun.fengwk.automapper.processor.mapper.GlobalConfig;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import java.util.Map;

/**
 *
 * @see AutoMapper
 * @author fengwk
 */
public class AutoMapperInfo {

    private final DBType dbType;
    private final String mapperSuffix;
    private final NamingStyle tableNamingStyle;
    private final NamingStyle fieldNamingStyle;
    private final String tableName;

    private AutoMapperInfo(DBType dbType, String mapperSuffix, NamingStyle tableNamingStyle,
                          NamingStyle fieldNamingStyle, String tableName) {
        this.dbType = dbType;
        this.mapperSuffix = mapperSuffix;
        this.tableNamingStyle = tableNamingStyle;
        this.fieldNamingStyle = fieldNamingStyle;
        this.tableName = tableName;
    }

    public static AutoMapperInfo parse(AutoMapper autoMapper, AnnotationMirror autoMapperMirror, GlobalConfig globalConfig) {
        // 规则：用户注解明确设置 > 全局配置 > 默认设置

        DBType dbType = autoMapper.dbType();
        String mapperSuffix = autoMapper.mapperSuffix();
        NamingStyle tableNamingStyle = autoMapper.tableNamingStyle();
        NamingStyle fieldNamingStyle = autoMapper.fieldNamingStyle();
        String tableName = autoMapper.tableName();

        DBType globalDbType = globalConfig.getDBType();
        String globalMapperSuffix = globalConfig.getMapperSuffix();
        NamingStyle globalTableNamingStyle = globalConfig.getTableNamingStyle();
        NamingStyle globalFieldNamingStyle = globalConfig.getFieldNamingStyle();

        if (globalDbType != null && !isExplicit(autoMapperMirror, "dbType")) {
            dbType = globalDbType;
        }
        if (globalMapperSuffix != null && !isExplicit(autoMapperMirror, "mapperSuffix")) {
            mapperSuffix = globalMapperSuffix;
        }
        if (globalTableNamingStyle != null && !isExplicit(autoMapperMirror, "tableNamingStyle")) {
            tableNamingStyle = globalTableNamingStyle;
        }
        if (globalFieldNamingStyle != null && !isExplicit(autoMapperMirror, "fieldNamingStyle")) {
            fieldNamingStyle = globalFieldNamingStyle;
        }

        return new AutoMapperInfo(dbType, mapperSuffix, tableNamingStyle, fieldNamingStyle, tableName);
    }

    // 检查注解方法是否被用户明确设置了
    private static boolean isExplicit(AnnotationMirror autoMapperMirror, String methodName) {
        Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = autoMapperMirror.getElementValues();
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> e : elementValues.entrySet()) {
            if (methodName.equals(e.getKey().getSimpleName().toString())) {
                return true;
            }
        }
        return false;
    }

    public DBType getDbType() {
        return dbType;
    }

    public String getMapperSuffix() {
        return mapperSuffix;
    }

    public NamingStyle getTableNamingStyle() {
        return tableNamingStyle;
    }

    public NamingStyle getFieldNamingStyle() {
        return fieldNamingStyle;
    }

    public String getTableName() {
        return tableName;
    }
}
