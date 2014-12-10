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
import org.javasimon.SimonManager;
import org.javasimon.Split;
import org.javasimon.Stopwatch;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

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
//    @Ignore
    public void testVprRpcVitalsReturnsResults() {
        RpcParameter dfn, id;
        try {
            dfn = new RpcParameter(RpcParameter.LITERAL, "3");
            id = new RpcParameter(RpcParameter.LITERAL, VprDomain.ALLERGY.getId());
            String preparedRpc = VistaRpc.prepare("VPR GET PATIENT DATA", new RpcParameter[]{dfn,id});
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
        } catch (VistaException ex) {
            logger.error(null, ex);
        } 
    }
    
    
    @Test
    @Ignore
    public void testVprRpcVitalsPerformance() {
        for(VprDomain domain : VprDomain.values()) {
            for (int i = 0; i < 20; i++) {
                RpcParameter dfn, id;
                try {
                    Split split = SimonManager.getStopwatch(("patient 3," + domain).replaceAll(" ", "")).start();
                    dfn = new RpcParameter(RpcParameter.LITERAL, "3");
                    id = new RpcParameter(RpcParameter.LITERAL, domain.getId());
                    String preparedRpc = VistaRpc.prepare("VPR GET PATIENT DATA", new RpcParameter[]{dfn,id});
                    String result = connection.exec(preparedRpc);
                    split.stop();
                } catch (VistaException ex) {
                    logger.error(null, ex);
                }
            }
        }
    }
    
    @Test
    @Ignore
    public void testVprJsonRpcVitalsPerformance() {
        for(VprDomain domain : VprDomain.values()) {
            for (int i = 0; i < 20; i++) {
                RpcParameter param;
                try {
                    Split split = SimonManager.getStopwatch(("patient 3," + domain + ",JSON").replaceAll(" ", "")).start();
                    LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
                    params.put("\"patientId\"", "3");
                    params.put("\"domain\"", VprDomain.VITAL.getId());
                    param = new RpcParameter(RpcParameter.LIST,  params);
                    String preparedRpc = VistaRpc.prepare("VPR GET PATIENT DATA JSON", new RpcParameter[]{param});
                    String result = connection.exec(preparedRpc);
                    split.stop();
                } catch (VistaException ex) {
                    logger.error(null, ex);
                }
            }
        }
        
        for (int i = 0; i < 20; i++) {
            RpcParameter param;
            try {
                Split split = SimonManager.getStopwatch("describe all vitals of patient 3 (json)".replaceAll(" ", "")).start();
                LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
                params.put("\"patientId\"", "3");
                params.put("\"domain\"", VprDomain.VITAL.getId());
                param = new RpcParameter(RpcParameter.LIST,  params);
                String preparedRpc = VistaRpc.prepare("VPR GET PATIENT DATA JSON", new RpcParameter[]{param});
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
    public void testOperationalData() {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("\"domain\"", "problem");
            RpcParameter param = new RpcParameter(RpcParameter.LIST, params);
            String preparedRpc = VistaRpc.prepare("VPR GET OPERATIONAL DATA", new RpcParameter[]{param});
            System.out.println("rpc = " + preparedRpc);
            String result = connection.exec(preparedRpc);
            System.out.println("json = " + result);
        } catch (VistaException ex) {
            logger.error(null, ex);
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
            dfn = new RpcParameter(RpcParameter.LITERAL, "1");
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
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("\"patientId\"", "8");
            params.put("\"domain\"", "allergy");
            RpcParameter param = new RpcParameter(RpcParameter.LIST, params);
            String preparedRpc = VistaRpc.prepare("VPR GET PATIENT DATA JSON", new RpcParameter[]{param});
            System.out.println("rpc = " + preparedRpc);
            String result = connection.exec(preparedRpc);
            System.out.println("json = " + result);
        } catch (VistaException ex) {
            logger.error(null, ex);
        }
    }

    @Test
    public void testVprdjdsApis() {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();

            params.put("command", "getPtUpdates");
            params.put("lastUpdate", "2014010000");
            params.put("getStatus", "true");  // Temp flag for experimentation; check sync statii against VISTA
            params.put("max", "1000");
            params.put("hmpVersion", "S64");
            params.put("extractSchema", "3.001");
            RpcParameter param = new RpcParameter(RpcParameter.LIST, params);

            String preparedRpc = VistaRpc.prepare("VPRDJFS API", new RpcParameter[]{param});
            String result = connection.exec(preparedRpc);
            System.out.println("result = " + result);
        } catch (VistaException ex) {
            logger.error(null, ex);
        }
    }


    @Test
    public void testChecksum() {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();

            params.put("system", "9E7A");
            params.put("patientId", "3");
            params.put("domain", "vital");
            params.put("queued", "false");
            RpcParameter param = new RpcParameter(RpcParameter.LIST, params);

            String preparedRpc = VistaRpc.prepare("VPR GET CHECKSUM", new RpcParameter[]{param});
            String result = connection.exec(preparedRpc);
            System.out.println("result = " + result);
        } catch (VistaException ex) {
            logger.error(null, ex);
        }
    }


    @Test
    public void testUSERINFO() {
        try {
            String preparedRpc = VistaRpc.prepare("ORWU USERINFO", null);
            String result = connection.exec(preparedRpc);
            System.out.println("result = " + result);
        } catch (VistaException ex) {
            logger.error(null, ex);
        }
    }

    @Test
    public void testGetUserInfo() {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("\"command\"", "getUserInfo");
            params.put("\"userId\"", "11286");
            RpcParameter param = new RpcParameter(RpcParameter.LIST, params);

            String preparedRpc = VistaRpc.prepare("HMPCRPC RPC", new RpcParameter[]{param});
            String result = connection.exec(preparedRpc);
            System.out.println("result = " + result);
        } catch (VistaException ex) {
            logger.error(null, ex);
        }
    }
    
    @Test
    public void testGetDefaultPatientList() {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();

            params.put("\"command\"", "getDefaultPatientList");
//            params.put("server", "9E7A");

//            params.put("patientId", "3");
            RpcParameter param = new RpcParameter(RpcParameter.LIST, params);

            String preparedRpc = VistaRpc.prepare("VPRCRPC RPC", new RpcParameter[]{param});
            String result = connection.exec(preparedRpc);
            System.out.println("result = " + result);
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
            //System.out.println("result = " + result);
            Assert.assertTrue(result + " does not match " + versionRegEx, Pattern.matches(versionRegEx, result));
        } catch (VistaException ex) {
            logger.error(null, ex);
        }
    }



    @Test
    @Ignore
    public void exportVprXml() {
        VistaSelect select = new VistaSelect();
        select.setFile("2");
        String[][] result = null;
        try {
            result = select.find(connection);
        } catch (VistaException ex) {
            logger.error(null, ex);
        }

        DateFormat format = new SimpleDateFormat("yyyyMMdd'T'HHmm");
        String directory = System.getProperty("user.home") + "/vprxml_" + format.format(new Date());
        Boolean useUserDirectory = new File(directory).mkdirs();

        for(VprDomain domain : VprDomain.values()) {
            for(String[] arrDfn : result) {
                String dfnString = arrDfn[0];
                RpcParameter dfn, id;
                String xml = null;
                try {
                    dfn = new RpcParameter(RpcParameter.LITERAL, dfnString);
                    id = new RpcParameter(RpcParameter.LITERAL, domain.getId());
                    String preparedRpc = VistaRpc.prepare("VPR GET PATIENT DATA", new RpcParameter[]{dfn,id});
                    xml = connection.exec(preparedRpc);

                    try {
                        Document document = TestUtils.getDom(xml);
                        xml = TestUtils.getPrettyPrintDocument(document);
                    } catch (SAXException ex) {
                        System.out.println("XML could not be parsed:" + result);
                    }

                    String basename = domain.name().toLowerCase() + "_" + String.format("%04d", Integer.parseInt(dfnString));
                    String filename = directory + File.separator + basename + ".xml";
                    if (!useUserDirectory) {
                        filename = File.createTempFile(basename, "xml").getName();
                    }
                    FileWriter fstream = new FileWriter(new File(filename));
                    BufferedWriter out = new BufferedWriter(fstream);
                    out.write(xml);
                    out.close();
                } catch (Exception ex) {
                    logger.error("Error on dfn: " + dfnString + "; domain: " + domain.name(), ex);
                    logger.error("xml = " + xml);
                }
            }

        }
    }


    @Test
    public void exportVprJson() {
        VistaSelect select = new VistaSelect();
        select.setFile("2");
        String[][] result = null;
        try {
            result = select.find(connection);
        } catch (VistaException ex) {
            logger.error(null, ex);
        }

        DateFormat format = new SimpleDateFormat("yyyyMMdd'T'HHmm");
        String directory = System.getProperty("user.home") + "/vprjson_" + format.format(new Date());
        Boolean useUserDirectory = new File(directory).mkdirs();

        for(VprDomain domain : new VprDomain[] {VprDomain.ACCESSION, VprDomain.LAB}) {
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

                    String basename = domain.name().toLowerCase() + "_" + String.format("%04d", Integer.parseInt(dfn));
                    String filename = directory + File.separator + basename + ".json";
                    if (!useUserDirectory) {
                        filename = File.createTempFile(basename, "json").getName();
                    }
                    FileWriter fstream = new FileWriter(new File(filename));
                    BufferedWriter out = new BufferedWriter(fstream);
                    out.write(json);
                    out.close();
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
