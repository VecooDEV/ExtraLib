package com.vecoo.extralib.item;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import com.vecoo.extralib.ExtraLib;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomModelData;
import org.jetbrains.annotations.NotNull;

public class UtilItem {
    /**
     * Parses an item ID into an {@link ItemStack}. If the item does not exist,
     * returns {@link ItemStack#EMPTY}.
     *
     * @param itemId the string item ID in namespace:path format
     * @return the parsed ItemStack or {@link ItemStack#EMPTY} if invalid
     */
    @NotNull
    public static ItemStack parseItem(@NotNull String itemId) {
        Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId));

        if (item == Items.AIR) {
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
                itemStack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(Integer.parseInt(parts[2])));
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
     * @param server    the Minecraft server providing registry access
     * @return a JSON element representing the serialized ItemStack
     */
    @NotNull
    public static JsonElement serialize(@NotNull ItemStack itemStack, @NotNull MinecraftServer server) {
        return ItemStack.CODEC.encodeStart(server.registryAccess().createSerializationContext(JsonOps.INSTANCE),
                itemStack).getOrThrow();
    }

    /**
     * Deserializes an {@link ItemStack} from a JSON structure using the game's registry context.
     *
     * @param jsonElement the JSON data describing the ItemStack
     * @param server      the Minecraft server providing registry access
     * @return the deserialized ItemStack
     */
    @NotNull
    public static ItemStack deserialize(@NotNull JsonElement jsonElement, @NotNull MinecraftServer server) {
        return ItemStack.CODEC.decode(server.registryAccess().createSerializationContext(JsonOps.INSTANCE),
                jsonElement).getOrThrow().getFirst();
    }
}
