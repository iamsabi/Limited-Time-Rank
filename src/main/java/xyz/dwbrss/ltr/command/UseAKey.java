package xyz.dwbrss.ltr.command;

import com.google.gson.Gson;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.io.FileUtils;
import xyz.dwbrss.ltr.json.KeysJson;
import xyz.dwbrss.ltr.json.PlayersJson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import static xyz.dwbrss.ltr.command.NameAndUUID.UUIDToName;
import static xyz.dwbrss.ltr.util.Utils.*;

@Mod.EventBusSubscriber
public class UseAKey implements Command<CommandSourceStack> {
    public static UseAKey instance = new UseAKey();
    @Override
    public int run(CommandContext<CommandSourceStack> context){
        assert Minecraft.getInstance().player != null;
        try {
            String UUID = context.getSource().getPlayerOrException().getStringUUID();
            String NAME = UUIDToName(UUID);
            LOGGER.info("the player " + NAME + " is using a key");
            SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd");
            File j1 = new File("ltr_keys/" + StringArgumentType.getString(context, "key") + ".json");
            File j2 = new File("ltr_players_data/" + UUID + ".json");
            // 检查卡密是否已经存在
            if (j1.exists()) {// 如果存在，检查玩家是否已进入某个权限组
                Gson GSON = new Gson();
                String CONTENTj1 = FileUtils.readFileToString(j1, "UTF-8");
                KeysJson KEYSJSONj1 = GSON.fromJson(CONTENTj1, KeysJson.class);
                long ADD_TIME = KEYSJSONj1.DAYS * 86400000L;// 把天数转化成毫秒数
                Date TODAY = new Date();
                long TODAY_LONG = TODAY.getTime();
                Date DATE;
                long DATE_LONG;
                String DATE_STRING;
                if (j2.exists()) {// 如果是，检查玩家进入的权限组是不是卡密里预设的
                    String CONTENTj2 = FileUtils.readFileToString(j2, "UTF-8");
                    PlayersJson PLAYERSJSONj2 = GSON.fromJson(CONTENTj2, PlayersJson.class);
                    FileOutputStream jop1;
                    OutputStreamWriter WRITER1;
                    String NEW_PLAYERSJSON;
                    if (Objects.equals(PLAYERSJSONj2.GROUP, KEYSJSONj1.GROUP)) {// 如果是，延长时间
                        DATE = DateFormat.parse(PLAYERSJSONj2.END_DATE);
                        DATE_LONG = DATE.getTime();
                        DATE.setTime(DATE_LONG + ADD_TIME);
                        DATE_STRING = DateFormat.format(DATE);
                        NEW_PLAYERSJSON = "{\"uuid\": \"" + UUID + "\", \"end_date\": \"" + DATE_STRING + "\", \"group\": \"" + PLAYERSJSONj2.GROUP + "\"}";
                        if (j2.delete()){
                            if(j2.createNewFile()){
                                LOGGER.info("the data of player " + NAME + " is reset");
                                jop1 = new FileOutputStream(j2);
                                WRITER1 = new OutputStreamWriter(jop1, StandardCharsets.UTF_8);
                                WRITER1.write(NEW_PLAYERSJSON);
                                WRITER1.flush();
                                WRITER1.close();
                                jop1.close();
                            }
                        }
                    } else {// 如果否，覆盖当前权限组，并将时间重置为卡密指定的时间
                        DATE = TODAY;
                        DATE.setTime(TODAY_LONG + ADD_TIME);
                        DATE_STRING = DateFormat.format(DATE);
                        NEW_PLAYERSJSON = "{\"uuid\": \"" + UUID + "\", \"end_date\": \"" + DATE_STRING + "\", \"group\": \"" + KEYSJSONj1.GROUP + "\"}";
                        if (j2.delete()){
                            if(j2.createNewFile()){
                                LOGGER.info("the data of player " + NAME + " is reset");
                                jop1 = new FileOutputStream(j2);
                                WRITER1 = new OutputStreamWriter(jop1, StandardCharsets.UTF_8);
                                WRITER1.write(NEW_PLAYERSJSON);
                                WRITER1.flush();
                                WRITER1.close();
                                jop1.close();
                            }
                        }
                        Minecraft.getInstance().player.chat("/ftbranks add " + NAME + " " + KEYSJSONj1.GROUP);
                        Minecraft.getInstance().player.sendMessage(
                                new TranslatableComponent("ltr.message.you_have_already_been_in", new TextComponent(KEYSJSONj1.GROUP).withStyle(ChatFormatting.RED)),
                                Util.NIL_UUID
                        );
                        Minecraft.getInstance().player.sendMessage(
                                new TranslatableComponent("ltr.message.the_end_date_of_your_rank_has_reset_to", new TextComponent(DATE_STRING).withStyle(ChatFormatting.RED)),
                                Util.NIL_UUID
                        );
                    }
                } else {// 如果否，将玩家加入指定权限组
                    DATE = TODAY;
                    DATE.setTime(TODAY_LONG + ADD_TIME);
                    DATE_STRING = DateFormat.format(DATE);
                    LOGGER.info("running command \"/ftbranks add " + NAME + " " + KEYSJSONj1.GROUP + "\"");
                    Minecraft.getInstance().player.chat("/ftbranks add " + NAME + " " + KEYSJSONj1.GROUP);
                    if(j2.createNewFile()){
                        LOGGER.info("the data of player " + NAME + " is created");
                        FileOutputStream jop2 = new FileOutputStream(j2);
                        OutputStreamWriter WRITER2 = new OutputStreamWriter(jop2, StandardCharsets.UTF_8);
                        WRITER2.write("{\"uuid\": \"" + UUID + "\", \"end_date\": \"" + DATE_STRING + "\", \"group\": \"" + KEYSJSONj1.GROUP + "\"}");
                        WRITER2.flush();
                        WRITER2.close();
                        jop2.close();
                        Minecraft.getInstance().player.sendMessage(
                            new TranslatableComponent("ltr.message.you_have_already_been_in", new TextComponent(KEYSJSONj1.GROUP).withStyle(ChatFormatting.RED)),
                            Util.NIL_UUID
                        );
                        Minecraft.getInstance().player.sendMessage(
                            new TranslatableComponent("ltr.message.your_rank_will_expire_on", new TextComponent(DATE_STRING).withStyle(ChatFormatting.RED)),
                            Util.NIL_UUID
                        );
                    }
                }
            } else {// 如果不存在
                LOGGER.error("The key does not exist");
                Minecraft.getInstance().player.sendMessage(
                        new TranslatableComponent("ltr.message.the_key_does_not_exist"),
                        Util.NIL_UUID
                );
            }
        } catch (ParseException | IOException | CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }
}
