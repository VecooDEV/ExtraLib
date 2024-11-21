package com.vecoo.extralib.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class UtilItemStack {
    public static ItemStack parseItemId(String itemId) {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemId));
        if (item == null) {
            return new ItemStack(Items.BARRIER);
        }

        return item.getDefaultInstance();
    }
}
