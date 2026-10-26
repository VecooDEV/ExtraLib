package com.vecoo.extralib.ui.api.elements;

import com.vecoo.extralib.ui.api.gui.GuiInterface;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GuiElement implements GuiElementInterface {
    protected ItemStack item;
    protected final ClickCallback callback;

    public GuiElement(@NotNull ItemStack item, @NotNull ClickCallback callback) {
        this.item = item;
        this.callback = callback;
    }

    public GuiElement(@NotNull ItemStack item, @NotNull ItemClickCallback callback) {
        this.item = item;
        this.callback = callback;
    }

    @Override
    public ItemStack getItemStack() {
        return this.item;
    }

    public void setItemStack(@NotNull ItemStack itemStack) {
        this.item = itemStack;
    }

    @Override
    public ClickCallback getGuiCallback() {
        return this.callback;
    }

    @Override
    public ItemStack getItemStackForDisplay(@NotNull GuiInterface gui) {
        return this.item.copy();
    }
}
