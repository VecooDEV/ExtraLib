package com.vecoo.extralib.player;

import com.mojang.authlib.GameProfile;
import com.vecoo.extralib.ExtraLib;
import com.vecoo.extralib.chat.UtilChat;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public final class UtilPlayer {
    @Nullable
    public static UUID findUUID(@NotNull String playerName) {
        GameProfileCache profileCache = ExtraLib.getInstance().getServer().getProfileCache();

        if (profileCache == null) {
            return null;
        }

        GameProfile gameProfile = profileCache.get(playerName).orElse(null);

        return gameProfile != null ? gameProfile.getId() : null;
    }

    public static boolean hasUUID(@NotNull String playerName) {
        return findUUID(playerName) != null;
    }

    @NotNull
    public static String getPlayerName(@NotNull UUID playerUUID) {
        GameProfileCache profileCache = ExtraLib.getInstance().getServer().getProfileCache();

        if (profileCache == null) {
            return "Unknown";
        }

        GameProfile gameProfile = profileCache.get(playerUUID).orElse(null);

        return gameProfile != null ? gameProfile.getName() : "Unknown";
    }

    public static void sendMessageUuid(@NotNull UUID playerUUID, @NotNull String message) {
        ServerPlayer player = ExtraLib.getInstance().getServer().getPlayerList().getPlayer(playerUUID);

        if (player != null) {
            player.sendSystemMessage(UtilChat.formatMessage(message));
        }
    }

    public static void sendMessageUuid(@NotNull UUID playerUUID, @NotNull Component message) {
        ServerPlayer player = ExtraLib.getInstance().getServer().getPlayerList().getPlayer(playerUUID);

        if (player != null) {
            player.sendSystemMessage(message);
        }
    }

    @Nullable
    public static ServerPlayer findPlayer(@NotNull String playerName) {
        return ExtraLib.getInstance().getServer().getPlayerList().getPlayerByName(playerName);
    }

    @NotNull
    public static CommandSourceStack getSource(@NotNull String sourceName) {
        MinecraftServer server = ExtraLib.getInstance().getServer();
        ServerPlayer player = server.getPlayerList().getPlayerByName(sourceName);

        return player != null ? player.createCommandSourceStack() : server.createCommandSourceStack();
    }

    public static int countItemStack(@NotNull Player player, @NotNull ItemStack searchItemStack) {
        int count = 0;

        for (ItemStack itemStack : player.inventoryMenu.getItems()) {
            if (itemStack.isEmpty() || !ItemStack.isSameItemSameTags(itemStack, searchItemStack)) {
                continue;
            }

            count += itemStack.getCount();
        }

        return count;
    }

    public static int countItemStackTag(@NotNull Player player, @NotNull ItemStack searchItemStack, @NotNull String tag) {
        int count = 0;

        for (ItemStack itemStack : player.inventoryMenu.getItems()) {
            if (itemStack.isEmpty() || itemStack.getItem() != searchItemStack.getItem()) {
                continue;
            }

            if (itemStack.getTag() == null && searchItemStack.getTag() == null) {
                count += itemStack.getCount();
                continue;
            }

            if (itemStack.getTag() == null || searchItemStack.getTag() == null) {
                continue;
            }

            if (!Objects.equals(itemStack.getTag().get(tag), searchItemStack.getTag().get(tag))) {
                continue;
            }

            count += itemStack.getCount();
        }

        return count;
    }

    public static void removeItemStack(@NotNull Player player, @NotNull ItemStack removeItemStack, int amount) {
        int totalRemoved = 0;

        InventoryMenu playerContainer = player.inventoryMenu;

        for (ItemStack itemStack : playerContainer.getItems()) {
            if (totalRemoved >= amount) {
                break;
            }

            if (itemStack.isEmpty() || itemStack.getItem() != removeItemStack.getItem() || !ItemStack.isSameItemSameTags(itemStack, removeItemStack)) {
                continue;
            }

            int toRemove = Math.min(itemStack.getCount(), amount - totalRemoved);

            itemStack.shrink(toRemove);
            totalRemoved += toRemove;
        }

        playerContainer.broadcastChanges();
    }

    public static void removeItemStackTag(@NotNull Player player, @NotNull ItemStack removeItemStack, @NotNull String tag, int amount) {
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

            if (itemStack.getTag() == null && removeItemStack.getTag() == null) {
                itemStack.shrink(toRemove);
                totalRemoved += toRemove;
                continue;
            }

            if (itemStack.getTag() == null || removeItemStack.getTag() == null) {
                continue;
            }

            if (!Objects.equals(itemStack.getTag().get(tag), removeItemStack.getTag().get(tag))) {
                continue;
            }

            itemStack.shrink(toRemove);
            totalRemoved += toRemove;
        }

        playerContainer.broadcastChanges();
    }

    public static boolean hasFreeSlot(@NotNull Player player) {
        return player.getInventory().getFreeSlot() != -1;
    }
}