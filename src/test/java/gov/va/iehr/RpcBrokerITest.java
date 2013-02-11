package gov.va.iehr;

import com.vistacowboy.jVista.RpcParameter;
import com.vistacowboy.jVista.VistaConnection;
import com.vistacowboy.jVista.VistaException;
import com.vistacowboy.jVista.VistaRpc;
import com.vistacowboy.jVista.VistaSelect;
import com.vistacowboy.jVista.VistaUser;
import java.util.logging.Level;
import static junit.framework.Assert.*;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author gaineys
 */
public class RpcBrokerITest {

    private Logger logger = LoggerFactory.getLogger(RpcBrokerITest.class);
    VistaConnection connection = null;

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        //connection = new VistaConnection("74.67.137.153", 19200);
        connection = new VistaConnection("107.21.64.209", 9260);
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
    public void createRpcConnectionTest() {

        assertFalse(connection.getIsConnected());

        try {
            connection.connect();
        } catch (VistaException ex) {
            logger.error(null, ex);
        }

        assertTrue("VistaConnection not connected", connection.getIsConnected());

        try {
            connection.disconnect();
        } catch (VistaException ex) {
            logger.error(null, ex);
        }
        assertFalse(connection.getIsConnected());

    }

    @Test
    public void userLoginTest() {
        try {
            connection.connect();
        } catch (VistaException ex) {
            logger.error(null, ex);
        }
        VistaUser user = new VistaUser();
        String access_code = "sys.admin";
        String verify_code = "vista!123";
        String context = "OR CPRS GUI CHART";
        String greeting = null;
        try {
            greeting = user.login(connection, access_code, verify_code, context);
        } catch (VistaException ex) {
            logger.error(null, ex);
        }
        assertEquals(access_code, user.getAccess_code());
        assertEquals(verify_code, user.getVerify_code());
        assertEquals(context, user.getContext());
        assertEquals("1", user.getDuz());
        assertNotNull(greeting);
        assertTrue("greeting was " + greeting, greeting.contains("SA"));
    }

    @Test
    public void selectTest() {

        try {
            connection.connect();
        } catch (VistaException ex) {
            logger.error(null, ex);
        }
        
        VistaUser user = new VistaUser();
        String access_code = "sys.admin";
        String verify_code = "vista!123";
        String context = "OR CPRS GUI CHART";
        try {
            user.login(connection, access_code, verify_code, context);
        } catch (VistaException ex) {
            logger.error(null, ex);
        }

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

    @Test
    public void vprRpcTest() {

//        http://livevista.caregraf.info/rambler#!8994-3141
//        http://livevista.caregraf.info/rambler#!8994-3142
//        https://gitorious.org/osehra/mdo/blobs/1ed36e99c47b181447ea04d22a281c620e79a6a4/mdo/src/mdo/dao/vista/VistaClinicalDao.cs
//        http://livevista.caregraf.info/rambler#!8994-3140
//        http://wbvista.info/VDOCS/RoutinesPlus/RFRAME.php?ROUTINE=VPRD
        
        try {
            connection.connect();
        } catch (VistaException ex) {
            logger.error(null, ex);
        }
        VistaUser user = new VistaUser();
        String access_code = "sys.admin";
        String verify_code = "vista!123";
        String context = "OR CPRS GUI CHART";
        try {
            user.login(connection, access_code, verify_code, context);
        } catch (VistaException ex) {
            logger.error(null, ex);
        }
        
        VistaRpc rpc = new VistaRpc();
        RpcParameter rpcParam1 = null;
        RpcParameter rpcParam2 = null;

        try {
            String dfn = "2";
            
            rpcParam1 = new RpcParameter(RpcParameter.LITERAL, dfn);
            rpcParam2 = new RpcParameter(RpcParameter.LITERAL, "");  // all types
            String preparedRpc = rpc.prepare("VPR GET PATIENT DATA", new RpcParameter[]{rpcParam1});
            System.out.println(preparedRpc);
            connection.exec(preparedRpc);
        } catch (VistaException ex) {
            logger.error(null, ex);
        }


// TODO:



    }

    @Test
    public void socketTest() {
        //        try {
//            RpcParameter param = new RpcParameter(RpcParameter.ENCRYPTED, "OR CPRS GUI CHART", 8, 14);
//            String actual = VistaRpc.prepare("XWB CREATE CONTEXT", new RpcParameter[]{param});
//
//        } catch (VistaException ex) {
//            logger.error(null, ex);
//        }
    }

    private void printMatrix(Object[][] array) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                System.out.print(array[i][j]);
            }
            System.out.println();
        }
    }
}
