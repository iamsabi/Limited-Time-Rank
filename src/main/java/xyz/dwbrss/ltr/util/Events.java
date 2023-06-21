package xyz.dwbrss.ltr.util;

import com.google.gson.Gson;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.io.FileUtils;
import xyz.dwbrss.ltr.command.CreateKeys;
import xyz.dwbrss.ltr.command.GivePlayerRank;
import xyz.dwbrss.ltr.command.UseAKey;
import xyz.dwbrss.ltr.json.PlayersJson;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static xyz.dwbrss.ltr.command.NameAndUUID.UUIDToName;
import static xyz.dwbrss.ltr.util.ConfigUtils.ConfigUtils;
import static xyz.dwbrss.ltr.util.FunctionUtils.runCommand;
import static xyz.dwbrss.ltr.util.FunctionUtils.sendMessage;
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
                                        .then(Commands.argument("group(rank)", StringArgumentType.string())
                                                .requires((commandSource) -> commandSource.hasPermission(0))
                                                .executes(CreateKeys.instance)
                                        )))));
    }
    // 新建GivePlayerRank命令
    @SubscribeEvent
    public static void onServerStaring3(RegisterCommandsEvent event){
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        LiteralCommandNode<CommandSourceStack> cmd = dispatcher.register(Commands.literal("ltr")
                .then(Commands.literal("give")
                        .then(Commands.argument("player", StringArgumentType.string())
                                .then(Commands.argument("time(d)", IntegerArgumentType.integer())
                                        .then(Commands.argument("group(rank)", StringArgumentType.string())
                                                .requires((commandSource) -> commandSource.hasPermission(4))
                                                .executes(GivePlayerRank.instance)
                                        )))));
    }
    // 检查玩家登入时所在权限组
    @SubscribeEvent
    public static void WhenAPlayerLogIn(PlayerEvent.PlayerLoggedInEvent event) {
        MinecraftServer SERVER = event.getPlayer().getServer();
        assert SERVER != null;
        ServerPlayer PLAYER = (ServerPlayer) event.getPlayer();
        SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String UUID = event.getPlayer().getUUID().toString();
        String NAME = UUIDToName(UUID);
        File j1 = new File("ltr_players_data/" + UUID + ".json");
        if (j1.exists()) {
            java.util.Date TODAY = new java.util.Date();
            Gson GSON = new Gson();
            try {
                String CONTENTj1 = FileUtils.readFileToString(j1, "UTF-8");
                PlayersJson PLAYERSJSONj1 = GSON.fromJson(CONTENTj1, PlayersJson.class);
                java.util.Date DATE = DateFormat.parse(PLAYERSJSONj1.END_DATE);
                runCommand("/ftbranks add " + NAME + " " + PLAYERSJSONj1.GROUP, SERVER, PLAYER);
                if (DATE.getTime() < TODAY.getTime()) {
                    if (j1.delete()) {
                        runCommand("/ftbranks add " + NAME + " " + ConfigUtils().DEFAULT_GROUP, SERVER, PLAYER);
                        sendMessage(new TranslatableComponent("ltr.message.your_rank_has_already_expired", new TextComponent("/ltr_keys_out/" + DateFormat.format(DATE) + ".txt").withStyle(ChatFormatting.RED)), SERVER, PLAYER);
                    }
                } else {
                    sendMessage(new TranslatableComponent("ltr.message.your_rank_will_expire_on", new TextComponent(PLAYERSJSONj1.END_DATE).withStyle(ChatFormatting.RED)), SERVER, PLAYER);
                }
            } catch (IOException | ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
