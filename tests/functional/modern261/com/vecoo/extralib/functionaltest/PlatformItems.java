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
import net.minecraft.world.inventory.ContainerInput;
import com.vecoo.extralib.ui.api.elements.GuiElementBuilder;
import com.vecoo.extralib.util.WorldUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.phys.AABB;
import java.util.List;

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
        return ClickTypes.toClickType(ContainerInput.valueOf(action), button, slot);
    }

    public static ServerLevel level(ServerPlayer player) {
        return player.level();
    }

    public static void additionalChecks(ServerPlayer player, Checks checks) {
        ItemStack model = ItemUtil.parseItemCustomModel("minecraft:stone:7");
        checks.check("26.1/custom model floats", model.get(DataComponents.CUSTOM_MODEL_DATA).floats().equals(List.of(7.0F)));

        ItemStack legacy = ItemUtil.deserializeLegacy("{id:\"minecraft:stone\",Count:3b,tag:{CustomModelData:7}}", 3465, player.level().getServer());
        checks.check("26.1/legacy migration", legacy.is(Items.STONE) && legacy.getCount() == 3 && legacy.get(DataComponents.CUSTOM_MODEL_DATA).floats().equals(List.of(7.0F)));

        ItemStack potion = new GuiElementBuilder(Items.POTION)
                .setName(Component.literal("Name"))
                .setLore(List.of(Component.literal("Lore")))
                .getItemStack();
        TooltipDisplay display = potion.get(DataComponents.TOOLTIP_DISPLAY);
        checks.check("26.1/tooltip keeps name and lore", !display.hideTooltip() && !display.shows(DataComponents.POTION_CONTENTS) && display.shows(DataComponents.CUSTOM_NAME) && display.shows(DataComponents.LORE));

        ItemStack head = new GuiElementBuilder(Items.PLAYER_HEAD)
                .setSkullOwner("test-texture").getItemStack();
        checks.check("26.1/head texture", head.get(DataComponents.PROFILE).partialProfile().properties().get("textures").iterator().next().value().equals("test-texture"));
        checks.check("26.1/head round trip", same(head, roundTrip(head, player.level().getServer())));

        ItemStack sword = new GuiElementBuilder(Items.IRON_SWORD).unbreakable().getItemStack();
        checks.check("26.1/unbreakable component", sword.has(DataComponents.UNBREAKABLE));
        AABB bounds = WorldUtil.getWorldBounds(player.level());
        checks.check("26.1/world height", bounds.minY == player.level().getMinY() && bounds.maxY == player.level().getMaxY() + 1);
    }
}
