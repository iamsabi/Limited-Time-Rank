package xyz.dwbrss.ltr.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.fml.common.Mod;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

import static xyz.dwbrss.ltr.command.RandomStringGenerator.UsingMath;
import static xyz.dwbrss.ltr.util.ConfigUtils.ConfigUtils;
import static xyz.dwbrss.ltr.util.Utils.*;

@Mod.EventBusSubscriber
public class CreateKeys implements Command<CommandSourceStack> {
    // private static final PermissionNode<Boolean> node = new PermissionNode<>(MOD_ID, "command.ltr.create_keys", PermissionTypes.BOOLEAN, (player, playerUUID, context) ->{return player.hasPermissions(4);});
    // 新建权限节点
    // @SubscribeEvent
    // public static void Permission(PermissionGatherEvent.Nodes event) {
    //     event.addNodes(node);
    // }
    public static CreateKeys instance = new CreateKeys();
    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        assert Minecraft.getInstance().player != null;
        if (IntegerArgumentType.getInteger(context, "time(d)") <= 0 || IntegerArgumentType.getInteger(context, "time(d)") >= 2147483647/86400) {
            Minecraft.getInstance().player.sendMessage(
                    new TranslatableComponent("ltr.message.time_must_be_over_0_and_less_than_24855"),
                    Util.NIL_UUID
            );
        } else if (IntegerArgumentType.getInteger(context, "number") <= 0) {
            Minecraft.getInstance().player.sendMessage(
                    new TranslatableComponent("ltr.message.number_must_be_over_0"),
                    Util.NIL_UUID
            );
        } else {
            try {
                Date DATE = new Date();
                SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
                String KEY;
                int TIME = IntegerArgumentType.getInteger(context, "time(d)");
                String GROUP = StringArgumentType.getString(context, "group");
                LOGGER.info("Keys are creating...");
                File j;
                FileOutputStream jop;
                OutputStreamWriter WRITER1;
                String KEY_LIST = "";
                int x = 1;
                while (x <= IntegerArgumentType.getInteger(context, "number")) {
                    KEY = ConfigUtils().PREFIX + UsingMath(ConfigUtils().LENGTH) + ConfigUtils().SUFFIX;
                    KEY_LIST = KEY_LIST + "\r\n" + KEY;
                    j = new File("ltr_keys/" + KEY + ".json");
                    if (j.exists()) {
                        System.out.println("The key has already existed, and it will create a new key");
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
                    LOGGER.info("Done! " + IntegerArgumentType.getInteger(context, "number") + " keys have been created");
                    Minecraft.getInstance().player.sendMessage(
                        new TranslatableComponent("ltr.message.keys_have_saved_at", new TextComponent("/ltr_keys_out/" + DateFormat.format(DATE) + ".txt").withStyle(ChatFormatting.WHITE)),
                        Util.NIL_UUID
                    );
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return 0;
    }
}
