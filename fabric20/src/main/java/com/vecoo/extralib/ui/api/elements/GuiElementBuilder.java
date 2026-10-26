package com.vecoo.extralib.ui.api.elements;

import com.mojang.authlib.GameProfile;
import com.vecoo.extralib.ExtraLib;
import com.mojang.authlib.properties.Property;
import com.vecoo.extralib.ui.api.GuiHelpers;
import com.vecoo.extralib.util.ItemUtil;
import com.vecoo.extralib.util.TextUtil;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.core.registries.BuiltInRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GuiElementBuilder implements GuiElementBuilderInterface<GuiElementBuilder> {
    protected ItemStack itemStack = new ItemStack(Items.STONE);
    protected GuiElement.ClickCallback callback = GuiElementInterface.EMPTY_CALLBACK;
    private boolean noTooltips = true;

    public GuiElementBuilder() {
    }

    public GuiElementBuilder(@NotNull Item item) {
        this.itemStack = new ItemStack(item);
    }

    public GuiElementBuilder(@NotNull Item item, int count) {
        this.itemStack = new ItemStack(item, count);
    }

    public GuiElementBuilder(@NotNull ItemStack itemStack) {
        this.itemStack = itemStack.copy();
    }

    public GuiElementBuilder(@NotNull String itemId) {
        this.itemStack = ItemUtil.parseItemCustomModel(itemId);
    }

    @NotNull
    public static GuiElementBuilder from(@NotNull ItemStack stack) {
        return new GuiElementBuilder(stack);
    }

    @NotNull
    public static List<Component> getLore(@NotNull ItemStack stack) {
        List<Component> result = new ArrayList<>();
        CompoundTag tag = stack.getTagElement("display");
        if (tag != null) {
            ListTag lore = tag.getList("Lore", Tag.TAG_STRING);
            for (int index = 0; index < lore.size(); index++) {
                Component line = Component.Serializer.fromJson(lore.getString(index));
                if (line != null) {
                    result.add(line);
                }
            }
        }
        return result;
    }

    @NotNull
    public GuiElementBuilder setItem(@NotNull Item item) {
        ItemStack replacement = new ItemStack(item, this.itemStack.getCount());
        if (this.itemStack.getTag() != null) {
            replacement.setTag(this.itemStack.getTag().copy());
        }
        this.itemStack = replacement;
        return this;
    }

    @NotNull
    public GuiElementBuilder setName(@NotNull Component name) {
        this.itemStack.setHoverName(name.copy().withStyle(GuiHelpers.STYLE_CLEARER));
        return this;
    }

    @NotNull
    public GuiElementBuilder setFormattedName(@NotNull String name) {
        return setName(TextUtil.formatMessage(name));
    }

    @NotNull
    public GuiElementBuilder setCount(int count) {
        this.itemStack.setCount(count);
        return this;
    }

    @NotNull
    public GuiElementBuilder setLore(@NotNull List<Component> lore) {
        List<Component> styled = new ArrayList<>(lore.size());
        for (Component line : lore) {
            styled.add(line.copy().withStyle(GuiHelpers.STYLE_CLEARER));
        }
        return setLoreRaw(styled);
    }

    @NotNull
    public GuiElementBuilder setLoreRaw(@NotNull List<Component> lore) {
        ListTag list = new ListTag();
        for (Component line : lore) {
            list.add(StringTag.valueOf(Component.Serializer.toJson(line)));
        }
        this.itemStack.getOrCreateTagElement("display").put("Lore", list);
        return this;
    }

    @NotNull
    public GuiElementBuilder addLoreLine(@NotNull Component lore) {
        return addLoreLineRaw(lore.copy().withStyle(GuiHelpers.STYLE_CLEARER));
    }

    @NotNull
    public GuiElementBuilder addLoreLineRaw(@NotNull Component lore) {
        List<Component> lines = getLore(this.itemStack);
        lines.add(lore);
        return setLoreRaw(lines);
    }

    @NotNull
    public GuiElementBuilder setDamage(int damage) {
        this.itemStack.setDamageValue(damage);
        return this;
    }

    @NotNull
    public GuiElementBuilder tooltip() {
        this.noTooltips = false;
        return this;
    }

    @NotNull
    public GuiElementBuilder enchant(@NotNull Enchantment enchantment, int level) {
        this.itemStack.enchant(enchantment, level);
        return this;
    }

    @NotNull
    public GuiElementBuilder enchant(@NotNull Holder<Enchantment> enchantment, int level) {
        return enchant(enchantment.value(), level);
    }

    @NotNull
    public GuiElementBuilder enchant(@NotNull ResourceKey<Enchantment> enchantment, int level) {
        Enchantment value = BuiltInRegistries.ENCHANTMENT.getOptional(enchantment.location()).orElse(null);
        if (value != null) {
            enchant(value, level);
        }
        return this;
    }

    @NotNull
    public GuiElementBuilder glow() {
        if (!this.itemStack.isEnchanted()) {
            ListTag enchantments = new ListTag();
            enchantments.add(new CompoundTag());
            this.itemStack.getOrCreateTag().put("Enchantments", enchantments);
            this.itemStack.getOrCreateTag().putBoolean("ExtraLibGlow", true);
        }
        return this;
    }

    /** Removes only the synthetic glow; real enchantments retain their vanilla glint. */
    @NotNull
    public GuiElementBuilder glow(boolean value) {
        if (value) {
            return glow();
        }
        CompoundTag tag = this.itemStack.getTag();
        if (tag != null && tag.getBoolean("ExtraLibGlow")) {
            ListTag enchantments = tag.getList("Enchantments", Tag.TAG_COMPOUND);
            if (enchantments.size() == 1 && enchantments.getCompound(0).isEmpty()) {
                tag.remove("Enchantments");
            }
            tag.remove("ExtraLibGlow");
        }
        return this;
    }

    @NotNull
    public GuiElementBuilder setCustomModelData(int value) {
        this.itemStack.getOrCreateTag().putInt("CustomModelData", value);
        return this;
    }

    @NotNull
    public GuiElementBuilder unbreakable() {
        this.itemStack.getOrCreateTag().putBoolean("Unbreakable", true);
        return this;
    }

    @NotNull
    public GuiElementBuilder setSkullOwner(@NotNull GameProfile profile) {
        MinecraftServer server = ExtraLib.getCurrentServer();
        if (server != null && !profile.getProperties().containsKey("textures")) {
            profile = server.getSessionService().fillProfileProperties(profile, false);
        }
        this.itemStack.getOrCreateTag().put("SkullOwner", NbtUtils.writeGameProfile(new CompoundTag(), profile));
        return this;
    }

    @NotNull
    public GuiElementBuilder setSkullOwner(@NotNull String value) {
        return setSkullOwner(value, null, null);
    }

    @NotNull
    public GuiElementBuilder setSkullOwner(@NotNull String value, @Nullable String signature, @Nullable UUID uuid) {
        GameProfile profile = new GameProfile(uuid == null ? new UUID(0L, 0L) : uuid, null);
        profile.getProperties().put("textures", signature == null ? new Property("textures", value) : new Property("textures", value, signature));
        this.itemStack.getOrCreateTag().put("SkullOwner", NbtUtils.writeGameProfile(new CompoundTag(), profile));
        return this;
    }

    @NotNull
    public CompoundTag getTag() {
        return this.itemStack.getOrCreateTag();
    }

    @NotNull
    public GuiElementBuilder setTag(@Nullable CompoundTag tag) {
        this.itemStack.setTag(tag == null ? null : tag.copy());
        return this;
    }

    @Override
    public GuiElementBuilder setCallback(@NotNull GuiElement.ClickCallback callback) {
        this.callback = callback;
        return this;
    }

    @Override
    public GuiElementBuilder setCallback(@NotNull GuiElementInterface.ItemClickCallback callback) {
        this.callback = callback;
        return this;
    }

    @NotNull
    public ItemStack getItemStack() {
        ItemStack copy = this.itemStack.copy();
        if (this.noTooltips) {
            copy.getOrCreateTag().putInt("HideFlags", copy.getOrCreateTag().getInt("HideFlags") | 32);
        }
        return copy;
    }

    @Override
    public GuiElement build() {
        return new GuiElement(getItemStack(), this.callback);
    }
}
