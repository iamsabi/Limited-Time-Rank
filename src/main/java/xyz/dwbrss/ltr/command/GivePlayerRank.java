package xyz.dwbrss.ltr.command;

import com.google.gson.Gson;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.io.FileUtils;
import xyz.dwbrss.ltr.json.PlayersJson;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import static xyz.dwbrss.ltr.command.NameAndUUID.NameToUUID;
import static xyz.dwbrss.ltr.util.FunctionUtils.runCommand;
import static xyz.dwbrss.ltr.util.FunctionUtils.sendMessage;
import static xyz.dwbrss.ltr.util.Utils.LOGGER;

@Mod.EventBusSubscriber
public class GivePlayerRank implements Command<CommandSourceStack> {
    public static GivePlayerRank instance = new GivePlayerRank();
    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        try {
            MinecraftServer SERVER = context.getSource().getServer();
            ServerPlayer PLAYER = context.getSource().getPlayerOrException();
            String NAME = StringArgumentType.getString(context, "player");
            int TIME = IntegerArgumentType.getInteger(context, "time(d)");
            String GROUP = StringArgumentType.getString(context, "group(rank)");
            String UUID = NameToUUID(NAME);
            SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Gson GSON = new Gson();
            File j2 = new File("ltr_players_data/" + UUID + ".json");
            long ADD_TIME = TIME * 86400000L;// 把天数转化成毫秒数
            Date TODAY = new Date();
            long TODAY_LONG = TODAY.getTime();
            Date DATE;
            long DATE_LONG;
            String DATE_STRING;
            if (j2.exists()) {
                String CONTENTj2 = FileUtils.readFileToString(j2, "UTF-8");
                PlayersJson PLAYERSJSONj2 = GSON.fromJson(CONTENTj2, PlayersJson.class);
                FileOutputStream jop1;
                OutputStreamWriter WRITER1;
                String NEW_PLAYERSJSON;
                if (Objects.equals(PLAYERSJSONj2.GROUP, GROUP)) {// 如果是，延长时间
                    DATE = DateFormat.parse(PLAYERSJSONj2.END_DATE);
                    DATE_LONG = DATE.getTime();
                    DATE.setTime(DATE_LONG + ADD_TIME);
                    DATE_STRING = DateFormat.format(DATE);
                    NEW_PLAYERSJSON = "{\"uuid\": \"" + UUID + "\", \"end_date\": \"" + DATE_STRING + "\", \"group\": \"" + PLAYERSJSONj2.GROUP + "\"}";
                    if (j2.delete()){
                        if(j2.createNewFile()){
                            LOGGER.info("the data of player " + NAME + " is reset");
                            sendMessage(new TranslatableComponent("ltr.message.the_end_date_of_players_rank_has_reset_to", new TextComponent(NAME).withStyle(ChatFormatting.RED), new TextComponent(DATE_STRING).withStyle(ChatFormatting.RED)),SERVER, PLAYER);
                            jop1 = new FileOutputStream(j2);
                            WRITER1 = new OutputStreamWriter(jop1, StandardCharsets.UTF_8);
                            WRITER1.write(NEW_PLAYERSJSON);
                            WRITER1.flush();
                            WRITER1.close();
                            jop1.close();
                        }
                    }
                } else {
                    DATE = TODAY;
                    DATE.setTime(TODAY_LONG + ADD_TIME);
                    DATE_STRING = DateFormat.format(DATE);
                    NEW_PLAYERSJSON = "{\"uuid\": \"" + UUID + "\", \"end_date\": \"" + DATE_STRING + "\", \"group\": \"" + GROUP + "\"}";
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
                    runCommand("/ftbranks add " + NAME + " " + GROUP, SERVER, PLAYER);
                    sendMessage(new TranslatableComponent("ltr.message.player_have_already_been_in", new TextComponent(NAME).withStyle(ChatFormatting.RED), new TextComponent(GROUP).withStyle(ChatFormatting.RED)),SERVER, PLAYER);
                    sendMessage(new TranslatableComponent("ltr.message.the_end_date_of_players_rank_has_reset_to", new TextComponent(NAME).withStyle(ChatFormatting.RED), new TextComponent(DATE_STRING).withStyle(ChatFormatting.RED)),SERVER, PLAYER);
                }
            } else {
                DATE = TODAY;
                DATE.setTime(TODAY_LONG + ADD_TIME);
                DATE_STRING = DateFormat.format(DATE);
                runCommand("/ftbranks add " + NAME + " " + GROUP, SERVER, PLAYER);
                if(j2.createNewFile()){
                    LOGGER.info("the data of player " + NAME + " is created");
                    FileOutputStream jop2 = new FileOutputStream(j2);
                    OutputStreamWriter WRITER2 = new OutputStreamWriter(jop2, StandardCharsets.UTF_8);
                    WRITER2.write("{\"uuid\": \"" + UUID + "\", \"end_date\": \"" + DATE_STRING + "\", \"group\": \"" + GROUP + "\"}");
                    WRITER2.flush();
                    WRITER2.close();
                    jop2.close();
                    sendMessage(new TranslatableComponent("ltr.message.player_have_already_been_in", new TextComponent(NAME).withStyle(ChatFormatting.RED), new TextComponent(GROUP).withStyle(ChatFormatting.RED)),SERVER, PLAYER);
                    sendMessage(new TranslatableComponent("ltr.message.players_rank_will_expire_on", new TextComponent(NAME).withStyle(ChatFormatting.RED), new TextComponent(DATE_STRING).withStyle(ChatFormatting.RED)),SERVER, PLAYER);
                }
            }
        } catch (IOException | ParseException | CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }
}
