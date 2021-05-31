package fun.fengwk.automapper.processor.util;

import fun.fengwk.automapper.processor.AutoMapperException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

/**
 * @author fengwk
 */
public class DOMUtils {

    private DOMUtils() {}

    public static String toString(Document document) {
        try (StringWriter writer = new StringWriter()) {
            write(document, writer);
            String str = writer.toString();
            str = str.replaceAll("\\r\\n", "\n");
            if (str.endsWith("\n")) {
                str = str.substring(0, str.length() - 1);
            }
            return str;
        } catch (IOException e) {
            throw new AutoMapperException(e);
        }
    }

    private static void write(Document document, Writer writer) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DocumentType docType = document.getDoctype();
            transformer.setOutputProperty(OutputKeys.ENCODING, StandardCharsets.UTF_8.name());
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            if (docType != null) {
                transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, docType.getPublicId());
                transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, docType.getSystemId());
            }
            DOMSource source = new DOMSource(document);
            StreamResult streamResult = new StreamResult(writer);
            transformer.transform(source, streamResult);
        } catch (TransformerException e) {
            throw new AutoMapperException(e);
        }
    }

}
