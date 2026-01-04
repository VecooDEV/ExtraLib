package com.vecoo.extralib.player;

import com.vecoo.extralib.ExtraLib;
import com.vecoo.extralib.chat.UtilChat;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.UsernameCache;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class UtilPlayer {
    @Nullable
    public static UUID findUUID(@Nullable String playerName) {
        return UsernameCache.getMap().entrySet().stream()
                .filter(entry -> entry.getValue().equalsIgnoreCase(playerName))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    public static boolean hasUUID(@Nonnull String playerName) {
        return findUUID(playerName) != null;
    }

    @Nonnull
    public static String getPlayerName(@Nonnull UUID playerUUID) {
        String name = UsernameCache.getLastKnownUsername(playerUUID);
        return name != null ? name : "Unknown";
    }

    public static void sendMessageUuid(@Nonnull UUID playerUUID, @Nonnull String message) {
        ServerPlayerEntity player = ExtraLib.getInstance().getServer().getPlayerList().getPlayer(playerUUID);

        if (player != null) {
            player.sendMessage(UtilChat.formatMessage(message), Util.NIL_UUID);
        }
    }

    public static void sendMessageUuid(@Nonnull UUID playerUUID, @Nonnull StringTextComponent message) {
        ServerPlayerEntity player = ExtraLib.getInstance().getServer().getPlayerList().getPlayer(playerUUID);

        if (player != null) {
            player.sendMessage(message, Util.NIL_UUID);
        }
    }

    @Nullable
    public static ServerPlayerEntity findPlayer(@Nonnull String playerName) {
        return ExtraLib.getInstance().getServer().getPlayerList().getPlayerByName(playerName);
    }

    @Nonnull
    public static CommandSource getSource(@Nonnull String sourceName) {
        MinecraftServer server = ExtraLib.getInstance().getServer();
        ServerPlayerEntity player = server.getPlayerList().getPlayerByName(sourceName);

        return player != null ? player.createCommandSourceStack() : server.createCommandSourceStack();
    }

    public static void executeCommand(@Nonnull PlayerEntity player, @Nonnull String command) {
        ExtraLib.getInstance().getServer().getCommands().performCommand(player.createCommandSourceStack(), command);
    }

    public static int countItemStack(@Nonnull PlayerEntity player, @Nonnull ItemStack searchItemStack) {
        int count = 0;

        for (ItemStack itemStack : player.inventoryMenu.getItems()) {
            if (itemStack.isEmpty() || itemStack.getItem() != searchItemStack.getItem() || !ItemStack.tagMatches(itemStack, searchItemStack)) {
                continue;
            }

            count += itemStack.getCount();
        }

        return count;
    }

    public static int countItemStackTag(@Nonnull PlayerEntity player, @Nonnull ItemStack searchItemStack, @Nonnull String tag) {
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

    public static void removeItemStack(@Nonnull PlayerEntity player, @Nonnull ItemStack removeItemStack, int amount) {
        int totalRemoved = 0;

        PlayerContainer playerContainer = player.inventoryMenu;

        for (ItemStack itemStack : playerContainer.getItems()) {
            if (totalRemoved >= amount) {
                break;
            }

            if (itemStack.isEmpty() || itemStack.getItem() != removeItemStack.getItem() || !ItemStack.tagMatches(itemStack, removeItemStack)) {
                continue;
            }

            int toRemove = Math.min(itemStack.getCount(), amount - totalRemoved);

            itemStack.shrink(toRemove);
            totalRemoved += toRemove;
        }

        playerContainer.broadcastChanges();
    }

    public static void removeItemStackTag(@Nonnull PlayerEntity player, @Nonnull ItemStack removeItemStack, @Nonnull String tag, int amount) {
        int totalRemoved = 0;

        PlayerContainer playerContainer = player.inventoryMenu;

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

    public static boolean hasFreeSlot(@Nonnull PlayerEntity player) {
        return player.inventory.getFreeSlot() != -1;
    }
}