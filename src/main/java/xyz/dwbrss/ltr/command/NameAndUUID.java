package xyz.dwbrss.ltr.command;

import org.jline.utils.InputStreamReader;
import xyz.dwbrss.ltr.util.JsonUtils;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class NameAndUUID {
    public static String NameToUUID(String name) {
        return JsonUtils.getValue(httpRequest("https://playerdb.co/api/player/minecraft/" + name, 5000, "GET"), new String[]{"data", "player", "id"});
    }
    public static String UUIDToName(String uuid) {
        return JsonUtils.getValue(httpRequest("https://playerdb.co/api/player/minecraft/" + uuid, 5000, "GET"), new String[]{"data", "player", "username"});
    }
    public static @Nullable String httpRequest(String httpUrl, int reqTime, String method) {
        HttpURLConnection connection = null;
        InputStream is = null;
        BufferedReader br = null;
        StringBuilder res = new StringBuilder();
        try {
            URL url = new URL(httpUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setReadTimeout(reqTime);
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.connect();
            if (Objects.equals(connection.getResponseCode(), 200)) {
                is = connection.getInputStream();
                if (Objects.nonNull(is)) {
                    br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                    String tmp;
                    while (Objects.nonNull((tmp = br.readLine()))) {
                        res.append(tmp);
                    }
                }
            }
        } catch (IOException e) {
            return null;
        } finally {
            if (Objects.nonNull(br)) {
                try {
                    br.close();
                } catch (IOException e) {
                    return null;
                }
            }
            if (Objects.nonNull(is)) {
                try {
                    is.close();
                } catch (IOException e) {
                    return null;
                }
            }
            Objects.requireNonNull(connection).disconnect();
        }
        return res.toString();
    }
}
