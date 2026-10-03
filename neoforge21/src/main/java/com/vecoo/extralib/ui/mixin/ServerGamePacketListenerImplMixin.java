package com.vecoo.extralib.ui.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.vecoo.extralib.ExtraLib;
import com.vecoo.extralib.ui.api.ClickTypes;
import com.vecoo.extralib.ui.api.GuiHelpers;
import com.vecoo.extralib.ui.api.gui.SimpleGui;
import com.vecoo.extralib.ui.api.gui.SlotGuiInterface;
import com.vecoo.extralib.ui.virtual.VirtualScreenHandlerInterface;
import com.vecoo.extralib.ui.virtual.inventory.VirtualScreenHandler;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin extends ServerCommonPacketListenerImpl {
    @Shadow
    public ServerPlayer player;
    @Unique
    private AbstractContainerMenu extraLib$previousMenu = null;

    public ServerGamePacketListenerImplMixin(MinecraftServer server, Connection connection, CommonListenerCookie clientData) {
        super(server, connection, clientData);
    }

    @Inject(
            method = "handleContainerClick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;resetLastActionTime()V",
                    shift = At.Shift.AFTER
            ),
            cancellable = true
    )

    private void updateClicks(ServerboundContainerClickPacket packet, CallbackInfo ci) {
        if (this.player.containerMenu instanceof VirtualScreenHandler handler) {
            try {
                SlotGuiInterface gui = handler.getGui();

                if (this.player.isSpectator() && !gui.canSpectatorsClick()) {
                    return;
                }

                int slot = packet.getSlotNum();
                int button = packet.getButtonNum();

                ClickTypes type = ClickTypes.toClickType(packet.getClickType(), button, slot);
                boolean ignore = gui.onAnyClick(slot, type, packet.getClickType());

                if (ignore && !gui.getLockPlayerInventory() && (slot >= gui.getSize() || slot < 0 || gui.getSlotRedirect(slot) != null)) {
                    return;
                }

                AbstractContainerMenu containerMenu = this.player.containerMenu;

                boolean stateChanged;
                boolean allow;

                containerMenu.suppressRemoteUpdates();

                try {
                    stateChanged = packet.getStateId() != containerMenu.getStateId();

                    for (var entry : Int2ObjectMaps.fastIterable(packet.getChangedSlots())) {
                        containerMenu.setRemoteSlotNoCopy(entry.getIntKey(), entry.getValue());
                    }

                    containerMenu.setRemoteCarried(packet.getCarriedItem());

                    allow = gui.click(slot, type, packet.getClickType());
                } finally {
                    containerMenu.resumeRemoteUpdates();
                }

                if (allow) {
                    if (stateChanged) {
                        containerMenu.broadcastFullState();
                    } else {
                        containerMenu.broadcastChanges();
                    }
                }
            } catch (Throwable e) {
                handler.getGui().handleException(e);
            }

            ci.cancel();
        }
    }

    @ModifyExpressionValue(
            method = "handleContainerClick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;isSpectator()Z"
            )
    )
    private boolean handleContainerClick(boolean isSpectator) {
        return isSpectator && !(this.player.containerMenu instanceof VirtualScreenHandler handler && handler.getGui().canSpectatorsClick());
    }

    @Inject(method = "handleContainerClick", at = @At("TAIL"))
    private void handleContainerClick(ServerboundContainerClickPacket packet, CallbackInfo ci) {
        if (this.player.containerMenu instanceof VirtualScreenHandler handler) {
            try {
                int slot = packet.getSlotNum();
                int button = packet.getButtonNum();
                ClickTypes type = ClickTypes.toClickType(packet.getClickType(), button, slot);

                if (type == ClickTypes.MOUSE_DOUBLE_CLICK || (type.isDragging && type.value == 2) || type.shift) {
                    GuiHelpers.sendPlayerScreenHandler(this.player);
                }

            } catch (Throwable e) {
                handler.getGui().handleException(e);
            }
        }
    }

    @Inject(
            method = "handleContainerClose",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/server/level/ServerLevel;)V",
                    shift = At.Shift.AFTER
            ),
            cancellable = true
    )
    private void handleContainerCloseServerLevel(ServerboundContainerClosePacket packet, CallbackInfo info) {
        if (this.player.containerMenu instanceof VirtualScreenHandlerInterface handler) {
            if (handler.getGui().canPlayerClose()) {
                this.extraLib$previousMenu = this.player.containerMenu;
            } else {
                AbstractContainerMenu screenHandler = this.player.containerMenu;

                try {
                    if (screenHandler.getType() != null) {
                        send(new ClientboundOpenScreenPacket(screenHandler.containerId, screenHandler.getType(), handler.getGui().getTitle()));
                        screenHandler.sendAllDataToRemote();
                    }
                } catch (Throwable ignored) {
                }

                info.cancel();
            }

        }
    }

    @Inject(method = "handleContainerClose", at = @At("TAIL"))
    private void handleContainerClose(ServerboundContainerClosePacket packet, CallbackInfo info) {
        try {
            if (this.extraLib$previousMenu != null) {
                if (this.extraLib$previousMenu instanceof VirtualScreenHandlerInterface screenHandler) {
                    screenHandler.getGui().close(true);
                }
            }
        } catch (Throwable e) {
            if (this.extraLib$previousMenu instanceof VirtualScreenHandlerInterface screenHandler) {
                screenHandler.getGui().handleException(e);
            } else {
                ExtraLib.getLogger().error(e.getMessage());
            }
        }

        this.extraLib$previousMenu = null;
    }

    @Inject(
            method = "handlePlaceRecipe",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;resetLastActionTime()V",
                    shift = At.Shift.BEFORE)
    )
    private void handlePlaceRecipe(ServerboundPlaceRecipePacket packet, CallbackInfo ci) {
        if (this.player.containerMenu instanceof VirtualScreenHandler handler && handler.getGui() instanceof SimpleGui gui) {
            try {
                gui.onCraftRequest(packet.getRecipe(), packet.isShiftDown());
            } catch (Throwable e) {
                handler.getGui().handleException(e);
            }
        }
    }

    @Inject(
            method = "handleSetCreativeModeSlot",
            at = @At(
                    value = "INVOKE",
                    shift = At.Shift.AFTER,
                    target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/server/level/ServerLevel;)V"),
            cancellable = true
    )
    private void handleSetCreativeModeSlot(ServerboundSetCreativeModeSlotPacket packet, CallbackInfo ci) {
        if (this.player.containerMenu instanceof VirtualScreenHandlerInterface) {
            ci.cancel();
        }
    }
}
