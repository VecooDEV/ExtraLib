package com.vecoo.extralib.ui.api.gui;

import com.vecoo.extralib.ui.api.GuiHelpers;
import com.vecoo.extralib.ui.api.SlotHolder;
import com.vecoo.extralib.ui.api.elements.GuiElement;
import com.vecoo.extralib.ui.api.elements.GuiElementBuilderInterface;
import com.vecoo.extralib.ui.api.elements.GuiElementInterface;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SimpleGuiBuilder implements SlotHolder {
    private final int size;
    private final int width;
    private final int height;
    private final MenuType<?> type;
    private final GuiElementInterface[] elements;
    private final Slot[] slotRedirects;
    private final boolean includePlayer;
    private final int sizeCont;
    private boolean lockPlayerInventory = false;
    private boolean hasRedirects = false;
    private Component title = null;

    public SimpleGuiBuilder(@NotNull MenuType<?> type, boolean manipulatePlayerSlots) {
        this.height = GuiHelpers.getHeight(type);
        this.width = GuiHelpers.getWidth(type);

        this.type = type;

        int tmp = manipulatePlayerSlots ? 36 : 0;
        this.size = this.width * this.height + tmp;
        this.sizeCont = this.width * this.height;
        this.elements = new GuiElementInterface[this.size];
        this.slotRedirects = new Slot[this.size];

        this.includePlayer = manipulatePlayerSlots;
    }

    public SimpleGui build(@NotNull ServerPlayer player) {
        SimpleGui gui = new SimpleGui(this.type, player, this.includePlayer);
        gui.setTitle(this.title);
        gui.setLockPlayerInventory(true);

        int pos = 0;

        for (GuiElementInterface element : this.elements) {
            if (element != null) {
                gui.setSlot(pos, element);
            }
            pos++;
        }

        pos = 0;

        for (Slot slot : this.slotRedirects) {
            if (slot != null) {
                gui.setSlotRedirect(pos, slot);
            }
            pos++;
        }

        return gui;
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    public void setSlot(int index, @NotNull GuiElementInterface element) {
        this.elements[index] = element;
    }

    public void addSlot(@NotNull GuiElementInterface element) {
        this.setSlot(this.getFirstEmptySlot(), element);
    }

    public void setSlot(int index, @NotNull ItemStack itemStack) {
        this.setSlot(index, new GuiElement(itemStack, GuiElementInterface.EMPTY_CALLBACK));
    }

    public void addSlot(@NotNull ItemStack itemStack) {
        this.setSlot(this.getFirstEmptySlot(), itemStack);
    }

    public void setSlot(int index, @NotNull GuiElementBuilderInterface<?> element) {
        this.setSlot(index, element.build());
    }

    public void addSlot(@NotNull GuiElementBuilderInterface<?> element) {
        this.setSlot(this.getFirstEmptySlot(), element.build());
    }

    public void setSlot(int index, @NotNull ItemStack itemStack, @NotNull GuiElement.ClickCallback callback) {
        this.setSlot(index, new GuiElement(itemStack, callback));
    }

    public void setSlot(int index, @NotNull ItemStack itemStack, @NotNull GuiElementInterface.ItemClickCallback callback) {
        this.setSlot(index, new GuiElement(itemStack, callback));
    }

    public void addSlot(@NotNull ItemStack itemStack, @NotNull GuiElement.ClickCallback callback) {
        this.setSlot(this.getFirstEmptySlot(), new GuiElement(itemStack, callback));
    }

    public void addSlot(@NotNull ItemStack itemStack, @NotNull GuiElementInterface.ItemClickCallback callback) {
        this.setSlot(this.getFirstEmptySlot(), new GuiElement(itemStack, callback));
    }

    public void setSlotRedirect(int index, @NotNull Slot slot) {
        this.elements[index] = null;
        this.slotRedirects[index] = slot;
        this.hasRedirects = true;
    }

    public void addSlotRedirect(@NotNull Slot slot) {
        this.setSlotRedirect(this.getFirstEmptySlot(), slot);
    }

    public int getFirstEmptySlot() {
        for (int i = 0; i < this.elements.length; i++) {
            if (this.elements[i] == null && this.slotRedirects[i] == null) {
                return i;
            }
        }
        return -1;
    }

    public void clearSlot(int index) {
        this.elements[index] = null;
        this.slotRedirects[index] = null;
    }

    public boolean isIncludingPlayer() {
        return this.includePlayer;
    }

    public int getVirtualSize() {
        return this.sizeCont;
    }

    @Nullable
    public GuiElementInterface getSlot(int index) {
        if (index >= 0 && index < this.size) {
            return this.elements[index];
        }

        return null;
    }

    @Nullable
    public Slot getSlotRedirect(int index) {
        if (index >= 0 && index < this.size) {
            return this.slotRedirects[index];
        }

        return null;
    }

    public boolean isRedirectingSlots() {
        return this.hasRedirects;
    }

    @NotNull
    public Component getTitle() {
        return this.title;
    }

    public void setTitle(@NotNull Component title) {
        this.title = title;
    }

    @NotNull
    public MenuType<?> getType() {
        return this.type;
    }

    @Override
    public int getSize() {
        return this.size;
    }

    public boolean getLockPlayerInventory() {
        return this.lockPlayerInventory || this.includePlayer;
    }

    public void setLockPlayerInventory(boolean value) {
        this.lockPlayerInventory = value;
    }
}
