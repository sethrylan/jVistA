package gov.va.iehr.jvista;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.vistacowboy.jVista.RpcParameter;
import com.vistacowboy.jVista.VistaConnection;
import com.vistacowboy.jVista.VistaException;
import com.vistacowboy.jVista.VistaRpc;
import com.vistacowboy.jVista.VistaSelect;
import com.vistacowboy.jVista.VistaUser;
import gov.va.common.TestUtils;
import gov.va.common.VistAResource;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Collection;
import java.util.LinkedHashMap;
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
    private static final long NANOSECONDS_PER_SECOND = 1000000000L;
    
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
//        String context = "VPR SYNCHRONIZATION CONTEXT";
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
    @Ignore
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
    public void testVprRpcVitalsPerformance() {
        for (int i = 0; i < 20; i++) {
        RpcParameter dfn, id;
        try {
            Split split = SimonManager.getStopwatch("describe all vitals of patient 3".replaceAll(" ", "")).start();
            dfn = new RpcParameter(RpcParameter.LITERAL, "3");
            id = new RpcParameter(RpcParameter.LITERAL, VprDomain.VITAL.getId());
            String preparedRpc = VistaRpc.prepare("VPR GET PATIENT DATA", new RpcParameter[]{dfn,id});
            String result = connection.exec(preparedRpc);
            split.stop();
        } catch (VistaException ex) {
            logger.error(null, ex);
        }
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
            Document document = null;
            try {
                document = TestUtils.getDom(result);
            } catch (SAXException ex) {
                fail("XML could not be parsed:" + result);
            }
            System.out.println(TestUtils.getPrettyPrintDocument(document));
        } catch (VistaException ex) {
            logger.error(null, ex);
        }
    }
    
    @Test
    @Ignore
    public void testVprRpcPatient() {
        RpcParameter dfn, id;
        try {
            dfn = new RpcParameter(RpcParameter.LITERAL, "3");
            id = new RpcParameter(RpcParameter.LITERAL, "patient");
            String preparedRpc = VistaRpc.prepare("VPR GET PATIENT DATA", new RpcParameter[]{dfn,id});
            String result = connection.exec(preparedRpc);
            System.out.println("vprpatient = " + result);
        } catch (VistaException ex) {
            logger.error(null, ex);
        }
    }

    
    @Test
    @Ignore
    public void testVprJsonRpc() {
        RpcParameter param;
        try {            
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("\"patientId\"", "73");
            params.put("\"domain\"", VprDomain.VITAL.getId());

            param = new RpcParameter(RpcParameter.LIST,  params);
            String preparedRpc = VistaRpc.prepare("VPR GET PATIENT DATA JSON", new RpcParameter[]{param});
            String result = connection.exec(preparedRpc);
            System.out.println("results = " + result);
            
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonParser jp = new JsonParser();
            JsonElement je = jp.parse(result);
            String json = gson.toJson(je);

            
            FileWriter fstream = new FileWriter("C:/vitals_0073_updated.json");
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(json);
            out.close();

        } catch (VistaException ex) {
            logger.error(null, ex);
        } catch (Exception ex) {
            logger.error(null, ex);
        }
    }


    
    @Test
    public void testVprDataVersionIsNumber() {
        String versionRegEx = "\\d\\.\\d+";
        try {
            String preparedRpc = VistaRpc.prepare("VPR DATA VERSION", null);
            String result = connection.exec(preparedRpc);
//            System.out.println("result = " + result);
            Assert.assertTrue(result + " does not match " + versionRegEx, Pattern.matches(versionRegEx, result));
        } catch (VistaException ex) {
            logger.error(null, ex);
        }
    }
    
    
    
    @Test
    @Ignore
    public void exportData() {
        VistaSelect select = new VistaSelect();
        select.setFile("2");
        String[][] result = null;
        try {
            result = select.find(connection);
        } catch (VistaException ex) {
            logger.error(null, ex);
        }

        for(VprDomain domain : VprDomain.values()) {
            for(String[] arrDfn : result) {
                String dfn = arrDfn[0];
                RpcParameter param;
                String json = null;
                try {            
                    LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
                    params.put("\"patientId\"", dfn);
                    params.put("\"domain\"", domain.getId());
                    param = new RpcParameter(RpcParameter.LIST,  params);
                    String preparedRpc = VistaRpc.prepare("VPR GET PATIENT DATA JSON", new RpcParameter[]{param});
                    json = connection.exec(preparedRpc);

                    // pretty-ify json
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    JsonParser jp = new JsonParser();
                    JsonElement je = jp.parse(json);
                    json = gson.toJson(je);

//                    FileWriter fstream = new FileWriter("C:/vpr_json/" + domain.name().toLowerCase() + "_" + String.format("%04d", Integer.parseInt(dfn)) + ".json");
//                    BufferedWriter out = new BufferedWriter(fstream);
//                    out.write(json);
//                    out.close();
                } catch (VistaException ex) {
                    logger.error("Error on dfn: " + dfn + "; domain: " + domain.name(), ex);
                    logger.error("json = " + json);
                } catch (Exception ex) {
                    logger.error("Error on dfn: " + dfn + "; domain: " + domain.name(), ex);
                    logger.error("json = " + json);
                }
            }

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
