package xyz.dwbrss.ltr.util;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkConstants;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

@Mod("ltr")
public class Utils{
    public static final Logger LOGGER = LogUtils.getLogger();
    public Utils() throws IOException {
        File f1 = new File("ltr_keys");
        if (!f1.exists() && !f1.isDirectory()) {
            if(f1.mkdirs()){
                LOGGER.info("ltr_keys mkdirs is created successfully");
            }
        }else{
            LOGGER.info("ltr_keys mkdirs has already been created");
        }
        File f2 = new File("ltr_keys_out");
        if (!f2.exists() && !f2.isDirectory()) {
            if(f2.mkdirs()){
                LOGGER.info("ltr_keys_out mkdirs is created successfully");
            }
        }else{
            LOGGER.info("ltr_keys_out mkdirs has already been created");
        }
        File f3 = new File("ltr_players_data");
        if (!f3.exists() && !f3.isDirectory()) {
            if(f3.mkdirs()){
                LOGGER.info("ltr_players_data mkdirs is created successfully");
            }
        }else{
            LOGGER.info("ltr_players_data mkdirs has already been created");
        }
        File f4 = new File("ltr_config");
        if (!f4.exists() && !f4.isDirectory()) {
            if(f4.mkdirs()){
                LOGGER.info("ltr_config mkdirs is created successfully");
            }
        }else{
            LOGGER.info("ltr_config mkdirs has already been created");
        }
        File CONFIG1 = new File("ltr_config/limited_time_rank_common.json");
        if (!CONFIG1.exists()) {
            if (CONFIG1.createNewFile()) {
                LOGGER.info("config is created successfully");
            } else {
                LOGGER.info("fail to create config");
            }
        }else{
            LOGGER.info("config has already been created");
        }
        FileOutputStream cop = new FileOutputStream(CONFIG1);
        OutputStreamWriter WRITER = new OutputStreamWriter(cop, StandardCharsets.UTF_8);
        WRITER.write("{\"default_group\": \"Default\", \"version\": \"0.1\", \"key_length\": 24,\"key_prefix\": \"\", \"key_suffix\": \"\"}");
        WRITER.flush();
        WRITER.close();
        cop.close();
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));
    }
}
