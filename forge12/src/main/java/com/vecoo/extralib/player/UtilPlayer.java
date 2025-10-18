package com.vecoo.extralib.player;

import com.vecoo.extralib.ExtraLib;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.UsernameCache;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class UtilPlayer {
    @Nullable
    public static UUID getUUID(@Nonnull String playerName) {
        return UsernameCache.getMap().entrySet().stream()
                .filter(entry -> entry.getValue().equalsIgnoreCase(playerName))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    public static boolean hasUUID(@Nonnull String playerName) {
        return getUUID(playerName) != null;
    }

    @Nonnull
    public static String getPlayerName(@Nonnull UUID playerUUID) {
        String name = UsernameCache.getLastKnownUsername(playerUUID);
        return name != null ? name : "Unknown";
    }

    public static void sendMessageUuid(@Nonnull UUID playerUUID, @Nonnull TextComponentString message) {
        ExtraLib.getInstance().getServer().getPlayerList().getPlayerByUUID(playerUUID).sendMessage(message);
    }

    public static void sendMessageUuid(@Nonnull UUID playerUUID, @Nonnull ITextComponent message) {
        ExtraLib.getInstance().getServer().getPlayerList().getPlayerByUUID(playerUUID).sendMessage(message);
    }

    @Nullable
    public static EntityPlayerMP getPlayer(@Nonnull String playerName) {
        return ExtraLib.getInstance().getServer().getPlayerList().getPlayerByUsername(playerName);
    }

    @Nonnull
    public static ICommandSender getSource(@Nonnull String sourceName) {
        MinecraftServer server = ExtraLib.getInstance().getServer();

        EntityPlayerMP player = server.getPlayerList().getPlayerByUsername(sourceName);
        return player != null ? player.getCommandSenderEntity() : server;
    }

    public static int countItemStack(@Nonnull EntityPlayerMP player, @Nonnull ItemStack searchItemStack) {
        int count = 0;

        for (ItemStack itemStack : player.inventory.mainInventory) {
            if (itemStack.isEmpty() || itemStack.getItem() != searchItemStack.getItem() || !ItemStack.areItemStackTagsEqual(itemStack, searchItemStack)) {
                continue;
            }

            count += itemStack.getCount();
        }

        return count;
    }

    public static int countItemStackTag(@Nonnull EntityPlayerMP player, @Nonnull ItemStack searchItemStack, @Nonnull String tag) {
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

    public static void removeItemStack(@Nonnull EntityPlayerMP player, @Nonnull ItemStack removeItemStack, int amount) {
        int totalRemoved = 0;

        InventoryPlayer inventoryPlayer = player.inventory;

        for (ItemStack itemStack : inventoryPlayer.mainInventory) {
            if (totalRemoved >= amount) {
                break;
            }

            if (itemStack.isEmpty() || itemStack.getItem() != removeItemStack.getItem() || !ItemStack.areItemStackTagsEqual(itemStack, removeItemStack)) {
                continue;
            }

            int toRemove = Math.min(itemStack.getCount(), amount - totalRemoved);

            itemStack.shrink(toRemove);
            totalRemoved += toRemove;
        }

        inventoryPlayer.markDirty();
        player.sendContainerToPlayer(player.inventoryContainer);
    }

    public static void removeItemStackTag(@Nonnull EntityPlayerMP player, @Nonnull ItemStack removeItemStack, @Nonnull String tag, int amount) {
        int totalRemoved = 0;

        InventoryPlayer inventoryPlayer = player.inventory;

        for (ItemStack itemStack : inventoryPlayer.mainInventory) {
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

        inventoryPlayer.markDirty();
        player.sendContainerToPlayer(player.inventoryContainer);
    }

    public static boolean hasFreeSlot(@Nullable EntityPlayerMP player) {
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
}