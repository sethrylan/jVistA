package com.vistacowboy.jVista;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.vistacowboy.jVista.VistaConnection;

/** 
* VistaConnection Tester. 
* 
* @author <Authors name> 
* @since <pre>11/26/2012</pre> 
* @version 1.0 
*/ 
public class VistaConnectionTest extends TestCase
{
    VistaConnection cxn = null;

    public VistaConnectionTest(String name)
    {
        super(name);
    }

    public void setUp() throws Exception
    {
        super.setUp();
        cxn = new VistaConnection("74.67.137.153", 19200);

    }

    public void tearDown() throws Exception
    {
        super.tearDown();
        if (cxn.getIsConnected())
        {
            cxn.disconnect();
        }
    }

    /**
    *
    * Method: connect()
    *
    */
    public void testConnect() throws Exception
    {
        cxn.connect();
        assertTrue(cxn.getIsConnected());
        String response = cxn.disconnect();
        assertFalse(cxn.getIsConnected());
        assertEquals("#BYE#", response);
    }

    /**
    *
    * Method: exec(String rpc)
    *
    */
    public void testExec() throws Exception
    {
    //TODO: Test goes here...
    }

    public static Test suite()
    {
        return new TestSuite(VistaConnectionTest.class);
    }
} 
