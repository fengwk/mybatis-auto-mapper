package fun.fengwk.automapper.processor.naming;

import org.junit.Test;

/**
 * @author fengwk
 */
public class FieldNamingConvertTest {

    @Test
    public void testConvert() {
        FieldNamingConvert converter = new FieldNamingConvert(new LowerUnderScoreCaseConverter(), "\"");
        assert "\"user_name\"".equals(converter.convert("userName"));
    }

}
