package airpygs.utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

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

            File file = new File(getClass().getClassLoader().getResource("airpygs/resources/config/config.json").getFile());
            config = (JSONObject) jsonp.parse(new FileReader(file));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


}
