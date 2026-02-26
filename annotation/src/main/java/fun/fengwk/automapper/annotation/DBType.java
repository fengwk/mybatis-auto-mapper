package fun.fengwk.automapper.annotation;

/**
 * @author fengwk
 */
public enum DBType {

    /**
     * 使用通用标准，生成的SQL语句支持各个支持SQL92标准的数据库。
     */
    SQL92,

    /**
     * 生成的语句部分仅支持MySQL语法。
     */
    MYSQL,

    /**
     * 生成的语句部分仅支持PostgreSQL语法。
     */
    POSTGRESQL,

    /**
     * 生成的语句部分仅支持SQLite语法。
     */
    SQLITE;

}
