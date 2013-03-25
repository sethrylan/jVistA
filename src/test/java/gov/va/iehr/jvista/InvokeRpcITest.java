package gov.va.iehr.jvista;

import com.vistacowboy.jVista.RpcParameter;
import com.vistacowboy.jVista.VistaConnection;
import com.vistacowboy.jVista.VistaException;
import com.vistacowboy.jVista.VistaRpc;
import com.vistacowboy.jVista.VistaSelect;
import com.vistacowboy.jVista.VistaUser;
import gov.va.common.TestUtils;
import gov.va.common.VistAResource;
import gov.va.common.xml.XMLValidation;
import static junit.framework.Assert.*;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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
    public void testSelectOnPatientFile() {
        VistaSelect select = new VistaSelect();
        select.setFile("2");
//        select.setFields(".01");  // uncomment for patient full names
        String[][] result = null;
        try {
            result = select.find(connection);
        } catch (VistaException ex) {
            logger.error(null, ex);
        }
//        TestUtils.printMatrix(result);
        assertTrue("There should be over 100 patients.", result.length > 100);
    }
    
    @Test
    public void testNhinPatientResults()  {
        RpcParameter dfn, id;
        try {
            dfn = new RpcParameter(RpcParameter.LITERAL, "2");
            id = new RpcParameter(RpcParameter.LITERAL, NhinDomain.PATIENT.getId());
            String preparedRpc = VistaRpc.prepare("NHIN GET VISTA DATA", new RpcParameter[]{dfn,id});
            String result = connection.exec(preparedRpc);
            Document document = null;
            try {
                document = TestUtils.getDom(result);
            } catch (SAXException ex) {
                fail("XML could not be parsed:" + result);
            }
            System.out.println(TestUtils.getPrettyPrintDocument(document));
            NodeList resultsNodes = document.getElementsByTagName("results");
            assertEquals("There should be only one results node.", 1, resultsNodes.getLength());
            Node resultNode = resultsNodes.item(0);
            assertEquals("1", resultNode.getAttributes().getNamedItem("total").getNodeValue());
        } catch (VistaException ex) {
            logger.error(null, ex);
        }
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
    public void testNhinVitalResultsAreWellFormed()  {
        RpcParameter dfn, id;
        try {
            dfn = new RpcParameter(RpcParameter.LITERAL, "2");
            id = new RpcParameter(RpcParameter.LITERAL, NhinDomain.VITAL.getId());
            String preparedRpc = VistaRpc.prepare("NHIN GET VISTA DATA", new RpcParameter[]{dfn,id});
            String result = connection.exec(preparedRpc);
            Document document = null;
            try {
                document = TestUtils.getDom(result);
            } catch (SAXException ex) {
                fail("XML could not be parsed:" + result);
            }
            //System.out.println(TestUtils.getPrettyPrintDocument(document));
            NodeList resultsNodes = document.getElementsByTagName("results");
            assertEquals("There should be only one results node.", 1, resultsNodes.getLength());
            Node resultNode = resultsNodes.item(0);
            assertEquals("1", resultNode.getAttributes().getNamedItem("total").getNodeValue());
        } catch (VistaException ex) {
            logger.error(null, ex);
        }
    }
    
    
   
    @Test
    @Ignore
    public void printNhinDomains() {
        NhinDomain[] domainList = {NhinDomain.ACCESSION, NhinDomain.ALLERGY, NhinDomain.APPOINTMENT, 
            NhinDomain.CONSULT, NhinDomain.IMMUNIZATION, NhinDomain.LAB, NhinDomain.MED, 
            NhinDomain.ORDER,NhinDomain.PANEL, NhinDomain.PATIENT, NhinDomain.PROBLEM, 
            NhinDomain.PROCEDURE, NhinDomain.RADIOOLOGY, NhinDomain.RX, NhinDomain.SURGERY, 
            NhinDomain.VISIT, NhinDomain.VITAL};

        for(NhinDomain domain : domainList) {
            System.out.println("================================================");
            System.out.println("================" + domain.name() + "================");
            System.out.println("================================================");
            RpcParameter dfn, id;
            try {
                dfn = new RpcParameter(RpcParameter.LITERAL, "1");
                id = new RpcParameter(RpcParameter.LITERAL, domain.getId());
                String preparedRpc = VistaRpc.prepare("NHIN GET VISTA DATA", new RpcParameter[]{dfn,id});
                String result = connection.exec(preparedRpc);
                try {
                    Document document = TestUtils.getDom(result);
                    result = TestUtils.getPrettyPrintDocument(document);
                } catch (SAXException ex) {
                    System.out.println("XML could not be parsed:" + result);
                }
                System.out.println(result.substring(0, Math.min(1500, result.length())));
            } catch (VistaException ex) {
                logger.error(null, ex);
            }
        }
    }
}
