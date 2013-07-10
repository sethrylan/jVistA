package gov.va.common;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author gaineys
 */
public class TestUtils {

    
    public static void printMatrix(Object[][] array) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                System.out.print(array[i][j]);
                System.out.print(" ");
            }
            System.out.println();
        }
    }
    
    public static Document getDom(String xmlSource) throws SAXException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder builder = null;
        Document document = null;
        try {
            builder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(TestUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            document = builder.parse(new InputSource(new StringReader(xmlSource)));
        } catch (IOException ex) {
            Logger.getLogger(TestUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return document;
    }

    public static String getPrettyPrintDocument(Document doc) {
        // see http://www.chipkillmar.net/2009/03/25/pretty-print-xml-from-a-dom/
        DOMImplementation domImplementation = doc.getImplementation();
        if (domImplementation.hasFeature("LS", "3.0") && domImplementation.hasFeature("Core", "2.0")) {
            DOMImplementationLS domImplementationLS = (DOMImplementationLS) domImplementation.getFeature("LS", "3.0");
            LSSerializer lsSerializer = domImplementationLS.createLSSerializer();
            DOMConfiguration domConfiguration = lsSerializer.getDomConfig();
            if (domConfiguration.canSetParameter("format-pretty-print", Boolean.TRUE)) {
                lsSerializer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);
                LSOutput lsOutput = domImplementationLS.createLSOutput();
                lsOutput.setEncoding("UTF-8");
                StringWriter stringWriter = new StringWriter();
                lsOutput.setCharacterStream(stringWriter);
                lsSerializer.write(doc, lsOutput);
                return stringWriter.toString();
            } else {
                throw new RuntimeException("DOMConfiguration 'format-pretty-print' parameter isn't settable.");
            }
        } else {
            throw new RuntimeException("DOM 3.0 LS and/or DOM 2.0 Core not supported.");
        }
    }

}
