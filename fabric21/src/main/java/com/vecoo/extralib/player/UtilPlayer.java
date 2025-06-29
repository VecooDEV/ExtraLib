package com.vecoo.extralib.player;

import com.mojang.authlib.GameProfile;
import com.vecoo.extralib.ExtraLib;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;
import java.util.UUID;

public class UtilPlayer {
    public static UUID getUUID(String playerName) {
        GameProfileCache profileCache = ExtraLib.getInstance().getServer().getProfileCache();

        if (profileCache == null) {
            return null;
        }

        GameProfile gameProfile = profileCache.get(playerName).orElse(null);

        return gameProfile != null ? gameProfile.getId() : null;
    }

    public static boolean hasUUID(String playerName) {
        GameProfileCache profileCache = ExtraLib.getInstance().getServer().getProfileCache();

        if (profileCache == null) {
            return false;
        }

        return profileCache.get(playerName).isPresent();
    }

    public static String getPlayerName(UUID playerUUID) {
        GameProfileCache profileCache = ExtraLib.getInstance().getServer().getProfileCache();

        if (profileCache == null) {
            return "Unknown";
        }

        GameProfile gameProfile = profileCache.get(playerUUID).orElse(null);

        return gameProfile != null ? gameProfile.getName() : "Unknown";
    }

    public static void sendMessageUuid(UUID playerUUID, Component message) {
        ServerPlayer player = ExtraLib.getInstance().getServer().getPlayerList().getPlayer(playerUUID);

        if (player != null) {
            player.sendSystemMessage(message);
        }
    }

    public static void sendMessageUuid(UUID playerUUID, MutableComponent message) {
        ServerPlayer player = ExtraLib.getInstance().getServer().getPlayerList().getPlayer(playerUUID);

        if (player != null) {
            player.sendSystemMessage(message);
        }
    }

    public static ServerPlayer getPlayer(String playerName) {
        return ExtraLib.getInstance().getServer().getPlayerList().getPlayerByName(playerName);
    }

    public static CommandSourceStack getSource(String sourceName) {
        MinecraftServer server = ExtraLib.getInstance().getServer();

        ServerPlayer player = server.getPlayerList().getPlayerByName(sourceName);
        return player != null ? player.createCommandSourceStack() : server.createCommandSourceStack();
    }

    public static int countItemStack(ServerPlayer player, ItemStack searchItemStack) {
        int count = 0;

        for (ItemStack itemStack : player.inventoryMenu.getItems()) {
            if (itemStack.isEmpty() || !ItemStack.isSameItemSameComponents(itemStack, searchItemStack)) {
                continue;
            }

            count += itemStack.getCount();
        }

        return count;
    }

    public static int countItemStackTag(ServerPlayer player, ItemStack searchItemStack, DataComponentType<?> dataComponent) {
        int count = 0;

        for (ItemStack itemStack : player.inventoryMenu.getItems()) {
            if (itemStack.isEmpty() || itemStack.getItem() != searchItemStack.getItem()) {
                continue;
            }

            if (itemStack.getComponents().isEmpty() && searchItemStack.getComponents().isEmpty()) {
                count += itemStack.getCount();
                continue;
            }

            if (itemStack.getComponents().isEmpty() || searchItemStack.getComponents().isEmpty()) {
                continue;
            }

            if (!Objects.equals(itemStack.getComponents().get(dataComponent), searchItemStack.getComponents().get(dataComponent))) {
                continue;
            }

            count += itemStack.getCount();
        }

        return count;
    }

    public static void removeItemStack(ServerPlayer player, ItemStack removeItemStack, int amount) {
        int totalRemoved = 0;

        InventoryMenu playerContainer = player.inventoryMenu;

        for (ItemStack itemStack : playerContainer.getItems()) {
            if (totalRemoved >= amount) {
                break;
            }

            if (itemStack.isEmpty() || !ItemStack.isSameItemSameComponents(itemStack, removeItemStack)) {
                continue;
            }

            int toRemove = Math.min(itemStack.getCount(), amount - totalRemoved);

            itemStack.shrink(toRemove);
            totalRemoved += toRemove;
        }

        playerContainer.broadcastChanges();
    }

    public static void removeItemStackTag(ServerPlayer player, ItemStack removeItemStack, DataComponentType<?> dataComponent, int amount) {
        int totalRemoved = 0;

        InventoryMenu playerContainer = player.inventoryMenu;

        for (ItemStack itemStack : playerContainer.getItems()) {
            if (totalRemoved >= amount) {
                break;
            }

            if (itemStack.isEmpty() || itemStack.getItem() != removeItemStack.getItem()) {
                continue;
            }

            int toRemove = Math.min(itemStack.getCount(), amount - totalRemoved);

            if (itemStack.getComponents().isEmpty() && removeItemStack.getComponents().isEmpty()) {
                itemStack.shrink(toRemove);
                totalRemoved += toRemove;
                continue;
            }

            if (itemStack.getComponents().isEmpty() || removeItemStack.getComponents().isEmpty()) {
                continue;
            }

            if (!Objects.equals(itemStack.getComponents().get(dataComponent), removeItemStack.getComponents().get(dataComponent))) {
                continue;
            }

            itemStack.shrink(toRemove);
            totalRemoved += toRemove;
        }

        playerContainer.broadcastChanges();
    }

    public static boolean hasFreeSlot(ServerPlayer player) {
        return player.getInventory().getFreeSlot() != -1;
    }
}