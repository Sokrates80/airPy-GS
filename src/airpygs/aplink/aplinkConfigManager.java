package airpygs.aplink;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by fabrizioscimia on 23/02/16.
 */
public class AplinkConfigManager {

    private static AplinkConfigManager instance = null;
    private Properties properties;

    protected AplinkConfigManager() throws IOException {

        properties = new Properties();
        properties.load(getClass().getResourceAsStream("aplink.properties"));
    }

    public static AplinkConfigManager getInstance() {
        if(instance == null) {
            try {
                instance = new AplinkConfigManager();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return instance;
    }

    public Properties getConfig() {
        return properties;
    }
}
