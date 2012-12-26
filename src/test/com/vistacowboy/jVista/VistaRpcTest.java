package test.com.vistacowboy.jVista;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.vistacowboy.jVista.RpcParameter;
import com.vistacowboy.jVista.VistaRpc;

/** 
* VistaRpc Tester. 
* 
* @author <Authors name> 
* @since <pre>11/26/2012</pre> 
* @version 1.0 
*/ 
public class VistaRpcTest extends TestCase
{
    public VistaRpcTest(String name)
    {
        super(name);
    }

    public void setUp() throws Exception
    {
        super.setUp();
    }

    public void tearDown() throws Exception
    {
        super.tearDown();
    }

    /**
    *
    * Method: prepareConnectRpc(Object[] params)
    *
    */
    public void testPrepareConnectRpc() throws Exception
    {
        String expected = "[XWB]10304\nTCPConnect50013192.168.1.107f00010f0022LAPTOP2.v11.med.va.govf\u0004";
        RpcParameter[] params = {
                new RpcParameter(RpcParameter.LITERAL, "192.168.1.107"),
                new RpcParameter(RpcParameter.LITERAL, "LAPTOP2.v11.med.va.gov")
        };
        String actual = VistaRpc.prepare("HELLO", params);
        assertEquals(expected, actual);
    }

    /**
    *
    * Method: prepareDisconnectRpc()
    *
    */
    public void testPrepareDisconnectRpc() throws Exception
    {
        String expected = "[XWB]10304\u0005#BYE#\u0004";
        String actual = VistaRpc.prepare("BYE", null);
        assertEquals(expected, actual);
    }

    /**
     *
     * Method: prepare(String rpcName, Object[] params)
     *
     */
    public void testPrepareIntroMsgRpc() throws Exception
    {
        String expected = "[XWB]11302\u00051.108\rXUS INTRO MSG54f\u0004";
        String actual = VistaRpc.prepare("XUS INTRO MSG", null);
        assertEquals(expected, actual);
    }

    public void testPrepareSetupLoginRpc() throws Exception
    {
        String expected = "[XWB]11302\u00051.108\u0010XUS SIGNON SETUP54f\u0004";
        String actual = VistaRpc.prepare("XUS SIGNON SETUP", null);
        assertEquals(expected, actual);
    }

    public void testPrepareLoginRpc() throws Exception
    {
        String expected = "[XWB]11302\u00051.108\u000BXUS AV CODE50017.r v11k3}!r&sAgP$f\u0004";
        RpcParameter param = new RpcParameter(RpcParameter.ENCRYPTED, "ijr773;Akiba12.", 14, 4);
        String actual = VistaRpc.prepare("XUS AV CODE", new RpcParameter[]{param});
        assertEquals(expected, actual);
    }

    public void testPrepareSetContextRpc() throws Exception
    {
        String expected = "[XWB]11302\u00051.108\u0012XWB CREATE CONTEXT50019(&y?#jy<?x:=?#68y].f\u0004";
        RpcParameter param = new RpcParameter(RpcParameter.ENCRYPTED, "OR CPRS GUI CHART", 8, 14);
        String actual = VistaRpc.prepare("XWB CREATE CONTEXT", new RpcParameter[]{param});
        assertEquals(expected, actual);
    }

    public void testPrepareGetVariableValueRpc() throws Exception
    {
        String arg = "$P($G(^DIC(3.1,1362,0)),U,1)";
        String expected = "[XWB]11302\u00051.108\u0016XWB GET VARIABLE VALUE51028$P($G(^DIC(3.1,1362,0)),U,1)f\u0004";
        RpcParameter param = new RpcParameter(RpcParameter.REFERENCE, arg);
        String actual = VistaRpc.prepare("XWB GET VARIABLE VALUE", new RpcParameter[]{param});
        assertEquals(expected, actual);
    }

    public static Test suite()
    {
        return new TestSuite(VistaRpcTest.class);
    }
} 
