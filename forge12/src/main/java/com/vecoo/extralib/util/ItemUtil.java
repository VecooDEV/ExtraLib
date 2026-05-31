package com.vecoo.extralib.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class ItemUtil {
    private ItemUtil() {
    }

    /**
     * Parses an item ID into an {@link ItemStack}. If the item does not exist,
     * returns {@link ItemStack#EMPTY}.
     *
     * @param itemId the string item ID in namespace:path format
     * @return the parsed ItemStack or {@link ItemStack#EMPTY} if invalid
     */
    @NotNull
    public static ItemStack parseItem(@NotNull String itemId) {
        Item item = Item.getByNameOrId(itemId);

        if (item == null) {
            return ItemStack.EMPTY;
        }

        return item.getDefaultInstance();
    }
}