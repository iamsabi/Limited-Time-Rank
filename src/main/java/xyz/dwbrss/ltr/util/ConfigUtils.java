package xyz.dwbrss.ltr.util;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import xyz.dwbrss.ltr.json.ConfigJson;

import java.io.File;
import java.io.IOException;

public class ConfigUtils {
    public static ConfigJson ConfigUtils() {
        File CONFIG = new File("ltr_config/limited_time_rank_common.json");
        Gson GSON = new Gson();
        String CONTENT;
            try {
                CONTENT = FileUtils.readFileToString(CONFIG, "UTF-8");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        return GSON.fromJson(CONTENT, ConfigJson.class);
    }
}
