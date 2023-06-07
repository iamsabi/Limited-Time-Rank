package xyz.dwbrss.ltr.util;

import com.google.gson.Gson;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.io.FileUtils;
import xyz.dwbrss.ltr.command.CreateKeys;
import xyz.dwbrss.ltr.command.UseAKey;
import xyz.dwbrss.ltr.json.PlayersJson;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static xyz.dwbrss.ltr.command.NameAndUUID.UUIDToName;
import static xyz.dwbrss.ltr.util.ConfigUtils.ConfigUtils;

@Mod.EventBusSubscriber()
public class Events {
    // 新建UseAKey命令
    @SubscribeEvent
    public static void onServerStaring1(RegisterCommandsEvent event){
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        LiteralCommandNode<CommandSourceStack> cmd = dispatcher.register(Commands.literal("ltr")
                .then(Commands.literal("key")
                        .then(Commands.argument("key", StringArgumentType.string())
                                                .requires((commandSource) -> commandSource.hasPermission(4))
                                                .executes(UseAKey.instance)
                                                )));
    }
    // 新建CreateKeys命令
    @SubscribeEvent
    public static void onServerStaring2(RegisterCommandsEvent event){
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        LiteralCommandNode<CommandSourceStack> cmd = dispatcher.register(Commands.literal("ltr")
                .then(Commands.literal("new")
                        .then(Commands.argument("number", IntegerArgumentType.integer())
                                .then(Commands.argument("time(d)", IntegerArgumentType.integer())
                                        .then(Commands.argument("group", StringArgumentType.string())
                                                .requires((commandSource) -> commandSource.hasPermission(0))
                                                .executes(CreateKeys.instance)
                                        )))));
    }
    // 检查玩家登入时所在权限组
    @SubscribeEvent
    public static void WhenAPlayerLogIn(PlayerEvent.PlayerLoggedInEvent event) {
        SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd");
        assert Minecraft.getInstance().player != null;
        String UUID = event.getPlayer().getUUID().toString();
        String NAME = UUIDToName(UUID);
        File j1 = new File("ltr_players_data/" + UUID + ".json");
        if (j1.exists()) {
            java.util.Date TODAY = new java.util.Date();
            Gson GSON = new Gson();
            try {
                String CONTENTj1 = FileUtils.readFileToString(j1, "UTF-8");
                PlayersJson PLAYERSJSONj1 = GSON.fromJson(CONTENTj1, PlayersJson.class);
                String GROUP = PLAYERSJSONj1.GROUP;
                java.util.Date DATE = DateFormat.parse(PLAYERSJSONj1.END_DATE);
                if (DATE.getTime() < TODAY.getTime()) {
                    if (j1.delete()) {
                        Minecraft.getInstance().player.chat("/ftbranks add " + NAME + " " + ConfigUtils().DEFAULT_GROUP);
                        Minecraft.getInstance().player.sendMessage(
                                new TranslatableComponent("ltr.message.your_rank_has_already_expired", new TextComponent(GROUP).withStyle(ChatFormatting.RED)),
                                Util.NIL_UUID
                        );
                    }
                } else {
                    Minecraft.getInstance().player.sendMessage(
                            new TranslatableComponent("ltr.message.your_rank_will_expire_on", new TextComponent(PLAYERSJSONj1.END_DATE).withStyle(ChatFormatting.RED)),
                            Util.NIL_UUID
                    );
                }
            } catch (IOException | ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
