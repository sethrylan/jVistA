package gov.va.iehr.jvista;

import com.vistacowboy.jVista.*;
import gov.va.common.VistAResource;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InvokeVafctfRpcITest {

    private Logger logger = LoggerFactory.getLogger(InvokeRpcITest.class);
    VistaConnection connection = null;
    private static final long NANOSECONDS_PER_SECOND = 1000000000L;

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
        String context = "VAFCTF RPC CALLS";
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
    public void testWithDfn() {
        RpcParameter dfn;
        try {
            dfn = new RpcParameter(RpcParameter.LITERAL, "237^PI^USVHA^500");
            String preparedRpc = VistaRpc.prepare("VAFC LOCAL GETCORRESPONDINGIDS", new RpcParameter[]{dfn});
            String result = connection.exec(preparedRpc);
            System.out.println(result);

        } catch (VistaException ex) {
            logger.error(null, ex);
        }
    }

    @Test
    public void testWithIcn() {
        RpcParameter icn;
        try {
            icn = new RpcParameter(RpcParameter.LITERAL, "1008861107V475740^NI^USVHA^200M^A");
            String preparedRpc = VistaRpc.prepare("VAFC LOCAL GETCORRESPONDINGIDS", new RpcParameter[]{icn});
            String result = connection.exec(preparedRpc);
            System.out.println(result);

        } catch (VistaException ex) {
            logger.error(null, ex);
        }
    }

    @Test
    public void testAllergy() {
        RpcParameter string;
        try {
            string = new RpcParameter(RpcParameter.LITERAL, "B");
            String preparedRpc = VistaRpc.prepare("ORWDAL32 ALLERGY MATCH", new RpcParameter[]{string});
            String result = connection.exec(preparedRpc);
            System.out.println(result);
        } catch (VistaException ex) {
            logger.error(null, ex);
        }
    }


    @Test
    public void testAllergy2() {
        RpcParameter string, string2;
        try {
            string = new RpcParameter(RpcParameter.LITERAL, "");
            string2 = new RpcParameter(RpcParameter.LITERAL, "1");
            String preparedRpc = VistaRpc.prepare("ORWDAL32 SYMPTOMS", new RpcParameter[]{string,string2});
            String result = connection.exec(preparedRpc);
            System.out.println(result);
        } catch (VistaException ex) {
            logger.error(null, ex);
        }
    }

    @Test
    public void testExample() {
        RpcParameter string, string2;
        try {
            string = new RpcParameter(RpcParameter.LITERAL, "XWB IS RPC AVAILABLE");
            string2 = new RpcParameter(RpcParameter.LITERAL, "L");
            String preparedRpc = VistaRpc.prepare("XWB IS RPC AVAILABLE", new RpcParameter[]{string,string2});
            String result = connection.exec(preparedRpc);
            System.out.println(result);
        } catch (VistaException ex) {
            logger.error(null, ex);
        }
    }








}
