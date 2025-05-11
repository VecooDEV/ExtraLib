package com.vecoo.extralib.item;

import com.vecoo.extralib.ExtraLib;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class UtilItem {
    public static ItemStack parseItem(String itemId) {
        Item item = BuiltInRegistries.ITEM.get(new ResourceLocation(itemId));

        if (item == Items.AIR) {
            return ItemStack.EMPTY;
        }

        return item.getDefaultInstance();
    }

    public static ItemStack parseItemCustomModel(String itemId) {
        String[] parts = itemId.split(":");

        ItemStack itemStack = parseItem(parts[0] + ":" + parts[1]);

        if (itemStack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        if (parts.length == 3) {
            try {
                itemStack.getOrCreateTag().putInt("CustomModelData", Integer.parseInt(parts[2]));
            } catch (NumberFormatException e) {
                ExtraLib.getLogger().error("[ExtraLib] Invalid CustomModelData value in item: " + itemId);
            }
        }

        return itemStack;
    }
}
