package com.vecoo.extralib.player;

import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
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

    public static void sendMessageUuid(UUID playerUUID, StringTextComponent message, MinecraftServer server) {
        ServerPlayerEntity player = server.getPlayerList().getPlayer(playerUUID);

        if (player != null) {
            player.sendMessage(message, Util.NIL_UUID);
        }
    }

    public static void sendMessageUuid(UUID playerUUID, IFormattableTextComponent message, MinecraftServer server) {
        ServerPlayerEntity player = server.getPlayerList().getPlayer(playerUUID);

        if (player != null) {
            player.sendMessage(message, Util.NIL_UUID);
        }
    }

    public static ServerPlayerEntity getPlayer(String playerName, MinecraftServer server) {
        return server.getPlayerList().getPlayerByName(playerName);
    }

    public static CommandSource getSource(String sourceName, MinecraftServer server) {
        ServerPlayerEntity player = server.getPlayerList().getPlayerByName(sourceName);
        return (player != null) ? player.createCommandSourceStack() : server.createCommandSourceStack();
    }

    public static int countItemStack(ServerPlayerEntity player, ItemStack itemStack) {
        int count = 0;

        for (int i = 0; i < player.inventory.getContainerSize(); ++i) {
            ItemStack stack = player.inventory.getItem(i);
            if (stack.getItem().equals(itemStack.getItem())) {
                if (itemStack.getTag() == null || (stack.getTag() != null && stack.getTag().equals(itemStack.getTag()))) {
                    count += stack.getCount();
                }
            }
        }

        return count;
    }

    public static boolean hasFreeSlot(ServerPlayerEntity player) {
        for (int i = 0; i < player.inventory.getContainerSize(); i++) {
            if (player.inventory.getFreeSlot() == i) {
                return true;
            }
        }

        return false;
    }
}