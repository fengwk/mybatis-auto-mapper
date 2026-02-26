package fun.fengwk.automapper.processor.naming;

import org.junit.Test;

/**
 * @author fengwk
 */
public class TableNamingConvertTest {

    @Test
    public void testConvertByMapperName() {
        TableNamingConvert converter = new TableNamingConvert(
            new LowerUnderScoreCaseConverter(),
            "UserMapper",
            "Mapper",
            "",
            "",
            "\""
        );
        assert "\"user\"".equals(converter.convert(""));
    }

    @Test
    public void testConvertQualifiedTableName() {
        TableNamingConvert converter = new TableNamingConvert(
            new LowerUnderScoreCaseConverter(),
            "UserMapper",
            "Mapper",
            "",
            "",
            "\""
        );
        assert "\"public\".\"user_info\"".equals(converter.convert("public.userInfo"));
    }

    @Test
    public void testConvertAlreadyQuotedTableName() {
        TableNamingConvert converter = new TableNamingConvert(
            new LowerUnderScoreCaseConverter(),
            "UserMapper",
            "Mapper",
            "",
            "",
            "\""
        );
        assert "\"public\".\"user\"".equals(converter.convert("\"public\".\"user\""));
    }

}
