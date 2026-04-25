package com.vecoo.extralib.ui.api.gui;

import com.vecoo.extralib.ui.api.GuiHelpers;
import com.vecoo.extralib.ui.api.ScreenProperty;
import com.vecoo.extralib.ui.api.elements.GuiElementInterface;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;

public abstract class BaseSlotGui implements SlotGuiInterface {
    protected final ServerPlayer player;
    protected final GuiElementInterface[] elements;
    protected final Slot[] slotRedirects;
    protected boolean autoUpdate = true;
    protected boolean reOpen = false;
    protected final int size;
    protected final IntList properties = new IntArrayList();

    public BaseSlotGui(@NotNull ServerPlayer player, int size) {
        this.player = player;
        this.elements = new GuiElementInterface[size];
        this.slotRedirects = new Slot[size];
        this.size = size;
    }

    @Override
    public void setSlot(int index, @NotNull GuiElementInterface element) {
        if (this.elements[index] != null) {
            this.elements[index].onRemoved(this);
        }

        this.elements[index] = element;
        this.slotRedirects[index] = null;
        element.onAdded(this);
    }

    @Override
    public void setSlotRedirect(int index, @NotNull Slot slot) {
        if (this.elements[index] != null) {
            this.elements[index].onRemoved(this);
            this.elements[index] = null;
        }

        this.slotRedirects[index] = slot;
    }

    @Override
    public int getFirstEmptySlot() {
        for (int i = 0; i < this.elements.length; i++) {
            if (this.elements[i] == null && this.slotRedirects[i] == null) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public void clearSlot(int index) {
        if (this.elements[index] != null) {
            this.elements[index].onRemoved(this);
            this.elements[index] = null;
        }

        this.slotRedirects[index] = null;
    }

    @Override
    public GuiElementInterface getSlot(int index) {
        if (index >= 0 && index < this.size) {
            return this.elements[index];
        }

        return null;
    }

    @Override
    public Slot getSlotRedirect(int index) {
        if (index >= 0 && index < this.size) {
            return this.slotRedirects[index];
        }

        return null;
    }

    @Override
    public boolean isOpen() {
        return GuiHelpers.getCurrentGui(this.player) == this;
    }

    @Override
    public boolean getAutoUpdate() {
        return this.autoUpdate;
    }

    @Override
    public void setAutoUpdate(boolean value) {
        this.autoUpdate = value;
    }

    @Override
    public int getSize() {
        return this.size;
    }

    @Override
    public ServerPlayer getPlayer() {
        return this.player;
    }

    @Override
    public void sendProperty(@NotNull ScreenProperty property, int value) {
        SlotGuiInterface.super.sendProperty(property, value);

        while (this.properties.size() < property.id()) {
            this.properties.add(0);
        }

        this.properties.set(property.id(), value);
    }

    @Override
    public void sendRawProperty(int id, int value) {
        SlotGuiInterface.super.sendRawProperty(id, value);

        while (this.properties.size() < id) {
            this.properties.add(0);
        }

        this.properties.set(id, value);
    }
}
