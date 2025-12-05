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
    /**
     * Finds the UUID of a player by their username.
     *
     * @param playerName the name of the player
     * @return the UUID of the player, or null if not found
     */
    @Nullable
    public static UUID findUUID(@NotNull String playerName) {
        return UsernameCache.getMap().entrySet().stream()
                .filter(entry -> entry.getValue().equalsIgnoreCase(playerName))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    /**
     * Checks if a player with the specified username has a cached UUID.
     *
     * @param playerName the name of the player
     * @return true if a UUID exists, false otherwise
     */
    public static boolean hasUUID(@NotNull String playerName) {
        return findUUID(playerName) != null;
    }

    /**
     * Finds the last known username for a given player UUID.
     *
     * @param playerUUID the UUID of the player
     * @return the last known username, or "Unknown" if not found
     */
    @NotNull
    public static String findPlayerName(@NotNull UUID playerUUID) {
        String name = UsernameCache.getLastKnownUsername(playerUUID);
        return name != null ? name : "Unknown";
    }

    /**
     * Sends a system message to a player identified by UUID.
     * <p>
     * Use this method when the player's online status is unknown.
     * If the player is offline, the message is silently ignored.
     * </p>
     *
     * @param playerUUID the UUID of the player
     * @param message    the message to send
     */
    public static void sendMessageUuid(@NotNull UUID playerUUID, @NotNull Component message) {
        ServerPlayer player = ExtraLib.instance().server().getPlayerList().getPlayer(playerUUID);

        if (player != null) {
            player.sendSystemMessage(message);
        }
    }

    /**
     * Sends a mutable system message to a player identified by UUID.
     * <p>
     * Use this method when the player's online status is unknown.
     * If the player is offline, the message is silently ignored.
     * </p>
     *
     * @param playerUUID the UUID of the player
     * @param message    the mutable message to send
     */
    public static void sendMessageUuid(@NotNull UUID playerUUID, @NotNull MutableComponent message) {
        ServerPlayer player = ExtraLib.instance().server().getPlayerList().getPlayer(playerUUID);

        if (player != null) {
            player.sendSystemMessage(message);
        }
    }

    /**
     * Finds a {@link ServerPlayer} object by player name.
     *
     * @param playerName the name of the player
     * @return the {@link ServerPlayer} instance, or null if not online
     */
    @Nullable
    public static ServerPlayer findPlayer(@NotNull String playerName) {
        return ExtraLib.instance().server().getPlayerList().getPlayerByName(playerName);
    }

    /**
     * Returns a {@link CommandSourceStack} for the finds player name.
     * <p>
     * If the player is not online, returns the server's command source stack.
     * </p>
     *
     * @param sourceName the name of the player or source
     * @return the command source stack
     */
    @NotNull
    public static CommandSourceStack findSource(@NotNull String sourceName) {
        MinecraftServer server = ExtraLib.instance().server();
        ServerPlayer player = server.getPlayerList().getPlayerByName(sourceName);

        return player != null ? player.createCommandSourceStack() : server.createCommandSourceStack();
    }

    /**
     * Executes a command as the specified player.
     *
     * @param player  the player executing the command
     * @param command the command string
     */
    public static void executeCommand(@NotNull ServerPlayer player, @NotNull String command) {
        ExtraLib.instance().server().getCommands().performPrefixedCommand(player.createCommandSourceStack(), command);
    }

    /**
     * Counts the number of {@link ItemStack} in the player's inventory that
     * matches the provided {@code searchItemStack}.
     *
     * @param player          the player
     * @param searchItemStack the item stack to search for
     * @return the total count of matching items
     */
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

    /**
     * Counts the number of {@link ItemStack} in the player's inventory that
     * matches the provided {@code searchItemStack} and a specific data component.
     *
     * @param player          the player
     * @param searchItemStack the item stack to search for
     * @param dataComponent   the data component to compare
     * @return the total count of matching items
     */
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

    /**
     * Removes a specific number of item stacks from the player's inventory.
     *
     * @param player          the player
     * @param removeItemStack the item stack to remove
     * @param amount          the number of items to remove
     */
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

    /**
     * Removes a specific number of item stacks from the player's inventory
     * that match a specific data component.
     *
     * @param player          the player
     * @param removeItemStack the item stack to remove
     * @param dataComponent   the data component to match
     * @param amount          the number of items to remove
     */
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

    /**
     * Checks if the player has at least one free slot in their inventory.
     *
     * @param player the player to check
     * @return true if the player has a free slot, false otherwise
     */
    public static boolean hasFreeSlot(@NotNull ServerPlayer player) {
        return player.getInventory().getFreeSlot() != -1;
    }
}