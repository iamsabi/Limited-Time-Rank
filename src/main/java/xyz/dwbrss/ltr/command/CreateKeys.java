package xyz.dwbrss.ltr.command;

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
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import net.minecraftforge.server.permission.nodes.PermissionTypes;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

import static xyz.dwbrss.ltr.command.RandomStringGenerator.UsingMath;
import static xyz.dwbrss.ltr.util.ConfigUtils.ConfigUtils;
import static xyz.dwbrss.ltr.util.FunctionUtils.sendMessage;
import static xyz.dwbrss.ltr.util.FunctionUtils.sendNormalMessage;
import static xyz.dwbrss.ltr.util.Utils.*;

@Mod.EventBusSubscriber
public class CreateKeys implements Command<CommandSourceStack> {
    private static final PermissionNode<Boolean> node = new PermissionNode<>("ltr", "command.ltr.create_keys", PermissionTypes.BOOLEAN, (player, playerUUID, context) ->{
        assert player != null;
        return player.hasPermissions(4);});
    //新建权限节点
    @SubscribeEvent
    public static void Permission(PermissionGatherEvent.Nodes event) {
        event.addNodes(node);
    }
    public static CreateKeys instance = new CreateKeys();
    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        try {
            MinecraftServer SERVER = context.getSource().getServer();
            ServerPlayer PLAYER = context.getSource().getPlayerOrException();
            int TIME = IntegerArgumentType.getInteger(context, "time(d)");
            int NUMBER = IntegerArgumentType.getInteger(context, "number");
            String GROUP = StringArgumentType.getString(context, "group(rank)");
            if (TIME <= 0 || TIME >= 2147483647/86400) {
                sendNormalMessage("ltr.message.time_must_be_over_0_and_less_than_24855", SERVER, PLAYER);
            } else if (NUMBER <= 0) {
                sendNormalMessage("ltr.message.number_must_be_over_0", SERVER, PLAYER);
            } else {
                Date DATE = new Date();
                SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
                String KEY;
                LOGGER.info("Keys are creating...");
                File j;
                FileOutputStream jop;
                OutputStreamWriter WRITER1;
                String KEY_LIST = "";
                int x = 1;
                while (x <= NUMBER) {
                    KEY = ConfigUtils().PREFIX + UsingMath(ConfigUtils().LENGTH) + ConfigUtils().SUFFIX;
                    KEY_LIST = KEY_LIST + "\r\n" + KEY;
                    j = new File("ltr_keys/" + KEY + ".json");
                    if (j.exists()) {
                        LOGGER.info("The key has already existed, and it will create a new key");
                    } else {
                        if(j.createNewFile()){
                            jop = new FileOutputStream(j);
                            WRITER1 = new OutputStreamWriter(jop, StandardCharsets.UTF_8);
                            WRITER1.write("{\"key\": \"" + KEY + "\", \"days\": " + TIME + ", \"group\": \"" + GROUP + "\"}");
                            WRITER1.flush();
                            WRITER1.close();
                            jop.close();
                            ++x;
                        }
                    }
                }
                File f2 = new File("ltr_keys_out/" + DateFormat.format(DATE) + ".txt");
                if(f2.createNewFile()){
                    FileOutputStream fop;
                    OutputStreamWriter WRITER2;
                    fop = new FileOutputStream(f2);
                    WRITER2 = new OutputStreamWriter(fop, StandardCharsets.UTF_8);
                    WRITER2.write(KEY_LIST);
                    WRITER2.flush();
                    WRITER2.close();
                    fop.close();
                    LOGGER.info("Done! " + NUMBER + " keys have been created");
                    sendMessage(new TranslatableComponent("ltr.message.keys_have_saved_at", new TextComponent("/ltr_keys_out/" + DateFormat.format(DATE) + ".txt").withStyle(ChatFormatting.WHITE)), SERVER, PLAYER);
                }
            }
        } catch (IOException | CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }
}
