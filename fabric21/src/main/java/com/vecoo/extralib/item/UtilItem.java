package com.vecoo.extralib.item;

import com.vecoo.extralib.ExtraLib;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomModelData;
import org.jetbrains.annotations.NotNull;

public class UtilItem {
    @NotNull
    public static ItemStack parseItem(@NotNull String itemId) {
        Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId));

        if (item == Items.AIR) {
            return ItemStack.EMPTY;
        }

        return item.getDefaultInstance();
    }

    @NotNull
    public static ItemStack parseItemCustomModel(@NotNull String itemId) {
        String[] parts = itemId.split(":");

        ItemStack itemStack = parseItem(parts[0] + ":" + parts[1]);

        if (itemStack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        if (parts.length == 3) {
            try {
                itemStack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(Integer.parseInt(parts[2])));
            } catch (NumberFormatException e) {
                ExtraLib.getLogger().error("[ExtraLib] Invalid CustomModelData value in item: " + itemId);
            }
        }

        return itemStack;
    }
}
