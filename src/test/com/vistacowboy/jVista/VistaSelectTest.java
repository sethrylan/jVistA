package test.com.vistacowboy.jVista;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.vistacowboy.jVista.VistaSelect;
import com.vistacowboy.jVista.VistaConnection;
import com.vistacowboy.jVista.VistaException;
import com.vistacowboy.jVista.VistaUser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/** 
* VistaSelect Tester.
* 
* @author <Authors name> 
* @since <pre>Nov 28, 2012</pre> 
* @version 1.0 
*/ 
public class VistaSelectTest {

    private VistaSelect query;

    @Before
    public void before() throws Exception {
        query = new VistaSelect();
    }

    @After
    public void after() throws Exception {
    }

    /**
     *
     * Constructor:
     */
    @Test
    public void testConstructor() throws Exception {
        Assert.assertEquals("#", query.getIndex());
        Assert.assertEquals("@", query.getFields());
        Assert.assertEquals("IP", query.getFlags());
        Assert.assertEquals(-1, query.getNumber());
    }

    /**
    *
    * Method: setFileNumber(String value)
    *
    */
    @Test
    public void testSetFile() throws Exception {
        query.setFile("200");
        Assert.assertEquals("200", query.getFile());
    }

    /**
    *
    * Method: setIens(String value)
    *
    */
    @Test
    public void testSetIens() throws Exception {
        try
        {
            query.setIens("abc");
        }
        catch (VistaException e)
        {
            Assert.assertEquals("Non-numeric IEN: abc", e.getMessage());
        }

        try
        {
            query.setIens("67,,");
        }
        catch (VistaException e)
        {
            Assert.assertEquals("Non-numeric IEN: ", e.getMessage());
        }

        try
        {
            query.setIens(",,67,");
        }
        catch (VistaException e)
        {
            Assert.assertEquals("Non-numeric IEN: ", e.getMessage());
        }

        try
        {
            query.setIens("67,,4,");
        }
        catch (VistaException e)
        {
            Assert.assertEquals("Non-numeric IEN: ", e.getMessage());
        }

        query.setIens("67");
        Assert.assertEquals(",67,", query.getIens());

        query.setIens(",67");
        Assert.assertEquals(",67,", query.getIens());

        query.setIens(",67,");
        Assert.assertEquals(",67,", query.getIens());

        query.setIens("67,44");
        Assert.assertEquals(",67,44,", query.getIens());

        query.setIens(",67,44");
        Assert.assertEquals(",67,44,", query.getIens());

        query.setIens("67,44,");
        Assert.assertEquals(",67,44,", query.getIens());

        query.setIens(",67,44,");
        Assert.assertEquals(",67,44,", query.getIens());
    }

    /**
    *
    * Method: setFieldString(String value)
    *
    */
    @Test
    public void testSetFields() throws Exception {
        query.setFields("");
        Assert.assertEquals("@", query.getFields());

        query.setFields("@");
        Assert.assertEquals("@", query.getFields());

        query.setFields(".01;2;4;5;.141;8;9;11;29");
        Assert.assertEquals("@;.01;2;4;5;.141;8;9;11;29", query.getFields());

        query.setFields("@;.01;2;4;5;.141;8;9;11;29");
        Assert.assertEquals("@;.01;2;4;5;.141;8;9;11;29", query.getFields());

        query.setFields(".01;2;4;5;@;.141;8;9;11;29");
        Assert.assertEquals(".01;2;4;5;@;.141;8;9;11;29", query.getFields());
    }

    /**
    *
    * Method: setFlags(String value)
    *
    */
    @Test
    public void testSetFlags() throws Exception {
        query.setFlags("");
        Assert.assertEquals("IP", query.getFlags());

        try
        {
            query.setFlags("I");
        }
        catch (VistaException e)
        {
            Assert.assertEquals("Current version does packed queries only", e.getMessage());
        }

        query.setFlags("BIP");
        Assert.assertEquals("BIP", query.getFlags());
    }

    /**
    *
    * Method: setIndex(String value)
    *
    */
    @Test
    public void testSetIndex() throws Exception {
    //TODO: Test goes here...
    }

    /**
    *
    * Method: find(VistaConnection cxn)
    *
    */
//    @Test
//    public void testFindWithStrings() throws Exception {
//        query.setFile("200", "Person");
//        query.setFieldString(".01;2;4;5;@;.141;8;9;11;29");
//        query.setNumber(1);
//        query.setFrom("546");
//
//        VistaConnection cxn = new VistaConnection("74.67.137.153", 19200);
//        cxn.connect();
//        VistaUser user = new VistaUser();
//        user.login(cxn,"1programmer", "programmer1.", "DVBA CAPRI GUI");
//        String[][] actual = query.find(cxn);
//        cxn.disconnect();
//
//        String[][] expected = {
//                {"546","ZZPROGRAMMER,NINE","@Jy$9BO'9iCm#:x*p:'E","F","","","2","666948848","tHffxTgZ)<4~.7`EUx}j","1043"},
//        };
//        Assert.assertArrayEquals(expected, actual);
//    }

    /**
     *
     * Method: find(VistaConnection cxn)
     *
     */
//    @Test
//    public void testFindWithHashMap() throws Exception {
//        query.setFile("200", "Person");
//        LinkedHashMap<String, String> flds = new LinkedHashMap<String, String>();
//        flds.put(".01", "Name");
//        flds.put("2", "Access Code");
//        flds.put("4", "Gender");
//        flds.put("5", "DOB");
//        flds.put(".141", "Room #");
//        flds.put("8", "Title");
//        flds.put("9", "SSN");
//        flds.put("11", "Verify Code");
//        flds.put("29", "Service");
//        query.setFields(flds);
//        query.setNumber(1);
//        query.setFrom("546");
//
//        VistaConnection cxn = new VistaConnection("74.67.137.153", 19200);
//        cxn.connect();
//        VistaUser user = new VistaUser();
//        user.login(cxn,"1programmer", "programmer1.", "DVBA CAPRI GUI");
//        String[][] actual = query.find(cxn);
//        cxn.disconnect();
//
//        String[][] expected = {
//                {"546","ZZPROGRAMMER,NINE","@Jy$9BO'9iCm#:x*p:'E","F","","","2","666948848","tHffxTgZ)<4~.7`EUx}j","1043"},
//        };
//        Assert.assertArrayEquals(expected, actual);
//    }

    /**
     *
     * Method: find(VistaConnection cxn)
     *
     */
    @Test
    public void testFindFms() throws Exception {
        query.setFile("410");
        query.setFields(".01;1;24;23;22");
        query.setNumber(200);
        query.setFrom("178");
        query.setPart("178");
        query.setIndex("AN");

        VistaConnection cxn = new VistaConnection("74.67.137.153", 19200);
        cxn.connect();
        VistaUser user = new VistaUser();
        user.login(cxn,"1programmer", "programmer1.", "DVBA CAPRI GUI");
        String[][] actual = query.find(cxn);
        cxn.disconnect();

        Assert.assertEquals(56, actual.length);
    }

    /**
    *
    * Method: prepare()
    *
    */
    @Test
    public void testPrepare() throws Exception {
        query.setFile("200");
        query.setFields(".01;2;4;5;.141;8;9;11;29");
        query.setNumber(1);
        query.setFrom("546");

        try
        {
            Method method = query.getClass().getDeclaredMethod("prepare");
            method.setAccessible(true);
            String actual = (String)method.invoke(query);
            String expected = "[XWB]11302\u00051.108\nDDR LISTER52006\"FILE\"003200t008\"FIELDS\"026@;.01;2;4;5;.141;8;9;11;29t007\"FLAGS\"002IPt005\"MAX\"0011t006\"FROM\"003545t006\"XREF\"001#f\u0004";
            Assert.assertEquals(expected, actual);
        }
        catch(NoSuchMethodException e)
        {
        }
        catch(IllegalAccessException e)
        {
        }
        catch(InvocationTargetException e)
        {
        }
    }

    /**
    *
    * Method: prepareParamList()
    *
    */
    @Test
    public void testPrepareParamList() throws Exception {
    //TODO: Test goes here...
    /*
    try {
       Method method = VistaSelect.getClass().getMethod("prepareParamList");
       method.setAccessible(true);
       method.invoke(<Object>, <Parameters>);
    } catch(NoSuchMethodException e) {
    } catch(IllegalAccessException e) {
    } catch(InvocationTargetException e) {
    }
    */
    }

    /**
    *
    * Method: load(String response)
    *
    */
    @Test
    public void testLoad() throws Exception {
        String response = "[Misc]\r\nMORE^546^546^\r\n[MAP]\r\nIEN^.01I^2I^4I^5I^.141I^8I^9I^11I^29I\r\n[BEGIN_diDATA]\r\n546^ZZPROGRAMMER,NINE^@Jy$9BO\'9iCm#:x*p:\'E^F^^^2^666948848^tHffxTgZ)<4~.7`EUx}j^1043\r\n[END_diDATA]";

        String[][] expected = {
            {"546","ZZPROGRAMMER,NINE","@Jy$9BO'9iCm#:x*p:'E","F","","","2","666948848","tHffxTgZ)<4~.7`EUx}j","1043"},
        };

        try
        {
            Method method = query.getClass().getDeclaredMethod("load", String.class);
            method.setAccessible(true);
            method.invoke(query, response);
            Assert.assertArrayEquals(expected, query.getRecords());
        }
        catch(NoSuchMethodException e)
        {
        }
        catch(IllegalAccessException e)
        {
        }
        catch(InvocationTargetException e)
        {
        }
    }

} 
