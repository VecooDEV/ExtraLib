package com.vecoo.extralib.ui.api.elements;

import com.vecoo.extralib.ExtraLib;
import com.vecoo.extralib.ui.api.gui.GuiInterface;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class AnimatedGuiElement implements GuiElementInterface {
    protected ItemStack[] items;
    protected int frame = 0;
    protected int tick = 0;
    protected int lastTick = -1;
    protected final int interval;
    protected final boolean random;
    protected final ClickCallback callback;

    public AnimatedGuiElement(@NotNull ItemStack[] items, int interval, boolean random, @NotNull ClickCallback callback) {
        this.items = items;
        this.interval = interval;
        this.random = random;
        this.callback = callback;
    }

    public AnimatedGuiElement(@NotNull ItemStack[] items, int interval, boolean random, @NotNull ItemClickCallback callback) {
        this.items = items;
        this.interval = interval;
        this.random = random;
        this.callback = callback;
    }

    public void setItemStacks(@NotNull ItemStack[] itemStacks) {
        this.items = itemStacks;
    }

    @Override
    public ItemStack getItemStack() {
        return this.items[this.frame];
    }

    @Override
    public ClickCallback getGuiCallback() {
        return this.callback;
    }

    @Override
    public ItemStack getItemStackForDisplay(@NotNull GuiInterface gui) {
        int copyFrame = this.frame;
        int tickCount = ExtraLib.getInstance().getServer().getTickCount();

        if (this.lastTick != tickCount) {
            this.tick += 1;
            this.lastTick = tickCount;
        }

        if (this.tick >= this.interval) {
            this.tick = 0;
            this.frame += 1;

            if (this.frame >= this.items.length) {
                this.frame = 0;
            }

            if (this.random) {
                this.frame = (int) (Math.random() * this.items.length);
            }
        }

        return this.items[copyFrame].copy();
    }
}
