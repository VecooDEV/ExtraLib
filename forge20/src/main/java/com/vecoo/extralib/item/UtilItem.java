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

public class UtilItem {
    @NotNull
    public static ItemStack parseItem(@NotNull String itemId) {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemId));

        if (item == null) {
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
                itemStack.getOrCreateTag().putInt("CustomModelData", Integer.parseInt(parts[2]));
            } catch (NumberFormatException e) {
                ExtraLib.getLogger().error("Invalid CustomModelData value in item: " + itemId);
            }
        }

        return itemStack;
    }

    @NotNull
    public static String serialize(@NotNull ItemStack itemStack) {
        return itemStack.save(new CompoundTag()).getAsString();
    }

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
