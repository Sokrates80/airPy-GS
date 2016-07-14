package airpygs.utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

/**
 * Created by fabrizioscimia on 04/07/16.
 */
public class ApConfigManager {

    JSONParser jsonp;
    JSONObject config;

    private static ApConfigManager currentInstance = new ApConfigManager();

    public static ApConfigManager getInstance() {
        return currentInstance;
    }

    public int getTxChannelsNumber(){
        return ((Long) ((JSONObject) config.get("rcRadio")).get("num_channels")).intValue();
    }

    private ApConfigManager(){
        jsonp = new JSONParser();

        try {
            String path = ApConfigManager.class.getProtectionDomain().getCodeSource().getLocation().getPath().replace("airPyGS.jar","");
            config = (JSONObject) jsonp.parse(new FileReader(path + "/config.json"));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


}
