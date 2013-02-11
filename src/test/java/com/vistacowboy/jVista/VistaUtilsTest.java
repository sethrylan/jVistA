package com.vistacowboy.jVista;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.vistacowboy.jVista.VistaUtils;

/** 
* VistaUtils Tester. 
* 
* @author <Authors name> 
* @since <pre>11/25/2012</pre> 
* @version 1.0 
*/ 
public class VistaUtilsTest extends TestCase
{

    public VistaUtilsTest(String name)
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
    * Method: strPack(String s, int width)
    *
    */
    public void testStrPack() throws Exception
    {
        String expected = "0000000015something!5here";
        String actual = VistaUtils.strPack("something!5here", 10);
        assertEquals(expected, actual);
    }

    /**
    *
    * Method: prependCount(String s)
    *
    */
    public void testPrependCount() throws Exception
    {
        String expected = "\nSomeString";
        String actual = VistaUtils.prependCount("SomeString");
        assertEquals(expected, actual);
    }

    /**
    *
    * Method: adjustForSearch(String s)
    *
    */
    public void testAdjustForSearch() throws Exception
    {
        String expected = "244";
        String actual = VistaUtils.adjustForSearch("245");
        assertEquals(expected, actual);

        expected = "Snurc~";
        actual = VistaUtils.adjustForSearch("Snurd");
        assertEquals(expected, actual);
    }

    /**
    *
    * Method: isNumeric(String s)
    *
    */
    public void testIsNumeric() throws Exception
    {
    //TODO: Test goes here...
    }

    public static Test suite()
    {
        return new TestSuite(VistaUtilsTest.class);
    }
} 
