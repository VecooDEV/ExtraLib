package com.vecoo.extralib.ui.api.elements;

import com.vecoo.extralib.ui.api.ClickTypes;
import com.vecoo.extralib.ui.api.gui.GuiInterface;
import com.vecoo.extralib.ui.api.gui.SlotGuiInterface;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface GuiElementInterface {
    ClickCallback EMPTY_CALLBACK = (index, type, action, gui) -> {
    };

    ItemStack getItemStack();

    default ClickCallback getGuiCallback() {
        return EMPTY_CALLBACK;
    }

    default ItemStack getItemStackForDisplay(@NotNull GuiInterface gui) {
        return this.getItemStack().copy();
    }

    default void onAdded(@NotNull SlotGuiInterface gui) {
    }

    default void onRemoved(@NotNull SlotGuiInterface gui) {

    }

    @FunctionalInterface
    interface ItemClickCallback extends ClickCallback {
        void click(int index, ClickTypes type, ClickType action);

        default void click(int index, ClickTypes type, ClickType action, SlotGuiInterface gui) {
            this.click(index, type, action);
        }
    }

    @FunctionalInterface
    interface ClickCallback {
        void click(int index, ClickTypes type, ClickType action, SlotGuiInterface gui);
    }
}
