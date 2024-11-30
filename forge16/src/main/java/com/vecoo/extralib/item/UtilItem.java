package com.vecoo.extralib.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class UtilItem {
    public static ItemStack parseItemId(String itemId) {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemId));
        if (item == null) {
            return new ItemStack(Items.STONE);
        }

        return item.getDefaultInstance();
    }
}
