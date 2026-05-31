package com.vecoo.extralib.util;

import com.vecoo.extralib.ExtraLib;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.UsernameCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public final class PlayerUtil {
    @NotNull
    public static String UNKNOWN_PLAYER = "Unknown";

    private PlayerUtil() {
    }

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
    public static String getPlayerName(@NotNull UUID playerUUID) {
        String name = UsernameCache.getLastKnownUsername(playerUUID);

        return name != null ? name : UNKNOWN_PLAYER;
    }

    /**
     * Sends a system formatted message to a player identified by UUID.
     * <p>
     * Use this method when the player's online status is unknown.
     * If the player is offline, the message is silently ignored.
     * </p>
     *
     * @param playerUUID the UUID of the player
     * @param message    the message to send
     */
    public static void sendMessageUUID(@NotNull UUID playerUUID, @NotNull String message) {
        EntityPlayerMP player = ExtraLib.getInstance().getServer().getPlayerList().getPlayerByUUID(playerUUID);

        if (player != null) {
            player.sendMessage(TextUtil.formatMessage(message));
        }
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
    public static void sendMessageUUID(@NotNull UUID playerUUID, @NotNull ITextComponent message) {
        EntityPlayerMP player = ExtraLib.getInstance().getServer().getPlayerList().getPlayerByUUID(playerUUID);

        if (player != null) {
            player.sendMessage(message);
        }
    }

    /**
     * Finds a {@link EntityPlayerMP} object by player UUID.
     *
     * @param playerUUID the UUID of the player
     * @return the {@link EntityPlayerMP} instance, or null if not online
     */
    @Nullable
    public static EntityPlayerMP findPlayer(@NotNull UUID playerUUID) {
        return ExtraLib.getInstance().getServer().getPlayerList().getPlayerByUUID(playerUUID);
    }

    /**
     * Finds a {@link EntityPlayerMP} object by player name.
     *
     * @param playerName the name of the player
     * @return the {@link EntityPlayerMP} instance, or null if not online
     */
    @Nullable
    public static EntityPlayerMP findPlayer(@NotNull String playerName) {
        return ExtraLib.getInstance().getServer().getPlayerList().getPlayerByUsername(playerName);
    }

    /**
     * Returns a {@link ICommandSender } for the finds player name.
     * <p>
     * If the player is not online, returns the server's command source stack.
     * </p>
     *
     * @param sourceName the name of the player or source
     * @return the command source stack
     */
    @NotNull
    public static ICommandSender getSource(@NotNull String sourceName) {
        MinecraftServer server = ExtraLib.getInstance().getServer();
        EntityPlayerMP player = server.getPlayerList().getPlayerByUsername(sourceName);

        return player != null ? player : server;
    }

    /**
     * Executes a command as the specified player.
     *
     * @param player  the player executing the command
     * @param command the command string
     */
    public static void executeCommand(@Nullable EntityPlayer player, @NotNull String command) {
        if (player != null) {
            ExtraLib.getInstance().getServer().commandManager.executeCommand(player, command);
        }
    }

    /**
     * Counts the number of {@link ItemStack} in the player's inventory that
     * matches the provided {@code searchItemStack}.
     *
     * @param player          the player
     * @param searchItemStack the item stack to search for
     * @return the total count of matching items
     */
    public static int countItemStack(@Nullable EntityPlayer player, @NotNull ItemStack searchItemStack) {
        if (player == null) {
            return 0;
        }

        int count = 0;

        for (ItemStack itemStack : player.inventory.mainInventory) {
            if (itemStack.isEmpty() || itemStack.getItem() != searchItemStack.getItem() || !ItemStack.areItemStackTagsEqual(itemStack, searchItemStack)) {
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
     * @param tag             the string tag
     * @return the total count of matching items
     */
    public static int countItemStackTag(@Nullable EntityPlayer player, @NotNull ItemStack searchItemStack, @NotNull String tag) {
        if (player == null) {
            return 0;
        }

        int count = 0;

        for (ItemStack itemStack : player.inventory.mainInventory) {
            if (itemStack.isEmpty() || itemStack.getItem() != searchItemStack.getItem()) {
                continue;
            }

            if (itemStack.getTagCompound() == null && searchItemStack.getTagCompound() == null) {
                count += itemStack.getCount();
                continue;
            }

            if (itemStack.getTagCompound() == null || searchItemStack.getTagCompound() == null) {
                continue;
            }

            if (!Objects.equals(itemStack.getTagCompound().getCompoundTag(tag), searchItemStack.getTagCompound().getCompoundTag(tag))) {
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
    public static void removeItemStack(@Nullable EntityPlayerMP player, @NotNull ItemStack removeItemStack, int amount) {
        if (player == null) {
            ExtraLib.getLogger().error("Item {} was not claimed because the player is null, this is an error.", removeItemStack.getDisplayName());
            return;
        }

        int totalRemoved = 0;

        InventoryPlayer playerContainer = player.inventory;

        for (ItemStack itemStack : playerContainer.mainInventory) {
            if (totalRemoved >= amount) {
                break;
            }

            if (itemStack.isEmpty() || itemStack.getItem() != removeItemStack.getItem() || !ItemStack.areItemStacksEqual(itemStack, removeItemStack)) {
                continue;
            }

            int toRemove = Math.min(itemStack.getCount(), amount - totalRemoved);

            itemStack.shrink(toRemove);
            totalRemoved += toRemove;
        }

        playerContainer.markDirty();
        player.sendContainerToPlayer(player.inventoryContainer);
    }

    /**
     * Removes a specific number of item stacks from the player's inventory
     * that match a specific data component.
     *
     * @param player          the player
     * @param removeItemStack the item stack to remove
     * @param tag             string tag
     * @param amount          the number of items to remove
     */
    public static void removeItemStackTag(@Nullable EntityPlayerMP player, @NotNull ItemStack removeItemStack, @NotNull String tag, int amount) {
        if (player == null) {
            ExtraLib.getLogger().error("Item {} was not claimed because the player is null, this is an error.", removeItemStack.getDisplayName());
            return;
        }

        int totalRemoved = 0;

        InventoryPlayer playerContainer = player.inventory;

        for (ItemStack itemStack : playerContainer.mainInventory) {
            if (totalRemoved >= amount) {
                break;
            }

            if (itemStack.isEmpty() || itemStack.getItem() != removeItemStack.getItem()) {
                continue;
            }

            int toRemove = Math.min(itemStack.getCount(), amount - totalRemoved);

            if (itemStack.getTagCompound() == null && removeItemStack.getTagCompound() == null) {
                itemStack.shrink(toRemove);
                totalRemoved += toRemove;
                continue;
            }

            if (itemStack.getTagCompound() == null || removeItemStack.getTagCompound() == null) {
                continue;
            }

            if (!Objects.equals(itemStack.getTagCompound().getCompoundTag(tag), removeItemStack.getTagCompound().getCompoundTag(tag))) {
                continue;
            }

            itemStack.shrink(toRemove);
            totalRemoved += toRemove;
        }

        playerContainer.markDirty();
        player.sendContainerToPlayer(player.inventoryContainer);
    }

    public static void giveItem(@Nullable EntityPlayer player, @NotNull ItemStack itemStack) {
        if (player == null) {
            ExtraLib.getLogger().error("Item {} was not issued because the player is null, this is an error.", itemStack.getDisplayName());
            return;
        }

        if (!player.addItemStackToInventory(itemStack)) {
            EntityItem itemEntity = player.dropItem(itemStack, false);

            if (itemEntity != null) {
                itemEntity.setNoPickupDelay();
                itemEntity.setOwner(player.getName());
            }
        }

        playSound(player, SoundEvents.ENTITY_ITEM_PICKUP);
        player.inventoryContainer.detectAndSendChanges();
    }

    /**
     * Checks if the player has at least one free slot in their inventory.
     *
     * @param player the player to check
     * @return true if the player has a free slot, false otherwise
     */
    public static boolean hasFreeSlot(@Nullable EntityPlayer player) {
        if (player == null || player.inventory == null) {
            return false;
        }

        for (int i = 0; i < player.inventory.mainInventory.size(); i++) {
            if (player.inventory.getStackInSlot(i).isEmpty()) {
                return true;
            }
        }

        return false;
    }

    public static void playSound(@Nullable EntityPlayer player, @NotNull SoundEvent soundEvent) {
        if (player != null) {
            Random random = player.world.rand;

            player.world.playSound(null, player.posX, player.posY, player.posZ, soundEvent,
                    SoundCategory.PLAYERS, 0.2F, (random.nextFloat() - random.nextFloat() * 0.7F + 1.0F) * 2.0F);
        }
    }
}