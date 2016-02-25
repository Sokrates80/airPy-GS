package airpygs.aplink;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by fabrizioscimia on 23/02/16.
 */
public class aplinkConfigManager {

    private static aplinkConfigManager instance = null;
    private Properties properties;

    protected aplinkConfigManager() throws IOException {

        properties = new Properties();
        properties.load(getClass().getResourceAsStream("aplink.properties"));
    }

    public static aplinkConfigManager getInstance() {
        if(instance == null) {
            try {
                instance = new aplinkConfigManager();
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
