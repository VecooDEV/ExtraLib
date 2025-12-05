package com.vecoo.extralib.ui.api.elements;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTextures;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.ProfileResult;
import com.vecoo.extralib.ExtraLib;
import com.vecoo.extralib.ui.api.GuiHelpers;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Unit;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    @NotNull
    public static GuiElementBuilder from(@NotNull ItemStack itemStack) {
        return new GuiElementBuilder(itemStack);
    }

    @Deprecated
    public static List<Component> getLore(@NotNull ItemStack stack) {
        return stack.getOrDefault(DataComponents.LORE, ItemLore.EMPTY).lines();
    }

    @NotNull
    public GuiElementBuilder setItem(Item item) {
        this.itemStack = new ItemStack(item.builtInRegistryHolder(), this.itemStack.getCount(), this.itemStack.getComponentsPatch());
        return this;
    }

    @NotNull
    public GuiElementBuilder setName(@NotNull Component name) {
        this.itemStack.set(DataComponents.CUSTOM_NAME, name.copy().withStyle(GuiHelpers.STYLE_CLEARER));
        return this;
    }

    @NotNull
    public GuiElementBuilder setItemName(@NotNull Component name) {
        this.itemStack.set(DataComponents.ITEM_NAME, name.copy());
        return this;
    }

    @NotNull
    public GuiElementBuilder setRarity(@NotNull Rarity rarity) {
        this.itemStack.set(DataComponents.RARITY, rarity);
        return this;
    }

    @NotNull
    public GuiElementBuilder setCount(int count) {
        this.itemStack.setCount(count);
        return this;
    }

    @NotNull
    public GuiElementBuilder setMaxCount(int count) {
        this.itemStack.set(DataComponents.MAX_STACK_SIZE, count);
        return this;
    }

    @NotNull
    public GuiElementBuilder setLore(@NotNull List<Component> lore) {
        List<Component> newLore = new ArrayList<>(lore.size());

        for (Component component : lore) {
            newLore.add(component.copy().withStyle(GuiHelpers.STYLE_CLEARER));
        }

        this.itemStack.set(DataComponents.LORE, new ItemLore(newLore));
        return this;
    }


    @NotNull
    public GuiElementBuilder setLoreRaw(@NotNull List<Component> lore) {
        this.itemStack.set(DataComponents.LORE, new ItemLore(lore));
        return this;
    }

    @NotNull
    public GuiElementBuilder addLoreLine(@NotNull Component lore) {
        this.itemStack.update(DataComponents.LORE, ItemLore.EMPTY, lore.copy().withStyle(GuiHelpers.STYLE_CLEARER), ItemLore::withLineAdded);
        return this;
    }

    @NotNull
    public GuiElementBuilder addLoreLineRaw(@NotNull Component lore) {
        this.itemStack.update(DataComponents.LORE, ItemLore.EMPTY, lore, ItemLore::withLineAdded);
        return this;
    }

    @NotNull
    public GuiElementBuilder setDamage(int damage) {
        this.itemStack.set(DataComponents.DAMAGE, damage);
        return this;
    }

    @NotNull
    public GuiElementBuilder setMaxDamage(int damage) {
        this.itemStack.set(DataComponents.MAX_DAMAGE, damage);
        return this;
    }

    @NotNull
    public GuiElementBuilder noDefaults() {
        for (TypedDataComponent<?> typedDataComponent : this.itemStack.getItem().components()) {
            if (this.itemStack.get(typedDataComponent.type()) == typedDataComponent.value()) {
                this.itemStack.set(typedDataComponent.type(), null);
            }
        }

        return this;
    }

    @Nullable
    public <T> T getComponent(@NotNull DataComponentType<T> type) {
        return this.itemStack.get(type);
    }

    @NotNull
    public <T> GuiElementBuilder setComponent(@NotNull DataComponentType<T> type, @Nullable T value) {
        this.itemStack.set(type, value);
        return this;
    }

    @NotNull
    public GuiElementBuilder tooltip() {
        this.noTooltips = false;
        return this;
    }

    @NotNull
    public GuiElementBuilder enchant(@NotNull Holder<Enchantment> enchantment, int level) {
        this.itemStack.enchant(enchantment, level);
        return this;
    }

    @NotNull
    public GuiElementBuilder enchant(@NotNull ResourceKey<Enchantment> enchantment, int level) {
        return enchant(ExtraLib.instance().server().registryAccess(), enchantment, level);
    }

    @NotNull
    public GuiElementBuilder enchant(@NotNull HolderLookup.Provider lookup, @NotNull ResourceKey<Enchantment> enchantment, int level) {
        return enchant(lookup.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(enchantment), level);
    }

    @NotNull
    public GuiElementBuilder glow() {
        this.itemStack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        return this;
    }

    @NotNull
    public GuiElementBuilder glow(boolean value) {
        this.itemStack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, value);
        return this;
    }

    @NotNull
    public GuiElementBuilder setCustomModelData(int value) {
        this.itemStack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(value));
        return this;
    }


    @NotNull
    public GuiElementBuilder unbreakable() {
        this.itemStack.set(DataComponents.UNBREAKABLE, new Unbreakable(true));
        return this;
    }

    @NotNull
    public GuiElementBuilder setSkullOwner(@NotNull GameProfile profile) {
        if (ExtraLib.instance().server().getSessionService().getTextures(profile) == MinecraftProfileTextures.EMPTY) {
            ProfileResult profileResult = ExtraLib.instance().server().getSessionService().fetchProfile(profile.getId(), false);

            if (profileResult != null) {
                profile = profileResult.profile();
            }
        }

        this.itemStack.set(DataComponents.PROFILE, new ResolvableProfile(profile));
        return this;
    }

    public GuiElementBuilder setSkullOwner(@NotNull String value) {
        return this.setSkullOwner(value, null, null);
    }

    @NotNull
    public GuiElementBuilder setSkullOwner(@NotNull String value, @Nullable String signature, @Nullable UUID uuid) {
        PropertyMap map = new PropertyMap();

        map.put("textures", new Property("textures", value, signature));
        this.itemStack.set(DataComponents.PROFILE, new ResolvableProfile(Optional.empty(), Optional.ofNullable(uuid), map));
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
        ItemStack copy = itemStack.copy();

        if (this.noTooltips) {
            copy.set(DataComponents.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE);
        }

        return copy;
    }

    @Override
    public GuiElement build() {
        return new GuiElement(this.getItemStack(), this.callback);
    }
}
