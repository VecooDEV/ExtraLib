package com.vecoo.extralib.ui.virtual;

import com.vecoo.extralib.ui.api.gui.GuiInterface;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class FakeScreenHandler extends AbstractContainerMenu implements VirtualScreenHandlerInterface {
    private final GuiInterface gui;

    public FakeScreenHandler(@NotNull GuiInterface gui) {
        super(null, -1);
        this.gui = gui;
    }

    @Override
    public GuiInterface getGui() {
        return this.gui;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public void broadcastChanges() {
        try {
            this.gui.onTick();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.broadcastChanges();
    }

    @Override
    public ItemStack quickMoveStack(@NotNull Player player, int index) {
        return ItemStack.EMPTY;
    }
}
