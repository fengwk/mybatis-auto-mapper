package fun.fengwk.automapper.processor.util;

import fun.fengwk.automapper.processor.util.LocalEntityResolver;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author fengwk
 */
public class LocalEntityResolverTest {

    @Test
    public void test() throws ParserConfigurationException, IOException, SAXException {
        URL url = ClassLoader.getSystemResource("fun/fengwk/automapper/example/mapper/ExampleMapper.xml");
        URLConnection conn = url.openConnection();
        InputStream input = conn.getInputStream();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        db.setEntityResolver(new LocalEntityResolver());
        db.parse(input);
        input.close();
    }

}
