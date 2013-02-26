package gov.va.iehr.jvista;

import com.vistacowboy.jVista.RpcParameter;
import com.vistacowboy.jVista.VistaConnection;
import com.vistacowboy.jVista.VistaException;
import com.vistacowboy.jVista.VistaRpc;
import com.vistacowboy.jVista.VistaUser;
import gov.va.common.TestUtils;
import gov.va.common.VistAResource;
import java.util.Collection;
import java.util.regex.Pattern;
import static junit.framework.Assert.*;
import org.javasimon.SimonManager;
import org.javasimon.Split;
import org.javasimon.Stopwatch;
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
public class InvokeVprRpcITest {

    private Logger logger = LoggerFactory.getLogger(InvokeRpcITest.class);
    VistaConnection connection = null;
    private static final long NANOSECONDS_PER_SECOND = 1000000000l;


    
    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        printStatistics();
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
        String context = "VPR APPLICATION PROXY";
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
    
    
    /**
     * Test of VPR GET PATIENT DATA RPC
     * VPR GET PATIENT DATA: http://livevista.caregraf.info/rambler#!8994-3141
     * VPR DATA VERSION: http://livevista.caregraf.info/rambler#!8994-3142
     * MDO example of VPR GET PATIENT DATA: https://gitorious.org/osehra/mdo/blobs/1ed36e99c47b181447ea04d22a281c620e79a6a4/mdo/src/mdo/dao/vista/VistaClinicalDao.cs
     * M source to VPR GET PATIENT DATA: http://wbvista.info/VDOCS/RoutinesPlus/RFRAME.php?ROUTINE=VPRD
     * VDL Documentation: http://www.va.gov/vdl/application.asp?appid=197
     */
    @Test
    public void testVprRpcVitalsReturnsResults() {
        RpcParameter dfn, id;
        try {
            dfn = new RpcParameter(RpcParameter.LITERAL, "2");
            id = new RpcParameter(RpcParameter.LITERAL, VprDomain.VITAL.getId());
            String preparedRpc = VistaRpc.prepare("VPR GET PATIENT DATA", new RpcParameter[]{dfn,id});
            String result = connection.exec(preparedRpc);
            Document document = null;
            try {
                document = TestUtils.getDom(result);
            } catch (SAXException ex) {
                fail("XML could not be parsed:" + result);
            }
//            System.out.println(TestUtils.getPrettyPrintDocument(document));
            NodeList resultsNodes = document.getElementsByTagName("results");
            assertEquals("There should be only one results node.", 1, resultsNodes.getLength());
        } catch (VistaException ex) {
            logger.error(null, ex);
        }
    }

    @Test
    @Ignore
    public void printVprDomains() {
        for(VprDomain domain : VprDomain.values()) {
            System.out.println("================================================");
            System.out.println("================" + domain.name() + "================");
            System.out.println("================================================");
            RpcParameter dfn, id;
            try {
                dfn = new RpcParameter(RpcParameter.LITERAL, "1");
                id = new RpcParameter(RpcParameter.LITERAL, domain.getId());
                String preparedRpc = VistaRpc.prepare("VPR GET PATIENT DATA", new RpcParameter[]{dfn,id});
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

    
    @Test
    @Ignore
    public void testVprRpcLabReturnsResults() {
        RpcParameter dfn, id;
        try {
            dfn = new RpcParameter(RpcParameter.LITERAL, "3");
            id = new RpcParameter(RpcParameter.LITERAL, VprDomain.ORDER.getId());
            String preparedRpc = VistaRpc.prepare("VPR GET PATIENT DATA", new RpcParameter[]{dfn,id});
            String result = connection.exec(preparedRpc);
//            System.out.println(result);
            Document document = null;
            try {
                document = TestUtils.getDom(result);
            } catch (SAXException ex) {
                fail("XML could not be parsed:" + result);
            }
            System.out.println(TestUtils.getPrettyPrintDocument(document));
            NodeList resultsNodes = document.getElementsByTagName("results");
//            assertEquals("There should be only one results node.", 1, resultsNodes.getLength());
        } catch (VistaException ex) {
            logger.error(null, ex);
        }
    }
    
    
    @Test
    public void testVprDataVersionIsNumber() {
        String versionRegEx = "\\d\\.\\d+";
        try {
            String preparedRpc = VistaRpc.prepare("VPR DATA VERSION", null);
            String result = connection.exec(preparedRpc);
            Assert.assertTrue(result + " does not match " + versionRegEx, Pattern.matches(versionRegEx, result));
        } catch (VistaException ex) {
            logger.error(null, ex);
        }
    }
    
    private static void printStatistics() {        
        Collection<String> simonNames = SimonManager.getSimonNames();
        for (String string : simonNames) {
            if (string.length() > 0) {
                Stopwatch stopwatch = SimonManager.getStopwatch(string);
                if (stopwatch.getCounter() != 0L) {
//                    logger.info("JavaSimon Result: {}", stopwatch);       // uncomment for a simple output
                    System.out.println(stopwatch.getName());
                    System.out.println("\tcount: " + stopwatch.getCounter());
                    System.out.println("\tmax  : " + Double.valueOf(stopwatch.getMax())/NANOSECONDS_PER_SECOND);
                    System.out.println("\tmin  : " + Double.valueOf(stopwatch.getMin())/NANOSECONDS_PER_SECOND);
                    System.out.println("\tmu   : " + stopwatch.getMean()/NANOSECONDS_PER_SECOND);
                    System.out.println("\tsigma: " + stopwatch.getStandardDeviation()/NANOSECONDS_PER_SECOND);
                }
            }
        }
    }


    

}
