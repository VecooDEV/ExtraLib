package com.vecoo.extralib.util;

import com.vecoo.extralib.ExtraLib;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
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
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemId));

        if (item == null) {
            return ItemStack.EMPTY;
        }

        return item.getDefaultInstance();
    }

    /**
     * Parses an {@link ItemStack} that may contain CustomModelData.
     * <p>
     * Expected formats:
     * <ul>
     *     <li>{@code namespace:item}</li>
     *     <li>{@code namespace:item:modelData}</li>
     * </ul>
     *
     * @param itemId the string item ID, optionally including CustomModelData
     * @return the parsed ItemStack or {@link ItemStack#EMPTY} if invalid
     */
    @NotNull
    public static ItemStack parseItemCustomModel(@NotNull String itemId) {
        String[] parts = itemId.split(":");

        ItemStack itemStack = parseItem(parts[0] + ":" + parts[1]);

        if (itemStack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        if (parts.length == 3) {
            try {
                itemStack.getOrCreateTag().putInt("CustomModelData", Integer.parseInt(parts[2]));
            } catch (NumberFormatException e) {
                ExtraLib.getLogger().error("Invalid CustomModelData value in item: {}.", itemId);
            }
        }

        return itemStack;
    }

    /**
     * Serializes an {@link ItemStack} to a JSON representation using the game's registry context.
     *
     * @param itemStack the ItemStack to serialize
     * @return a JSON element representing the serialized ItemStack
     */
    @NotNull
    public static String serialize(@NotNull ItemStack itemStack) {
        return itemStack.save(new CompoundNBT()).getAsString();
    }
}