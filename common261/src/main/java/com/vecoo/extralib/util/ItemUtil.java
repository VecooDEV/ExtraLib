package com.vecoo.extralib.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.nbt.NbtOps;
import net.minecraft.SharedConstants;
import com.mojang.serialization.Dynamic;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.vecoo.extralib.internal.ExtraLibLogger;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomModelData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
        Identifier resourceLocation = Identifier.tryParse(itemId);

        if (resourceLocation == null) {
            return ItemStack.EMPTY;
        }

        Item item = BuiltInRegistries.ITEM.getValue(resourceLocation);

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
        String[] parts = itemId.split(":", -1);

        if (parts.length < 2 || parts.length > 3 || parts[0].isBlank() || parts[1].isBlank()) {
            return ItemStack.EMPTY;
        }

        ItemStack itemStack = parseItem(parts[0] + ":" + parts[1]);

        if (itemStack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        if (parts.length == 3) {
            try {
                itemStack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(List.of((float) Integer.parseInt(parts[2])), List.of(), List.of(), List.of()));
            } catch (NumberFormatException e) {
                ExtraLibLogger.getLogger().error("Invalid CustomModelData value in item: {}.", itemId);
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
        if (itemStack.isEmpty()) {
            JsonObject air = new JsonObject();

            air.addProperty("id", "minecraft:air");
            air.addProperty("count", 0);
            return air;
        }

        return ItemStack.CODEC.encodeStart(server.registryAccess().createSerializationContext(JsonOps.INSTANCE), itemStack).getOrThrow();
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
        try {
            if (jsonElement.isJsonObject()) {
                JsonObject object = jsonElement.getAsJsonObject();

                if (object.has("id") && !object.get("id").isJsonNull() && "minecraft:air".equals(object.get("id").getAsString())) {
                    return ItemStack.EMPTY;
                }
            }
            return ItemStack.CODEC.decode(server.registryAccess().createSerializationContext(JsonOps.INSTANCE), jsonElement).getOrThrow().getFirst();
        } catch (RuntimeException error) {
            ExtraLibLogger.getLogger().error("Invalid item data.", error);
            return ItemStack.EMPTY;
        }
    }

    /** Migrates SNBT saved on an older Minecraft version using its DataVersion. */
    @NotNull
    public static ItemStack deserializeLegacy(@NotNull String snbt, int dataVersion, @NotNull MinecraftServer server) {
        try {
            CompoundTag tag = (CompoundTag) server.getFixerUpper().update(References.ITEM_STACK,
                    new Dynamic<>(NbtOps.INSTANCE, TagParser.parseCompoundFully(snbt)), dataVersion,
                    SharedConstants.getCurrentVersion().dataVersion().version()).getValue();

            return ItemStack.CODEC.parse(server.registryAccess().createSerializationContext(NbtOps.INSTANCE), tag).getOrThrow();
        } catch (CommandSyntaxException | RuntimeException error) {
            ExtraLibLogger.getLogger().error("Invalid legacy item data.", error);
            return ItemStack.EMPTY;
        }
    }

}
