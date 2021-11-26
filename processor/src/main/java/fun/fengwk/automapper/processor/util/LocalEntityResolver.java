package fun.fengwk.automapper.processor.util;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

/**
 * 本地EntityResolver，将读取resources下同文件名的文件，以避免网络问题导致的超时。
 *
 * @author fengwk
 */
public class LocalEntityResolver implements EntityResolver {

    private static final char SPLIT = '/';

    private static final LocalEntityResolver INSTANCE = new LocalEntityResolver();

    public static LocalEntityResolver getInstance() {
        return INSTANCE;
    }

    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        if (systemId != null) {
            int index = systemId.lastIndexOf(SPLIT);
            if (index != -1) {
                systemId = systemId.substring(index + 1);
            }
            InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(systemId);
            if (input != null) {
                return new InputSource(input);
            }
        }
        return null;
    }

}
