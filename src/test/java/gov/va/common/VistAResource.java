package gov.va.common;

import gov.va.common.TestResource;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * @author gaineys
 */
public class VistAResource {
    
    private static PropertiesConfiguration props = TestResource.getPropertiesConfiguration("worldvista.1.properties"); 
            
    private VistAResource() {
    }
    
    public static String getAddress() {
        return props.getString("address");
    }
    
    public static int getPort() {
        return props.getInt("port");
    }
    
    public static String getAccessCode() {
        return props.getString("accessCode");
    }
    
    public static String getVerifyCode() {
        return props.getString("verifyCode");
    }
}
