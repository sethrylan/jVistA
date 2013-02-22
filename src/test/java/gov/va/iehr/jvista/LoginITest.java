package gov.va.iehr.jvista;

import com.vistacowboy.jVista.VistaConnection;
import com.vistacowboy.jVista.VistaException;
import com.vistacowboy.jVista.VistaUser;
import gov.va.common.VistAResource;
import static junit.framework.Assert.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author gaineys
 */
public class LoginITest {

    private Logger logger = LoggerFactory.getLogger(LoginITest.class);
    VistaConnection connection = null;

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
    public void userLoginTest() {
        VistaUser user = new VistaUser();
        String access_code = VistAResource.getAccessCode();
        String verify_code = VistAResource.getVerifyCode();
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
    
}
