package com.vecoo.extralib.ui.virtual.inventory;

import com.vecoo.extralib.ui.api.elements.GuiElementInterface;
import com.vecoo.extralib.ui.api.gui.SlotGuiInterface;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class VirtualSlot extends Slot {
    @NotNull
    private final SlotGuiInterface gui;

    public VirtualSlot(@NotNull SlotGuiInterface gui, int index, int x, int y) {
        super(VirtualInventory.INSTANCE, index, x, y);
        this.gui = gui;
    }

    @Override
    @NotNull
    public ItemStack remove(int amount) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean mayPickup(@NotNull Player Player) {
        return false;
    }

    @Override
    public boolean allowModification(@NotNull Player player) {
        return false;
    }

    @Override
    @NotNull
    public ItemStack safeInsert(@NotNull ItemStack itemStack, int count) {
        return itemStack;
    }

    @Override
    @NotNull
    public Optional<ItemStack> tryRemove(int min, int max, @NotNull Player player) {
        return Optional.empty();
    }

    @Override
    @NotNull
    public ItemStack safeInsert(@NotNull ItemStack itemStack) {
        return itemStack;
    }

    @Override
    @NotNull
    public ItemStack getItem() {
        GuiElementInterface guiElement = this.gui.getSlot(this.getContainerSlot());

        if (guiElement == null) {
            return ItemStack.EMPTY;
        }

        return guiElement.getItemStackForDisplay(this.gui).copy();
    }

    @Override
    public void setByPlayer(@NotNull ItemStack itemStack) {
    }

    @Override
    public void set(@NotNull ItemStack itemStack) {
    }

    @Override
    public boolean hasItem() {
        return !getItem().isEmpty();
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack itemStack) {
        return false;
    }

    @Override
    public void setChanged() {
    }
}
