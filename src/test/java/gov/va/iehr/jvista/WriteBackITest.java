package gov.va.iehr.jvista;

import com.vistacowboy.jVista.RpcParameter;
import com.vistacowboy.jVista.VistaConnection;
import com.vistacowboy.jVista.VistaException;
import com.vistacowboy.jVista.VistaRpc;
import com.vistacowboy.jVista.VistaSelect;
import com.vistacowboy.jVista.VistaUser;
import gov.va.common.TestUtils;
import gov.va.common.VistAResource;
import org.javasimon.SimonManager;
import org.javasimon.Stopwatch;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import static junit.framework.Assert.assertTrue;

/**
 * @author gaineys
 */
public class WriteBackITest {

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
    
    
    @Test
    @Ignore
    public void testSelectOnPatientFile() {
        VistaSelect select = new VistaSelect();
        select.setFile("2");
        select.setFields(".01");  // uncomment for patient full names
        String[][] result = null;
        try {
            result = select.find(connection);
        } catch (VistaException ex) {
            logger.error(null, ex);
        }
        TestUtils.printMatrix(result);
        assertTrue("There should be over 100 patients.", result.length > 100);
    }
    
    
    @Test
    @Ignore
    public void testSelectOnGMRDFile() {
        VistaSelect select = new VistaSelect();
        select.setFile("120.82");
        select.setFields(".01;1;2;99.98;99.99");        
        String[][] result = null;
        try {
            result = select.find(connection);
        } catch (VistaException ex) {
            logger.error(null, ex);
        }
        TestUtils.printMatrix(result);
    }
    
    @Test
    @Ignore
    public void testSelectOnPSNDFFile() {
        VistaSelect select = new VistaSelect();
        select.setFile("50.6");
        select.setFields(".01;99.98;99.99");        
        String[][] result = null;
        try {
            result = select.find(connection);
        } catch (VistaException ex) {
            logger.error(null, ex);
        }
        TestUtils.printMatrix(result);
    }

        
    @Test
    @Ignore
    public void testSCCheckOnDuz1()  {
        RpcParameter param;
        try {
            param = new RpcParameter(RpcParameter.LITERAL, "SC PCMM SETUP");
            String preparedRpc = VistaRpc.prepare("SC KEY CHECK", new RpcParameter[]{param});
            String result = connection.exec(preparedRpc);
            //System.out.println("preparedRpc = " + preparedRpc);
            //System.out.println("result = " + result);
        } catch (VistaException ex) {
            logger.error(null, ex);
        }
    }
    
    
    @Test
    @Ignore
    public void testAllergiesDefaults() {
         // ORWDAL32 DEF
        try {
            String preparedRpc = VistaRpc.prepare("ORWDAL32 DEF", new RpcParameter[]{});
            System.out.println("preparedRpc = " + preparedRpc);
            String result = connection.exec(preparedRpc);
            System.out.println("result = " + result);
            System.out.println();
        } catch (VistaException ex) {
            logger.error(null, ex);
        }
    }

    @Test
    @Ignore
    public void testAllergiesList() {        
        
        /*
         * ORWDAL32 ALLERGY MATCH
         */
        try {
            RpcParameter allergyNameMatch = new RpcParameter(RpcParameter.LITERAL, "PENICILLIN");
            String preparedRpc = VistaRpc.prepare("ORWDAL32 ALLERGY MATCH", new RpcParameter[]{allergyNameMatch});
            System.out.println("preparedRpc = " + preparedRpc);
            String result = connection.exec(preparedRpc);
            System.out.println("result = " + result);
            System.out.println();
        } catch (VistaException ex) {
            logger.error(null, ex);
        }
    }

    
    @Test
    @Ignore
    public void testAllergiesWritebackRemoval() {        
        String patientAllergyIen = "73";
        String dfn = "7";
        
        listAllergies(dfn, connection);
        /*
         * ORWDAL32 LOAD FOR EDIT  - Maybe not necessary
         * ORWDAL32 SAVE ALLERGY
            literal 89
            literal 7
            list  
             (""GMRAERR"")=YES
             (""GMRAERRBY"")=1
             (""GMRAERRDT"")=3130521.175315
             (""GMRAERRCMTS"",0)=1
             (""GMRAERRCMTS"",1)=In error"
         */
        String allergyInError = "YES";
        String allergyInErrorBy = "1";
        String allergyInErrorDatetime = "3130521.175315";
        String[] errorComments = new String[]{"Allergy marked in error by VIPR"};
        RpcParameter dfnParam, patientAllergiesIenParam, listParam;
        try {
            patientAllergiesIenParam = new RpcParameter(RpcParameter.LITERAL, patientAllergyIen);
            dfnParam = new RpcParameter(RpcParameter.LITERAL, "");
            
            LinkedHashMap<String, String> paramMap = new LinkedHashMap<String, String>();
            paramMap.put("\"GMRAERR\"", allergyInError); 
            paramMap.put("\"GMRAERRBY\"", allergyInErrorBy);
            paramMap.put("\"GMRAERRDT\"", allergyInErrorDatetime);
            paramMap.putAll(toListParameters("GMRAERRCMTS", errorComments));
            
            listParam = new RpcParameter(RpcParameter.LIST,  paramMap);
            String preparedRpc = VistaRpc.prepare("ORWDAL32 SAVE ALLERGY", new RpcParameter[]{patientAllergiesIenParam, dfnParam, listParam});
            System.out.println("preparedRpc = " + preparedRpc);
            String result = connection.exec(preparedRpc);
            System.out.println("result = " + result);
            System.out.println();
        } catch (VistaException ex) {
            logger.error(null, ex);
        }
        listAllergies(dfn, connection);
    }

    @Test
    @Ignore
    public void testAllergiesWritebackCreate() {   
        String dfn = "7";
        listAllergies(dfn, connection);
        
        /*
         * OVID Workflow: https://github.com/medcafe/medCafe/blob/master/ovid-1.1-src/ovid-domain/src/main/java/com/medsphere/ovid/domain/ov/PatientAllergyRepository.java
         * 
         * ORQQAL LIST - List Allergies
         * ORWU DT - Current Timestamp
         * ORWU VALDT - Arbitraty Timestamp
         * ORWDAL32 SYMPTOMS - Signs and Symptoms # 120.83 (Locked)
         * ORWDAL32 SAVE ALLERGY
                literal 0   - The IEN of the entry in PATIENT ALLERGIES file; not used for creates, on8ly updates
                literal 7   - Patient DFN
                list 
                 ("GMRAGNT")=PENICILLIN^16;PSNDF(50.6,
                 ("GMRATYPE")=D^Drug
                 ("GMRANATR")=A^Allergy
                 ("GMRAORIG")=1
                 ("GMRAORDT")=3130521.1746
                 ("GMRASYMP",0)=1
                 ("GMRASYMP",1)=66^DROWSINESS^^^
                 ("GMRACHT",0)=1
                 ("GMRACHT",1)=3130521.174752
                 ("GMRAOBHX")=h^HISTORICAL
                 ("GMRARDT")=3130521
         */
        
        String reactantName = "PENICILLIN";
        String reactantIen = "16";
        String reactantFile = "GMRD(120.82,";            // 120.82
//        String reactantFile = "PSDRUG(";                 // 50
//        String reactantFile = "PSNDF(50.6,";             // 50.6
//        String reactantFile = "PS("+allergy.getAllergyReactantFileNum()+",";   // 50.416 or 50.605
        String gmrAgent = reactantName.trim() + "^" + reactantIen.trim()+ ";" + reactantFile;
        
        String allergyType = "D^Drug";
//        String allergyType = "DF^Drug,Food";
        
        String reactionNature = "A^Allergy";
//        String reactionNature = "P^Pharmacological";
//        String reactionNature = "U^Unknown";

        String originatorIen = "1";
        
        String originatingDatetime = "3130521.2006";
        
        String[] symptoms = new String[]{"2^ITCHING,WATERING EYES^^^", "99^HYPOTENSION^^^", "66^DROWSINESS^^^"};

        String observedOrHistorical = "h^HISTORICAL";
//        String observedOrHistorical = "o^Observed";
        
        String reactionDate = "3130521.2006";
        RpcParameter dfnParam, patientAllergiesIenParam, listParam;
        try {
            patientAllergiesIenParam = new RpcParameter(RpcParameter.LITERAL, "");           // supposed to be the Patient Allergies IEN
            dfnParam = new RpcParameter(RpcParameter.LITERAL, dfn);
            
            LinkedHashMap<String, String> paramMap = new LinkedHashMap<String, String>();
            paramMap.put("\"GMRAGNT\"", gmrAgent);                              // Causative Agent
            paramMap.put("\"GMRATYPE\"", allergyType);                          // Allergy Type: Some VistA symptom may expect only "Drug^", not "D^Drug"
            paramMap.put("\"GMRANATR\"", reactionNature);                       // Nature of reaction
            paramMap.put("\"GMRAORIG\"", originatorIen);                        // IEN of originating NEW PERSON
            paramMap.put("\"GMRAORDT\"", originatingDatetime);                  // originating datetime in FM format
            paramMap.putAll(toListParameters("GMRASYMP", symptoms));            // list of symptoms
//            params.put("\"GMRACHT\"", );                                      // not sure, but apparently a list of FM formatted dates
            paramMap.put("\"GMRAOBHX\"", observedOrHistorical);                 // whether the allergy is observed or historical
            paramMap.put("\"GMRARDT\"", reactionDate);                          // Reaction date in FM format
//            params.put("\"GMRASEVR\"", );                                     // Optional; integer in range 1-3 inclusive 
//            paramMap.putAll(arrayToListParameters("GMRACMTS", comments));     // list of comments
            
            listParam = new RpcParameter(RpcParameter.LIST,  paramMap);
            String preparedRpc = VistaRpc.prepare("ORWDAL32 SAVE ALLERGY", new RpcParameter[]{patientAllergiesIenParam, dfnParam, listParam});
            System.out.println("preparedRpc = " + preparedRpc);
            String result = connection.exec(preparedRpc);
            System.out.println("result = " + result);                           // Error Case: -1^Patient already has a PENICILLIN reaction entered.  No duplicates allowed.
            System.out.println();
        } catch (VistaException ex) {
            logger.error(null, ex);
        }
        listAllergies(dfn, connection);
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
    
    private Map<String, String> toListParameters(String rpcParameterName, String[] strings) {
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        params.put(String.format("\"%s\",0", rpcParameterName), String.valueOf(strings.length));
        for(int i = 0; i < strings.length; i++) {
            params.put(String.format("\"%s\",%d", rpcParameterName, i+1), strings[i]);
        }
        return params;
    }

    private void listAllergies(String dfn, VistaConnection connection) {
        RpcParameter dfnParam;
        try {
            dfnParam = new RpcParameter(RpcParameter.LITERAL, dfn);
            
            String preparedRpc = VistaRpc.prepare("ORQQAL LIST", new RpcParameter[]{dfnParam});
                        
            String result = connection.exec(preparedRpc);
            
            System.out.println("======================================");
            System.out.println("====== Allergies for Patient " + dfn + " ======");
            System.out.println("======================================");
            System.out.println(result);
            System.out.println("======================================");
            System.out.println();

        } catch (VistaException ex) {
            logger.error(null, ex);
        } 
    }
}
