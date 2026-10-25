package com.vecoo.extralib.functionaltest;

import com.vecoo.extralib.util.ItemUtil;
import com.vecoo.extralib.util.PlayerUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class PlatformItems {
    private PlatformItems() {
    }

    public static int countTagged(Player player, ItemStack item) {
        return PlayerUtil.countItemStackTag(player, item, "CustomModelData");
    }

    public static void removeTagged(Player player, ItemStack item, int amount) {
        PlayerUtil.removeItemStackTag(player, item, "CustomModelData", amount);
    }

    public static ItemStack roundTrip(ItemStack item, MinecraftServer server) {
        return ItemUtil.deserialize(ItemUtil.serialize(item));
    }

    public static boolean same(ItemStack first, ItemStack second) {
        return ItemStack.isSameItemSameTags(first, second);
    }
}
