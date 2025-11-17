package com.vecoo.extralib.player;

import com.vecoo.extralib.ExtraLib;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.UsernameCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class UtilPlayer {
    @Nullable
    public static UUID getUUID(@NotNull String playerName) {
        return UsernameCache.getMap().entrySet().stream()
                .filter(entry -> entry.getValue().equalsIgnoreCase(playerName))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    public static boolean hasUUID(@NotNull String playerName) {
        return getUUID(playerName) != null;
    }

    @NotNull
    public static String getPlayerName(@NotNull UUID playerUUID) {
        String name = UsernameCache.getLastKnownUsername(playerUUID);
        return name != null ? name : "Unknown";
    }

    public static void sendMessageUuid(@NotNull UUID playerUUID, @NotNull Component message) {
        ServerPlayer player = ExtraLib.getInstance().getServer().getPlayerList().getPlayer(playerUUID);

        if (player != null) {
            player.sendSystemMessage(message);
        }
    }

    public static void sendMessageUuid(@NotNull UUID playerUUID, @NotNull MutableComponent message) {
        ServerPlayer player = ExtraLib.getInstance().getServer().getPlayerList().getPlayer(playerUUID);

        if (player != null) {
            player.sendSystemMessage(message);
        }
    }

    @Nullable
    public static ServerPlayer getPlayer(@NotNull String playerName) {
        return ExtraLib.getInstance().getServer().getPlayerList().getPlayerByName(playerName);
    }

    @NotNull
    public static CommandSourceStack getSource(@NotNull String sourceName) {
        MinecraftServer server = ExtraLib.getInstance().getServer();

        ServerPlayer player = server.getPlayerList().getPlayerByName(sourceName);
        return player != null ? player.createCommandSourceStack() : server.createCommandSourceStack();
    }

    public void executeCommand(@NotNull ServerPlayer player, @NotNull String command) {
        ExtraLib.getInstance().getServer().getCommands().performPrefixedCommand(player.createCommandSourceStack(), command);
    }

    public static int countItemStack(@NotNull ServerPlayer player, @NotNull ItemStack searchItemStack) {
        int count = 0;

        for (ItemStack itemStack : player.inventoryMenu.getItems()) {
            if (itemStack.isEmpty() || !ItemStack.isSameItemSameComponents(itemStack, searchItemStack)) {
                continue;
            }

            count += itemStack.getCount();
        }

        return count;
    }

    public static int countItemStackTag(@NotNull ServerPlayer player, @NotNull ItemStack searchItemStack, @NotNull DataComponentType<?> dataComponent) {
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

    public static void removeItemStack(@NotNull ServerPlayer player, @NotNull ItemStack removeItemStack, int amount) {
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

    public static void removeItemStackTag(@NotNull ServerPlayer player, @NotNull ItemStack removeItemStack, @NotNull DataComponentType<?> dataComponent, int amount) {
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

    public static boolean hasFreeSlot(@NotNull ServerPlayer player) {
        return player.getInventory().getFreeSlot() != -1;
    }
}