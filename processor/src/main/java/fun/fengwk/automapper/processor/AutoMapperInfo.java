package fun.fengwk.automapper.processor;

import fun.fengwk.automapper.annotation.AutoMapper;
import fun.fengwk.automapper.annotation.DBType;
import fun.fengwk.automapper.annotation.NamingStyle;
import fun.fengwk.automapper.processor.mapper.GlobalConfig;

/**
 *
 * @see AutoMapper
 * @author fengwk
 */
public class AutoMapperInfo {

    private DBType dbType;
    private String mapperSuffix;
    private NamingStyle tableNamingStyle;
    private NamingStyle fieldNamingStyle;
    private String tableName;

    public AutoMapperInfo(AutoMapper autoMapper) {
        this.dbType = autoMapper.dbType();
        this.mapperSuffix = autoMapper.mapperSuffix();
        this.tableNamingStyle = autoMapper.tableNamingStyle();
        this.fieldNamingStyle = autoMapper.fieldNamingStyle();
        this.tableName = autoMapper.tableName();
    }

    /**
     * 优先使用全局配置。
     *
     * @param globalConfig
     */
    public void preferGlobalConfig(GlobalConfig globalConfig) {
        DBType dbType = globalConfig.getDBType();
        String mapperSuffix = globalConfig.getMapperSuffix();
        NamingStyle tableNamingStyle = globalConfig.getTableNamingStyle();
        NamingStyle fieldNamingStyle = globalConfig.getFieldNamingStyle();

        if (dbType != null) {
            this.dbType = dbType;
        }
        if (mapperSuffix != null) {
            this.mapperSuffix = mapperSuffix;
        }
        if (tableNamingStyle != null) {
            this.tableNamingStyle = tableNamingStyle;
        }
        if (fieldNamingStyle != null) {
            this.fieldNamingStyle = fieldNamingStyle;
        }
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
