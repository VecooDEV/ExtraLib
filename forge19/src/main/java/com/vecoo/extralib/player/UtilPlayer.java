package com.vecoo.extralib.player;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.UsernameCache;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class UtilPlayer {
    public static UUID getUUID(String playerName) {
        return UsernameCache.getMap().entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey)).get(playerName);
    }

    public static boolean hasUUID(String playerName) {
        return UsernameCache.getMap().entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey)).containsKey(playerName);
    }

    public static String getPlayerName(UUID playerUUID) {
        String playerName = UsernameCache.containsUUID(playerUUID) ? UsernameCache.getLastKnownUsername(playerUUID) : "Undefined";

        if (playerName == null) {
            playerName = "Undefined";
        }

        return playerName;
    }

    public static void sendMessageUuid(UUID playerUUID, Component message, MinecraftServer server) {
        ServerPlayer player = server.getPlayerList().getPlayer(playerUUID);

        if (player != null) {
            player.sendSystemMessage(message);
        }
    }

    public static void sendMessageUuid(UUID playerUUID, MutableComponent message, MinecraftServer server) {
        ServerPlayer player = server.getPlayerList().getPlayer(playerUUID);

        if (player != null) {
            player.sendSystemMessage(message);
        }
    }

    public static ServerPlayer getPlayer(String playerName, MinecraftServer server) {
        return server.getPlayerList().getPlayerByName(playerName);
    }

    public static CommandSourceStack getSource(String sourceName, MinecraftServer server) {
        ServerPlayer player = server.getPlayerList().getPlayerByName(sourceName);
        return (player != null) ? player.createCommandSourceStack() : server.createCommandSourceStack();
    }

    public static int countItemStack(ServerPlayer player, ItemStack itemStack) {
        int count = 0;

        for (int i = 0; i < player.getInventory().getContainerSize(); ++i) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem().equals(itemStack.getItem())) {
                if (itemStack.getTag() == null || (stack.getTag() != null && stack.getTag().equals(itemStack.getTag()))) {
                    count += stack.getCount();
                }
            }
        }
        return count;
    }

    public static boolean hasFreeSlot(ServerPlayer player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            if (player.getInventory().getFreeSlot() == i) {
                return true;
            }
        }
        return false;
    }
}