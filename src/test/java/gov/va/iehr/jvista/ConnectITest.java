package gov.va.iehr.jvista;

import com.vistacowboy.jVista.VistaConnection;
import com.vistacowboy.jVista.VistaException;
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
public class ConnectITest {

    private Logger logger = LoggerFactory.getLogger(ConnectITest.class);
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


}
