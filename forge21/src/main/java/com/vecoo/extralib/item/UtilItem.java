package com.vecoo.extralib.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

public class UtilItem {
    public static ItemStack parseItemId(String itemId) {
        Item item = ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(itemId));
        if (item == null) {
            return new ItemStack(Items.STONE);
        }

        return item.getDefaultInstance();
    }
}
