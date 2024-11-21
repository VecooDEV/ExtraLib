package com.vecoo.extralib.player;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.UsernameCache;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class UtilPlayer {
    public static UUID getUUID(String player) {
        return UsernameCache.getMap().entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey)).get(player);
    }

    public static boolean hasUUID(String player) {
        return UsernameCache.getMap().entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey)).containsKey(player);
    }

    public static boolean isOp(ServerPlayer player) {
        return player.hasPermissions(2);
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