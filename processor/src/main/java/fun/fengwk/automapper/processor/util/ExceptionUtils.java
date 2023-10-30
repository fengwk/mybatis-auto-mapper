package fun.fengwk.automapper.processor.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * @author fengwk
 */
public class ExceptionUtils {

    private ExceptionUtils() {}

    public static String getStackTrace(Throwable e) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            try (PrintStream ps = new PrintStream(out, true, "utf-8")) {
                e.printStackTrace(ps);
                return new String(out.toByteArray(), StandardCharsets.UTF_8);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}
