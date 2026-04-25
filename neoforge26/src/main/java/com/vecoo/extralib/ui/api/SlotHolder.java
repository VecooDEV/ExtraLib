package com.vecoo.extralib.ui.api;

import com.vecoo.extralib.ui.api.elements.GuiElement;
import com.vecoo.extralib.ui.api.elements.GuiElementBuilderInterface;
import com.vecoo.extralib.ui.api.elements.GuiElementInterface;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings({"unused"})
public interface SlotHolder {
    int getHeight();

    int getWidth();

    void setSlot(int index, @NotNull GuiElementInterface element);

    default void addSlot(@NotNull GuiElementInterface element) {
        this.setSlot(this.getFirstEmptySlot(), element);
    }

    default void setSlot(int index, @NotNull ItemStack itemStack) {
        this.setSlot(index, new GuiElement(itemStack, GuiElementInterface.EMPTY_CALLBACK));
    }

    default void addSlot(@NotNull ItemStack itemStack) {
        this.setSlot(this.getFirstEmptySlot(), itemStack);
    }

    default void setSlot(int index, @NotNull GuiElementBuilderInterface<?> element) {
        this.setSlot(index, element.build());
    }

    default void addSlot(@NotNull GuiElementBuilderInterface<?> element) {
        this.setSlot(this.getFirstEmptySlot(), element.build());
    }

    default void setSlot(int index, @NotNull ItemStack itemStack, @NotNull GuiElement.ClickCallback callback) {
        this.setSlot(index, new GuiElement(itemStack, callback));
    }

    default void setSlot(int index, @NotNull ItemStack itemStack, @NotNull GuiElementInterface.ItemClickCallback callback) {
        this.setSlot(index, new GuiElement(itemStack, callback));
    }

    default void addSlot(@NotNull ItemStack itemStack, @NotNull GuiElement.ClickCallback callback) {
        this.setSlot(this.getFirstEmptySlot(), new GuiElement(itemStack, callback));
    }

    default void addSlot(@NotNull ItemStack itemStack, @NotNull GuiElementInterface.ItemClickCallback callback) {
        this.setSlot(this.getFirstEmptySlot(), new GuiElement(itemStack, callback));
    }

    void setSlotRedirect(int index, Slot slot);

    default void addSlotRedirect(@NotNull Slot slot) {
        this.setSlotRedirect(this.getFirstEmptySlot(), slot);
    }

    int getFirstEmptySlot();

    void clearSlot(int index);

    boolean isIncludingPlayer();

    int getVirtualSize();

    int getSize();

    @Nullable
    GuiElementInterface getSlot(int index);

    @Nullable
    Slot getSlotRedirect(int index);

    boolean isRedirectingSlots();
}
