package com.vecoo.extralib.item;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.vecoo.extralib.ExtraLib;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public final class UtilItem {
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
        return itemStack.save(new CompoundTag()).getAsString();
    }

    /**
     * Deserializes an {@link ItemStack} from a JSON structure using the game's registry context.
     *
     * @param itemStack the JSON data describing the ItemStack
     * @return the deserialized ItemStack
     */
    @NotNull
    public static ItemStack deserialize(@NotNull String itemStack) {
        try {
            return ItemStack.of(TagParser.parseTag(itemStack));
        } catch (CommandSyntaxException e) {
            ExtraLib.getLogger().error("Invalid tag item.");
            return Items.STONE.getDefaultInstance();
        }
    }
}
