package fun.fengwk.automapper.processor.mapper;

import fun.fengwk.automapper.annotation.AutoMapper;
import fun.fengwk.automapper.annotation.DBType;
import fun.fengwk.automapper.annotation.NamingStyle;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 全局配置。
 *
 * @author fengwk
 */
public class GlobalConfig {

    public static final String RESOURCE_PATH = "auto-mapper.config";

    private static final String DB_TYPE = AutoMapper.class.getName() + ".dbType";
    private static final String MAPPER_SUFFIX = AutoMapper.class.getName() + ".mapperSuffix";
    private static final String TABLE_NAMING_STYLE = AutoMapper.class.getName() + ".tableNamingStyle";
    private static final String FIELD_NAMING_STYLE = AutoMapper.class.getName() + ".fieldNamingStyle";
    private static final String TABLE_NAME_PREFIX = AutoMapper.class.getName() + ".tableNamePrefix";
    private static final String TABLE_NAME_SUFFIX = AutoMapper.class.getName() + ".tableNameSuffix";

    private final Properties config = new Properties();

    public void load(InputStream inputStream) throws IOException {
        config.load(inputStream);
    }

    public DBType getDBType() {
        String dbType = config.getProperty(DB_TYPE);
        if (dbType != null) {
            try {
                return DBType.valueOf(dbType);
            } catch (IllegalArgumentException ignore) {}
        }
        return null;
    }

    public String getMapperSuffix() {
        return config.getProperty(MAPPER_SUFFIX);
    }

    public NamingStyle getTableNamingStyle() {
        String tableNamingStyle = config.getProperty(TABLE_NAMING_STYLE);
        if (tableNamingStyle != null) {
            try {
                return NamingStyle.valueOf(tableNamingStyle);
            } catch (IllegalArgumentException ignore) {}
        }
        return null;
    }

    public NamingStyle getFieldNamingStyle() {
        String fieldNamingStyle = config.getProperty(FIELD_NAMING_STYLE);
        if (fieldNamingStyle != null) {
            try {
                return NamingStyle.valueOf(fieldNamingStyle);
            } catch (IllegalArgumentException ignore) {}
        }
        return null;
    }

    public String getTableNamePrefix() {
        return config.getProperty(TABLE_NAME_PREFIX);
    }

    public String getTableNameSuffix() {
        return config.getProperty(TABLE_NAME_SUFFIX);
    }

}
