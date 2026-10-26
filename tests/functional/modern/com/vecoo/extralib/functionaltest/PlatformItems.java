package com.vecoo.extralib.functionaltest;

import com.vecoo.extralib.util.ItemUtil;
import com.vecoo.extralib.util.PlayerUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import com.vecoo.extralib.ui.api.ClickTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ClickType;

public final class PlatformItems {
    private PlatformItems() {
    }

    public static int countTagged(Player player, ItemStack item) {
        return PlayerUtil.countItemStackTag(player, item, DataComponents.CUSTOM_MODEL_DATA);
    }

    public static void removeTagged(Player player, ItemStack item, int amount) {
        PlayerUtil.removeItemStackTag(player, item, DataComponents.CUSTOM_MODEL_DATA, amount);
    }

    public static ItemStack roundTrip(ItemStack item, MinecraftServer server) {
        return ItemUtil.deserialize(ItemUtil.serialize(item, server), server);
    }

    public static boolean same(ItemStack first, ItemStack second) {
        return ItemStack.isSameItemSameComponents(first, second);
    }

    public static ClickTypes clickType(String action, int button, int slot) {
        return ClickTypes.toClickType(ClickType.valueOf(action), button, slot);
    }

    public static ServerLevel level(ServerPlayer player) {
        return player.serverLevel();
    }

    public static void additionalChecks(ServerPlayer player, Checks checks) {
    }
}
