package com.vecoo.extralib.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class UtilItem {
    @Nonnull
    public static ItemStack parseItem(@Nonnull String itemId) {
        Item item = Item.getByNameOrId(itemId);

        if (item == null) {
            return ItemStack.EMPTY;
        }

        return item.getDefaultInstance();
    }
}
