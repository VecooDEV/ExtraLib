package com.vecoo.extralib.ui.virtual;

import com.vecoo.extralib.ui.api.gui.GuiInterface;
import com.vecoo.extralib.ui.api.gui.SlotGuiInterface;
import com.vecoo.extralib.ui.virtual.inventory.VirtualScreenHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuConstructor;
import org.jetbrains.annotations.NotNull;

public record SguiScreenHandlerFactory<T extends GuiInterface>(T gui, MenuConstructor factory) implements MenuProvider {
    @Override
    @NotNull
    public Component getDisplayName() {
        Component component = this.gui.getTitle();

        if (component == null) {
            component = Component.empty();
        }

        return component;
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, @NotNull Inventory playerInventory, @NotNull Player player) {
        return factory.createMenu(syncId, playerInventory, player);
    }

    public static <T extends SlotGuiInterface> SguiScreenHandlerFactory<T> ofDefault(T gui) {
        return new SguiScreenHandlerFactory<>(gui, ((syncId, inv, player) -> new VirtualScreenHandler(gui.getType(), syncId, gui, player)));
    }
}
