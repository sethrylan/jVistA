package gov.va.common;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 *
 * @author gaineys
 */
public class TestResource {
    
    private TestResource() {
    }

    public static Properties getProperties(String propertiesLocation) {
        Properties props = new Properties();
        try {
            props.load(TestResource.class.getClassLoader().getResourceAsStream(propertiesLocation));
        } catch (IOException ex) {
            Logger.getLogger(TestResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return props;
    }
    
    public static PropertiesConfiguration getPropertiesConfiguration(String propertiesLocation) {
        PropertiesConfiguration props = new PropertiesConfiguration();
        try {
            props.load(TestResource.class.getClassLoader().getResourceAsStream(propertiesLocation));
        } catch (ConfigurationException ex) {
            Logger.getLogger(TestResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return props;
    }
    
    
    public static URL getResource(String resourceClassPath) {
        return TestResource.class.getClassLoader().getResource(resourceClassPath);
    }
}
