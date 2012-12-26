package test.com.vistacowboy.jVista;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.vistacowboy.jVista.VistaConnection;
import com.vistacowboy.jVista.VistaUser;

/** 
* VistaUser Tester. 
* 
* @author <Authors name> 
* @since <pre>11/27/2012</pre> 
* @version 1.0 
*/ 
public class VistaUserTest extends TestCase
{
    private VistaConnection cxn;

    public VistaUserTest(String name)
    {
        super(name);
    }

    public void setUp() throws Exception
    {
        super.setUp();
        cxn = new VistaConnection("74.67.137.153", 19200);
        cxn.connect();
    }

    public void tearDown() throws Exception
    {
        super.tearDown();
        cxn.disconnect();
    }

    /**
    *
    * Method: login(VistaConnection cxn, String access_code, String verify_code)
    *
    */
    public void testLoginForCxnAccess_codeVerify_code() throws Exception
    {
    //TODO: Test goes here...
    }

    /**
    *
    * Method: login(VistaConnection cxn, String access_code, String verify_code, String context)
    *
    */
    public void testLoginForCxnAccess_codeVerify_codeContext() throws Exception
    {
        VistaUser user = new VistaUser();
        String access_code = "1programmer";
        String verify_code = "programmer1.";
        String context = "OR CPRS GUI CHART";
        String greeting = user.login(cxn, access_code, verify_code, context);
        assertEquals(access_code, user.getAccess_code());
        assertEquals(verify_code, user.getVerify_code());
        assertEquals(context, user.getContext());
        assertEquals("1", user.getDuz());
        assertTrue(greeting.contains("ZZPROGRAMMER"));
    }

    /**
    *
    * Method: setContext(VistaConnection cxn, String context)
    *
    */
    public void testSetContext() throws Exception
    {
    //TODO: Test goes here...
    }

    /**
    *
    * Method: load(String response)
    *
    */
    public void testLoad() throws Exception
    {
    //TODO: Test goes here...
    /*
    try {
       Method method = VistaUser.getClass().getMethod("load", String.class);
       method.setAccessible(true);
       method.invoke(<Object>, <Parameters>);
    } catch(NoSuchMethodException e) {
    } catch(IllegalAccessException e) {
    } catch(InvocationTargetException e) {
    }
    */
    }


    public static Test suite()
    {
        return new TestSuite(VistaUserTest.class);
    }
} 
