package com.vecoo.extralib.ui.virtual.inventory;

import com.vecoo.extralib.ui.api.GuiHelpers;
import com.vecoo.extralib.ui.api.gui.SlotGuiInterface;
import com.vecoo.extralib.ui.virtual.VirtualScreenHandlerInterface;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VirtualScreenHandler extends AbstractContainerMenu implements VirtualScreenHandlerInterface {
    @NotNull
    private final SlotGuiInterface gui;

    public VirtualScreenHandler(@Nullable MenuType<?> type, int syncId, @NotNull SlotGuiInterface gui, @NotNull Player player) {
        super(type, syncId);
        this.gui = gui;

        setupSlots(player);
    }

    protected void setupSlots(@NotNull Player player) {
        int slots;
        int playerSlots;

        for (slots = 0; slots < this.gui.getVirtualSize(); ++slots) {
            Slot slot = this.gui.getSlotRedirect(slots);

            if (slot != null) {
                addSlot(slot);
            } else {
                addSlot(new VirtualSlot(gui, slots, 0, 0));
            }
        }

        if (gui.isIncludingPlayer()) {
            int size = this.gui.getHeight() * this.gui.getWidth();

            for (slots = 0; slots < 4; ++slots) {
                for (playerSlots = 0; playerSlots < 9; ++playerSlots) {
                    addSlot(new VirtualSlot(this.gui, playerSlots + slots * 9 + size, 0, 0));
                }
            }
        } else {
            Inventory playerInventory = player.getInventory();

            for (slots = 0; slots < 3; ++slots) {
                for (playerSlots = 0; playerSlots < 9; ++playerSlots) {
                    addSlot(new Slot(playerInventory, playerSlots + slots * 9 + 9, 0, 0));
                }
            }

            for (slots = 0; slots < 9; ++slots) {
                addSlot(new Slot(playerInventory, slots, 0, 0));
            }
        }
    }

    @Override
    public void addSlotListener(@NotNull ContainerListener listener) {
        super.addSlotListener(listener);
        this.gui.afterOpen();
    }

    @Override
    public void sendAllDataToRemote() {
        super.sendAllDataToRemote();

        int index = this.getGui().getOffhandSlotIndex();

        ItemStack updated = index >= 0 ? this.getSlot(index).getItem() : ItemStack.EMPTY;
        GuiHelpers.sendSlotUpdate(this.gui.getPlayer(), -2, Inventory.SLOT_OFFHAND, updated, this.getStateId());
    }

    @Override
    @NotNull
    public SlotGuiInterface getGui() {
        return this.gui;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public void setItem(int slot, int i, @NotNull ItemStack itemStack) {
        if (this.gui.getSize() <= slot) {
            this.getSlot(slot).set(itemStack);
        } else {
            this.getSlot(slot).set(ItemStack.EMPTY);
        }
    }

    @Override
    public void broadcastChanges() {
        try {
            this.gui.onTick();
        } catch (Exception e) {
            this.gui.handleException(e);
        }

        super.broadcastChanges();
    }

    @Override
    @NotNull
    public ItemStack quickMoveStack(@NotNull Player player, int index) {
        return this.gui.quickMoveStack(index);
    }

    @Override
    public boolean canDragTo(@NotNull Slot slot) {
        return !(slot instanceof VirtualSlot) && super.canDragTo(slot);
    }

    @Override
    @NotNull
    public Slot addSlot(@NotNull Slot slot) {
        return super.addSlot(slot);
    }

    public void setSlot(int index, Slot slot) {
        this.slots.set(index, slot);
    }

    @Override
    protected boolean moveItemStackTo(@NotNull ItemStack itemStack, int startIndex, int endIndex, boolean fromLast) {
        return this.gui.insertItem(itemStack, startIndex, endIndex, fromLast);
    }
}
