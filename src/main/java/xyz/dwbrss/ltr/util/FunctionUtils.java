package xyz.dwbrss.ltr.util;

import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class FunctionUtils {
    public static void sendNormalMessage(String message_key, MinecraftServer server, ServerPlayer player){
        server.getPlayerList().broadcastMessage(new TranslatableComponent(message_key, player.getDisplayName()), ChatType.CHAT, player.getUUID());
    }
    public static void sendMessage(Component component, MinecraftServer server, ServerPlayer player){
        server.getPlayerList().broadcastMessage(component, ChatType.CHAT, player.getUUID());
    }
    public static void runCommand(String command, MinecraftServer server, ServerPlayer player){
        server.getCommands().performCommand(player.createCommandSourceStack().withPermission(4), command);
    }
}
