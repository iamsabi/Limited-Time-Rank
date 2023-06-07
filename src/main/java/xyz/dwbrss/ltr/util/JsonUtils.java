package xyz.dwbrss.ltr.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonUtils {
    public static JsonObject parseStrGson(String str) {
        return new JsonParser().parse(str).getAsJsonObject();
    }
    public static String getValue(String str, String [] keys) {
        JsonObject value = parseStrGson(str);
        for (int i = 0; i < keys.length - 1; i++) {
            value = value.get(keys[i]).getAsJsonObject();
        }
        return value.get(keys[keys.length - 1]).getAsString();
    }
}
