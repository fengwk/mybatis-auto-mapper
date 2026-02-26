package fun.fengwk.automapper.processor.translator;

import fun.fengwk.automapper.annotation.DBType;
import fun.fengwk.automapper.processor.naming.LowerUnderScoreCaseConverter;
import fun.fengwk.automapper.processor.translator.mysql.MySqlTranslator;
import fun.fengwk.automapper.processor.translator.postgresql.PostgreSqlTranslator;
import fun.fengwk.automapper.processor.translator.sqlite.SQLiteTranslator;
import org.junit.Test;

/**
 * @author fengwk
 */
public class TranslatorFactoryTest {

    @Test
    public void testGetSql92Translator() {
        Translator translator = TranslatorFactory.getInstance(
            DBType.SQL92,
            new TranslateContext("demo", "demo", new LowerUnderScoreCaseConverter())
        );
        assert translator instanceof Sql92Translator;
    }

    @Test
    public void testGetMySqlTranslator() {
        Translator translator = TranslatorFactory.getInstance(
            DBType.MYSQL,
            new TranslateContext("demo", "demo", new LowerUnderScoreCaseConverter())
        );
        assert translator instanceof MySqlTranslator;
    }

    @Test
    public void testGetPostgreSqlTranslator() {
        Translator translator = TranslatorFactory.getInstance(
            DBType.POSTGRESQL,
            new TranslateContext("demo", "demo", new LowerUnderScoreCaseConverter())
        );
        assert translator instanceof PostgreSqlTranslator;
    }

    @Test
    public void testGetSQLiteTranslator() {
        Translator translator = TranslatorFactory.getInstance(
            DBType.SQLITE,
            new TranslateContext("demo", "demo", new LowerUnderScoreCaseConverter())
        );
        assert translator instanceof SQLiteTranslator;
    }

}
