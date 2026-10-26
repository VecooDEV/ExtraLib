package com.vecoo.extralib.functionaltest;

import com.vecoo.extralib.ui.api.elements.GuiElementBuilder;
import com.vecoo.extralib.util.ItemUtil;
import com.vecoo.extralib.util.PlayerUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;

public final class ItemTests {
    private ItemTests() {
    }

    public static void run(ServerPlayer player, Checks checks) {
        clear(player);
        player.setGameMode(GameType.SURVIVAL);
        ItemStack given = new ItemStack(Items.STONE, 10);
        PlayerUtil.giveItem(player, given);
        checks.check("give/empty inventory", held(player, Items.STONE) == 10 && dropped(player, Items.STONE) == 0);
        checks.check("give/consumes input", given.isEmpty());

        clear(player);
        fill(player);
        player.getInventory().setItem(0, new ItemStack(Items.STONE, 60));
        PlayerUtil.giveItem(player, new ItemStack(Items.STONE, 10));
        checks.check("give/partial inventory inserts available 4", held(player, Items.STONE) == 64);
        checks.check("give/partial inventory drops remaining 6", dropped(player, Items.STONE) == 6);
        checks.check("give/partial delivery preserves total", held(player, Items.STONE) + dropped(player, Items.STONE) == 70);

        clear(player);
        fill(player);
        checks.check("slots/full inventory", !PlayerUtil.hasFreeSlot(player));
        PlayerUtil.giveItem(player, new ItemStack(Items.STONE, 10));
        checks.check("give/full inventory drops all", held(player, Items.STONE) == 0 && dropped(player, Items.STONE) == 10);
        player.getInventory().setItem(35, ItemStack.EMPTY);
        checks.check("slots/one empty slot", PlayerUtil.hasFreeSlot(player));

        clear(player);
        PlayerUtil.giveItem(player, new ItemStack(Items.STONE, 130));
        checks.check("give/oversized stack preserves 130", held(player, Items.STONE) == 130 && dropped(player, Items.STONE) == 0);
        checks.check("give/splits into legal stacks", legalStacks(player));

        clear(player);
        ItemStack sword = new ItemStack(Items.IRON_SWORD);
        sword.setDamageValue(5);
        PlayerUtil.giveItem(player, sword);
        checks.check("give/damaged nonstackable item", held(player, Items.IRON_SWORD) == 1 && player.getInventory().getItem(0).getDamageValue() == 5);
        PlayerUtil.giveItem(player, ItemStack.EMPTY);
        checks.check("give/empty stack has no effect", held(player, Items.IRON_SWORD) == 1 && dropped(player, Items.AIR) == 0);

        clear(player);
        fill(player);
        player.setGameMode(GameType.CREATIVE);
        given = new ItemStack(Items.STONE, 10);
        PlayerUtil.giveItem(player, given);
        checks.check("give/creative overflow follows vanilla", given.isEmpty() && held(player, Items.STONE) == 0 && dropped(player, Items.STONE) == 0);
        player.setGameMode(GameType.SURVIVAL);

        clear(player);
        ItemStack tagged = model(3, 7, "One");
        ItemStack otherName = model(4, 7, "Two");
        player.getInventory().setItem(0, new ItemStack(Items.STONE, 2));
        player.getInventory().setItem(1, tagged.copy());
        player.getInventory().setItem(2, otherName.copy());
        player.getInventory().setItem(3, model(6, 8, "Other"));
        player.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.STONE, 5));
        player.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
        player.inventoryMenu.slots.get(0).set(new ItemStack(Items.STONE, 11));
        checks.check("count/non-strict excludes crafting preview", PlayerUtil.countItemStack(player, new ItemStack(Items.STONE), false) == 20);
        checks.check("count/strict compares all metadata", PlayerUtil.countItemStack(player, tagged) == 3);
        checks.check("count/selected metadata ignores unrelated name", PlatformItems.countTagged(player, tagged) == 7);
        checks.check("count/absent selected metadata matches absent", PlatformItems.countTagged(player, new ItemStack(Items.STONE)) == 7);
        checks.check("count/equipment included", PlayerUtil.countItemStack(player, new ItemStack(Items.IRON_HELMET)) == 1);
        checks.check("count/null player", PlayerUtil.countItemStack(null, tagged) == 0 && PlatformItems.countTagged(null, tagged) == 0);

        PlayerUtil.removeItemStack(player, tagged, 2);
        checks.check("remove/strict preserves other metadata", player.getInventory().getItem(1).getCount() == 1 && player.getInventory().getItem(2).getCount() == 4);
        PlatformItems.removeTagged(player, tagged, 3);
        checks.check("remove/selected metadata spans matching stacks", PlatformItems.countTagged(player, tagged) == 2 && player.getInventory().getItem(3).getCount() == 6);
        PlayerUtil.removeItemStack(player, new ItemStack(Items.STONE), 1000, false);
        checks.check("remove/more than available removes available", held(player, Items.STONE) == 0);
        checks.check("remove/crafting preview preserved", player.inventoryMenu.slots.get(0).getItem().getCount() == 11);
        checks.check("remove/other item types preserved", held(player, Items.IRON_HELMET) == 1);

        for (boolean strict : new boolean[]{false, true}) {
            clear(player);
            player.getInventory().setItem(0, new ItemStack(Items.STONE, 2));
            player.getInventory().setItem(1, new ItemStack(Items.STONE, 3));
            PlayerUtil.removeItemStack(player, player.getInventory().getItem(0), 4, strict);
            checks.check("remove/live template strict=" + strict, held(player, Items.STONE) == 1);
        }

        clear(player);
        player.getInventory().setItem(0, model(2, 7, "Same"));
        player.getInventory().setItem(1, model(3, 7, "Same"));
        PlatformItems.removeTagged(player, player.getInventory().getItem(0), 4);
        checks.check("remove/live selected metadata template", held(player, Items.STONE) == 1);
        PlayerUtil.removeItemStack(player, new ItemStack(Items.STONE), 0, false);
        PlayerUtil.removeItemStack(player, new ItemStack(Items.STONE), -5, false);
        PlatformItems.removeTagged(player, model(1, 7, "Same"), -5);
        checks.check("remove/nonpositive amount has no effect", held(player, Items.STONE) == 1);

        clear(player);
        player.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.STONE, 3));
        PlayerUtil.removeItemStack(player, new ItemStack(Items.STONE), 2);
        checks.check("remove/offhand", player.getItemBySlot(EquipmentSlot.OFFHAND).getCount() == 1);

        ItemStack original = model(12, 7, "Saved item");
        ItemStack restored = PlatformItems.roundTrip(original, PlatformItems.level(player).getServer());
        checks.check("serialization/count and metadata round trip", restored.getCount() == 12 && PlatformItems.same(original, restored));
        checks.check("parse/registered item", ItemUtil.parseItem("minecraft:stone").is(Items.STONE));
        clear(player);
    }

    private static ItemStack model(int count, int data, String name) {
        return new GuiElementBuilder(Items.STONE, count).setCustomModelData(data).setName(Component.literal(name)).getItemStack();
    }

    private static void fill(ServerPlayer player) {
        for (int i = 0; i < 36; i++) {
            player.getInventory().setItem(i, new ItemStack(Items.DIRT, 64));
        }
    }

    public static int held(ServerPlayer player, Item item) {
        int count = 0;

        for (int i = 0; i < 36; i++) {
            ItemStack stack = player.getInventory().getItem(i);

            if (stack.is(item)) {
                count += stack.getCount();
            }
        }

        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.OFFHAND}) {
            ItemStack stack = player.getItemBySlot(slot);

            if (stack.is(item)) {
                count += stack.getCount();
            }
        }

        return count;
    }

    private static boolean legalStacks(ServerPlayer player) {
        for (int i = 0; i < 36; i++) {
            ItemStack stack = player.getInventory().getItem(i);

            if (!stack.isEmpty() && stack.getCount() > stack.getMaxStackSize()) {
                return false;
            }
        }

        return true;
    }

    private static int dropped(ServerPlayer player, Item item) {
        return PlatformItems.level(player).getEntitiesOfClass(ItemEntity.class, player.getBoundingBox().inflate(16)).stream()
                .filter(entity -> entity.getItem().is(item)).mapToInt(entity -> entity.getItem().getCount()).sum();
    }

    public static void clear(ServerPlayer player) {
        player.getInventory().clearContent();

        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.OFFHAND, EquipmentSlot.MAINHAND}) {
            player.setItemSlot(slot, ItemStack.EMPTY);
        }
        player.inventoryMenu.slots.get(0).set(ItemStack.EMPTY);
        PlatformItems.level(player).getEntitiesOfClass(ItemEntity.class, player.getBoundingBox().inflate(16)).forEach(ItemEntity::discard);
        player.inventoryMenu.setCarried(ItemStack.EMPTY);
    }
}
