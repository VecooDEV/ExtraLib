package com.vecoo.extralib.ui.api.gui;

import com.mojang.datafixers.util.Pair;
import com.vecoo.extralib.ui.api.GuiHelpers;
import com.vecoo.extralib.ui.api.elements.GuiElement;
import com.vecoo.extralib.ui.api.elements.GuiElementInterface;
import com.vecoo.extralib.ui.virtual.SguiScreenHandlerFactory;
import com.vecoo.extralib.ui.virtual.inventory.VirtualScreenHandler;
import com.vecoo.extralib.ui.virtual.inventory.VirtualSlot;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SimpleGui extends BaseSlotGui {
    public static final Map<UUID, Runnable> pendingOpens = new HashMap<>();

    protected final int width;
    protected final int height;
    protected final MenuType<?> type;
    private final boolean includePlayer;
    private final int sizeCont;
    protected boolean lockPlayerInventory = false;
    protected VirtualScreenHandler screenHandler = null;
    protected int syncId = -1;
    protected boolean hasRedirects = false;
    private Component title = null;

    public SimpleGui(@NotNull MenuType<?> type, @NotNull ServerPlayer player, boolean manipulatePlayerSlots) {
        super(player, GuiHelpers.getHeight(type) * GuiHelpers.getWidth(type) + (manipulatePlayerSlots ? 36 : 0));
        this.height = GuiHelpers.getHeight(type);
        this.width = GuiHelpers.getWidth(type);

        this.type = type;
        this.sizeCont = this.width * this.height;
        this.includePlayer = manipulatePlayerSlots;
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    @Override
    public void setSlot(int index, @NotNull GuiElementInterface element) {
        super.setSlot(index, element);

        if (this.isOpen() && this.autoUpdate) {
            this.screenHandler.setSlot(index, new VirtualSlot(this, index, 0, 0));
        }
    }

    @Override
    public void setSlotRedirect(int index, @NotNull Slot slot) {
        super.setSlotRedirect(index, slot);

        if (this.isOpen() && this.autoUpdate) {
            this.screenHandler.setSlot(index, slot);
        }
    }

    @Override
    public void clearSlot(int index) {
        super.clearSlot(index);

        this.hasRedirects = true;

        if (this.isOpen() && this.autoUpdate) {
            this.screenHandler.setSlot(index, new VirtualSlot(this, index, 0, 0));
        }
    }

    @Override
    public boolean isOpen() {
        return this.screenHandler != null && this.screenHandler == this.player.containerMenu;
    }

    @Override
    public Component getTitle() {
        return this.title;
    }

    @Override
    public void setTitle(@NotNull Component title) {
        this.title = title;

        if (this.isOpen()) {
            this.player.connection.send(new ClientboundOpenScreenPacket(this.syncId, this.type, title));
            this.screenHandler.sendAllDataToRemote();
        }
    }

    @Override
    public boolean getAutoUpdate() {
        return this.autoUpdate;
    }

    @Override
    public void setAutoUpdate(boolean value) {
        this.autoUpdate = value;
    }

    public boolean isIncludingPlayer() {
        return this.includePlayer;
    }

    public int getVirtualSize() {
        return this.sizeCont;
    }

    public boolean isRedirectingSlots() {
        return this.hasRedirects;
    }

    protected boolean sendGui() {
        this.reOpen = true;
        OptionalInt temp = this.player.openMenu(SguiScreenHandlerFactory.ofDefault(this));
        this.reOpen = false;

        if (temp.isPresent()) {
            this.syncId = temp.getAsInt();
            if (this.player.containerMenu instanceof VirtualScreenHandler) {
                this.screenHandler = (VirtualScreenHandler) this.player.containerMenu;
                player.connection.send(new ClientboundSetEquipmentPacket(player.getId(),
                        List.of(Pair.of(EquipmentSlot.OFFHAND, player.getOffhandItem()))));
                return true;
            }
        }

        return false;
    }

    public void onCraftRequest(@NotNull ResourceLocation recipe, boolean shift) {
    }

    @Override
    public MenuType<?> getType() {
        return this.type;
    }

    @Override
    public boolean open() {
        if (this.player.hasDisconnected() || this.isOpen()) {
            return false;
        } else {
            this.beforeOpen();
            this.onOpen();
            this.sendGui();
            return this.isOpen();
        }
    }

    public void safeOpen(@NotNull ServerPlayer player) {
        if (player.containerMenu != player.inventoryMenu) {
            pendingOpens.put(player.getUUID(), this::open);
            return;
        }

        open();
    }

    @Override
    public void openForce() {
        if (!this.player.hasDisconnected()) {
            beforeOpen();
            onOpen();
            sendGui();

            this.player.containerMenu.broadcastChanges();
        }
    }

    @Override
    public void reOpen() {
        if (!this.player.hasDisconnected()) {
            GuiInterface gui = GuiHelpers.getCurrentGui(player);

            if (gui != null) {
                gui.close();
            }

            open();
        }
    }

    @Override
    public void close(boolean screenHandlerIsClosed) {
        if ((this.isOpen() || screenHandlerIsClosed) && !this.reOpen) {
            if (!screenHandlerIsClosed && this.player.containerMenu == this.screenHandler) {
                this.player.closeContainer();
                this.screenHandler = null;
            }

            this.player.containerMenu.broadcastChanges();

            this.onClose();
        } else {
            this.reOpen = false;
        }
    }

    @Override
    public boolean getLockPlayerInventory() {
        return this.lockPlayerInventory || this.includePlayer;
    }

    @Override
    public void setLockPlayerInventory(boolean value) {
        this.lockPlayerInventory = value;
    }

    @Override
    public int getSyncId() {
        return syncId;
    }

    public void setSlot(int index, @NotNull ItemStack itemStack, @NotNull GuiElementInterface.ItemClickCallback callback) {
        this.setSlot(index, new GuiElement(itemStack, callback));
    }

    public void addSlot(@NotNull ItemStack itemStack, @NotNull GuiElementInterface.ItemClickCallback callback) {
        this.addSlot(new GuiElement(itemStack, callback));
    }
}
