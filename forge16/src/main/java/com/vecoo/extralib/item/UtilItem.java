package com.vecoo.extralib.item;

import com.vecoo.extralib.ExtraLib;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class UtilItem {
    @Deprecated
    public static ItemStack parseItemId(String itemId) {
        return parseItem(itemId);
    }

    public static ItemStack parseItem(String itemId) {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemId));
        if (item == null) {
            return ItemStack.EMPTY;
        }

        return item.getDefaultInstance();
    }

    public static ItemStack parsedItemCustomModel(String id) {
        String[] parts = id.split(":");

        ItemStack itemStack = parseItem(parts[0] + ":" + parts[1]);

        if (itemStack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        if (parts.length == 3) {
            try {
                itemStack.getOrCreateTag().putInt("CustomModelData", Integer.parseInt(parts[2]));
            } catch (NumberFormatException e) {
                ExtraLib.getLogger().error("[ExtraLib] Invalid CustomModelData value in item \"{}\".", id);
            }
        }

        return itemStack;
    }
}
