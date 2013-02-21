package gov.va.iehr.jvista;

import com.vistacowboy.jVista.RpcParameter;
import com.vistacowboy.jVista.VistaConnection;
import com.vistacowboy.jVista.VistaException;
import com.vistacowboy.jVista.VistaRpc;
import com.vistacowboy.jVista.VistaSelect;
import com.vistacowboy.jVista.VistaUser;
import gov.va.common.VistAResource;
import gov.va.common.xml.XMLValidation;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import static junit.framework.Assert.*;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author gaineys
 */
public class InvokeRpcITest {

    private Logger logger = LoggerFactory.getLogger(InvokeRpcITest.class);
    VistaConnection connection = null;

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        connection = new VistaConnection(VistAResource.getAddress(), VistAResource.getPort());
        try {
            connection.connect();
        } catch (VistaException ex) {
            logger.error(null, ex);
        }
        VistaUser user = new VistaUser();
        String access_code = VistAResource.getAccessCode();
        String verify_code = VistAResource.getVerifyCode();
        String context = "NHIN APPLICATION PROXY";
        try {
            user.login(connection, access_code, verify_code, context);
        } catch (VistaException ex) {
            logger.error(null, ex);
        }
    }

    @After
    public void tearDown() {
        try {
            connection.disconnect();
        } catch (VistaException ex) {
            logger.error(null, ex);
        }
        connection = null;
    }

    @Test
    public void selectTest() {
        VistaSelect select = new VistaSelect();
        select.setFile("2");
        String[][] result = null;
        try {
            result = select.find(connection);
        } catch (VistaException ex) {
            logger.error(null, ex);
        }
        assertTrue("There should be over 100 patients.", result.length > 100);
//        printMatrix(result);
    }

    /**
     * Test NHIN GET VISTA DATA RPC (Pre-v1.0 of VPR package RPCs)
     * M source to NHINV: NHIN GET VISTA DATA: https://github.com/OSEHRA/VistA-FOIA/blob/master/Packages/National%20Health%20Information%20Network/Routines/NHINV.m
     * Also source to NHINV: http://wbvista.info/VDOCS/RoutinesPlus/RFRAME.php?ROUTINE=NHINV
     * M source to NHINV: http://wbvista.info/VDOCS/RoutinesPlus/RFRAME.php?ROUTINE=NHINVTIU
     * NHIN GET VISTA DATA: http://livevista.caregraf.info/rambler#!8994-3140
     * VDL Documentation: http://www.va.gov/vdl/documents/Clinical/nationwide_health_info_net/nhin_tm.pdf
     */
    @Test
    public void nhinVitalRpcTest() throws ClassNotFoundException {
        RpcParameter dfn, id;
        try {
            // Routine parameters are GET(NHIN,DFN,TYPE,START,STOP,MAX,ID)
            dfn = new RpcParameter(RpcParameter.LITERAL, "1");
            id = new RpcParameter(RpcParameter.LITERAL, NhinDomain.VITAL.getId());
            String preparedRpc = VistaRpc.prepare("NHIN GET VISTA DATA", new RpcParameter[]{dfn,id});
            String result = connection.exec(preparedRpc);
//            System.out.println(result);
            assertTrue("XML result\n\n: " + result + "\n\n...could not be parsed.", XMLValidation.isXMLWellFormed(result));            
            Document document = null;
            try {
                document = stringToDom(result);
            } catch (SAXException ex) {
                logger.error(null, ex);
            } catch (ParserConfigurationException ex) {
                logger.error(null, ex);
            } catch (IOException ex) {
                logger.error(null, ex);
            }
            
            System.out.println(getPrettyPrintDocument(document));
            NodeList resultsNodes = document.getElementsByTagName("results");
            assertEquals("There should be only one results node.", 1, resultsNodes.getLength());
            Node resultNode = resultsNodes.item(0);
            assertEquals("1", resultNode.getAttributes().getNamedItem("total").getNodeValue());
        } catch (VistaException ex) {
            logger.error(null, ex);
        }
    }
    
    
    @Test
    public void nhinRpcTest() {
        RpcParameter dfn, id;
        try {
            dfn = new RpcParameter(RpcParameter.LITERAL, "2");
            NhinDomain[] domainList = {NhinDomain.ACCESSION, NhinDomain.ALLERGY, NhinDomain.APPOINTMENT, 
                                        NhinDomain.CONSULT, NhinDomain.IMMUNIZATION, NhinDomain.LAB, 
                                        NhinDomain.MED, NhinDomain.ORDER,NhinDomain.PANEL, NhinDomain.PATIENT, 
                                        NhinDomain.PROBLEM, NhinDomain.PROCEDURE, NhinDomain.RADIOOLOGY, 
                                        NhinDomain.RX, NhinDomain.SURGERY, NhinDomain.VISIT, NhinDomain.VITAL};
            for(NhinDomain nhinDomain : domainList) {
                id = new RpcParameter(RpcParameter.LITERAL, nhinDomain.getId());
                String preparedRpc = VistaRpc.prepare("NHIN GET VISTA DATA", new RpcParameter[]{dfn,id});
                String result = connection.exec(preparedRpc);
                assertTrue("XML result for dfn=" + dfn.getValue() + "; domain=" + nhinDomain.getId() + " could not be parsed: " + result + ".", XMLValidation.isXMLWellFormed(result));            
                Document document = null;
                try {
                    document = stringToDom(result);
                } catch (SAXException ex) {
                    logger.error(null, ex);
                } catch (ParserConfigurationException ex) {
                    logger.error(null, ex);
                } catch (IOException ex) {
                    logger.error(null, ex);
                }
                NodeList resultsNodes = document.getElementsByTagName("results");
                assertEquals("There should be only one results node.", 1, resultsNodes.getLength());
            }
        } catch (VistaException ex) {
            logger.error(null, ex);
        }
    }

    
    
    /**
     * Test of VPR GET PATIENT DATA RPC
     * VPR GET PATIENT DATA: http://livevista.caregraf.info/rambler#!8994-3141
     * VPR DATA VERSION: http://livevista.caregraf.info/rambler#!8994-3142
     * MDO example of VPR GET PATIENT DATA: https://gitorious.org/osehra/mdo/blobs/1ed36e99c47b181447ea04d22a281c620e79a6a4/mdo/src/mdo/dao/vista/VistaClinicalDao.cs
     * M source to VPR GET PATIENT DATA: http://wbvista.info/VDOCS/RoutinesPlus/RFRAME.php?ROUTINE=VPRD
     * VDL Documentation: http://www.va.gov/vdl/application.asp?appid=197
     */
    @Test
    @Ignore
    public void vprRpcTest() {
        VistaRpc rpc = new VistaRpc();
        RpcParameter rpcParam1 = null;
        RpcParameter rpcParam2 = null;
        try {
            String dfn = "2";
            rpcParam1 = new RpcParameter(RpcParameter.LITERAL, dfn);
            rpcParam2 = new RpcParameter(RpcParameter.LITERAL, "");  // all types
            String preparedRpc = rpc.prepare("VPR GET PATIENT DATA", new RpcParameter[]{rpcParam1});
            connection.exec(preparedRpc);
        } catch (VistaException ex) {
            logger.error(null, ex);
        }
    }

    
    private void printMatrix(Object[][] array) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                System.out.print(array[i][j]);
            }
            System.out.println();
        }
    }
    
    private static Document stringToDom(String xmlSource) throws SAXException, ParserConfigurationException, IOException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder builder = docFactory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(xmlSource)));
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
