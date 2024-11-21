package com.vecoo.extralib.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
