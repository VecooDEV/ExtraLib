package com.vecoo.extralib.ui.api.gui;

import com.vecoo.extralib.ui.api.ClickTypes;
import com.vecoo.extralib.ui.api.SlotHolder;
import com.vecoo.extralib.ui.api.elements.GuiElementInterface;
import com.vecoo.extralib.ui.virtual.inventory.VirtualScreenHandler;
import com.vecoo.extralib.ui.virtual.inventory.VirtualSlot;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface SlotGuiInterface extends SlotHolder, GuiInterface {
    int getSize();

    boolean getLockPlayerInventory();

    void setLockPlayerInventory(boolean value);

    @ApiStatus.Internal
    default boolean click(int index, @NotNull ClickTypes type, @NotNull ClickType action) {
        GuiElementInterface element = this.getSlot(index);

        if (element != null) {
            element.getGuiCallback().click(index, type, action, this);
        }

        return this.onClick(index, type, action, element);
    }


    default boolean onAnyClick(int index, @NotNull ClickTypes type, @NotNull ClickType action) {
        return true;
    }

    default boolean onClick(int index, @NotNull ClickTypes type, @NotNull ClickType action, @Nullable GuiElementInterface element) {
        return false;
    }

    default boolean canSpectatorsClick() {
        return true;
    }

    default int getHotbarSlotIndex(int slots, int index) {
        return slots + index - 9;
    }

    default int getOffhandSlotIndex() {
        return -1;
    }

    @Nullable
    default Slot getSlotRedirectOrPlayer(int index) {
        if (index < this.getSize()) {
            return this.getSlotRedirect(index);
        }

        if (this.getPlayer().containerMenu instanceof VirtualScreenHandler virtualScreenHandler && virtualScreenHandler.getGui() == this && index < virtualScreenHandler.slots.size()) {
            return virtualScreenHandler.slots.get(index);
        }

        return null;
    }

    default ItemStack quickMoveStack(int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.getSlotRedirectOrPlayer(index);

        if (slot != null && slot.hasItem() && !(slot instanceof VirtualSlot)) {
            ItemStack itemStack2 = slot.getItem();
            itemStack = itemStack2.copy();

            if (index < this.getVirtualSize()) {
                if (!this.insertItem(itemStack2, this.getVirtualSize(), this.getVirtualSize() + 9 * 4, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemStack2, 0, this.getVirtualSize(), false)) {
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        } else if (slot instanceof VirtualSlot) {
            return slot.getItem();
        }

        return itemStack;
    }

    default boolean insertItem(@NotNull ItemStack stack, int startIndex, int endIndex, boolean fromLast) {
        boolean modified = false;
        int i = startIndex;

        if (fromLast) {
            i = endIndex - 1;
        }

        if (stack.isStackable()) {
            while (!stack.isEmpty() && (fromLast ? i >= startIndex : i < endIndex)) {
                Slot slot = this.getSlotRedirectOrPlayer(i);

                if (slot != null && slot.mayPlace(stack)) {
                    ItemStack stackInSlot = slot.getItem();

                    if (!stackInSlot.isEmpty() && ItemStack.isSameItemSameComponents(stack, stackInSlot)) {
                        int totalCount = stackInSlot.getCount() + stack.getCount();
                        int maxSize = slot.getMaxStackSize(stackInSlot);

                        if (totalCount <= maxSize) {
                            stack.setCount(0);
                            stackInSlot.setCount(totalCount);
                            slot.setChanged();
                            modified = true;
                        } else if (stackInSlot.getCount() < maxSize) {
                            stack.shrink(maxSize - stackInSlot.getCount());
                            stackInSlot.setCount(maxSize);
                            slot.setChanged();
                            modified = true;
                        }
                    }
                }

                if (fromLast) {
                    i--;
                } else {
                    i++;
                }
            }
        }

        if (!stack.isEmpty()) {
            if (fromLast) {
                i = endIndex - 1;
            } else {
                i = startIndex;
            }

            while (fromLast ? i >= startIndex : i < endIndex) {
                Slot slot = this.getSlotRedirectOrPlayer(i);

                if (slot != null && slot.mayPlace(stack)) {
                    ItemStack stackInSlot = slot.getItem();

                    if (stackInSlot.isEmpty() && slot.mayPlace(stack)) {
                        int maxSize = slot.getMaxStackSize(stack);

                        slot.set(stack.split(Math.min(stack.getCount(), maxSize)));
                        slot.setChanged();
                        modified = true;
                        break;
                    }
                }

                if (fromLast) {
                    i--;
                } else {
                    i++;
                }
            }
        }

        return modified;
    }

    default boolean insertItem(@NotNull ItemStack stack, @NotNull List<Slot> slots, boolean fromLast) {
        boolean modified = false;

        if (fromLast) {
            slots = slots.reversed();
        }

        if (stack.isStackable()) {
            for (Slot slot : slots) {
                if (stack.isEmpty()) {
                    break;
                }

                if (slot.mayPlace(stack)) {
                    ItemStack stackInSlot = slot.getItem();

                    if (!stackInSlot.isEmpty() && ItemStack.isSameItemSameComponents(stack, stackInSlot)) {
                        int totalCount = stackInSlot.getCount() + stack.getCount();
                        int maxSize = slot.getMaxStackSize(stackInSlot);

                        if (totalCount <= maxSize) {
                            stack.setCount(0);
                            stackInSlot.setCount(totalCount);
                            slot.setChanged();
                            modified = true;
                        } else if (stackInSlot.getCount() < maxSize) {
                            stack.shrink(maxSize - stackInSlot.getCount());
                            stackInSlot.setCount(maxSize);
                            slot.setChanged();
                            modified = true;
                        }
                    }
                }
            }
        }

        if (!stack.isEmpty()) {
            for (var slot : slots) {
                if (slot.mayPlace(stack)) {
                    ItemStack stackInSlot = slot.getItem();

                    if (stackInSlot.isEmpty() && slot.mayPlace(stack)) {
                        int maxSize = slot.getMaxStackSize(stack);

                        slot.set(stack.split(Math.min(stack.getCount(), maxSize)));
                        slot.setChanged();
                        modified = true;
                        break;
                    }
                }
            }
        }

        return modified;
    }
}
