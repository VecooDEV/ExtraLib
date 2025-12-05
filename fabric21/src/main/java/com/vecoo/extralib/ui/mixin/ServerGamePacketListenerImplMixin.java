package com.vecoo.extralib.ui.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.vecoo.extralib.ui.api.ClickTypes;
import com.vecoo.extralib.ui.api.GuiHelpers;
import com.vecoo.extralib.ui.api.gui.SignGui;
import com.vecoo.extralib.ui.api.gui.SimpleGui;
import com.vecoo.extralib.ui.api.gui.SlotGuiInterface;
import com.vecoo.extralib.ui.virtual.FakeScreenHandler;
import com.vecoo.extralib.ui.virtual.VirtualScreenHandlerInterface;
import com.vecoo.extralib.ui.virtual.inventory.VirtualScreenHandler;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.FilteredText;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin extends ServerCommonPacketListenerImpl {
    @Shadow
    public ServerPlayer player;
    @Unique
    private AbstractContainerMenu previousMenu = null;

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

                if (ignore && !handler.getGui().getLockPlayerInventory() && (slot >= handler.getGui().getSize() || slot < 0 || handler.getGui().getSlotRedirect(slot) != null)) {
                    return;
                }

                this.player.containerMenu.suppressRemoteUpdates();
                boolean bl = packet.getStateId() != this.player.containerMenu.getStateId();

                for (var entry : Int2ObjectMaps.fastIterable(packet.getChangedSlots())) {
                    this.player.containerMenu.setRemoteSlotNoCopy(entry.getIntKey(), entry.getValue());
                }

                this.player.containerMenu.setRemoteCarried(packet.getCarriedItem());

                boolean allow = gui.click(slot, type, packet.getClickType());

                this.player.containerMenu.resumeRemoteUpdates();
                if (allow) {
                    if (bl) {
                        this.player.containerMenu.broadcastFullState();
                    } else {
                        this.player.containerMenu.broadcastChanges();
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
    private boolean canSpectatorClickSlot(boolean isSpectator) {
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
    private void sgui$storeScreenHandler(ServerboundContainerClosePacket packet, CallbackInfo info) {
        if (this.player.containerMenu instanceof VirtualScreenHandlerInterface handler) {
            if (handler.getGui().canPlayerClose()) {
                this.previousMenu = this.player.containerMenu;
            } else {
                AbstractContainerMenu screenHandler = this.player.containerMenu;
                try {
                    if (screenHandler.getType() != null) {
                        this.send(new ClientboundOpenScreenPacket(screenHandler.containerId, screenHandler.getType(), handler.getGui().getTitle()));
                        screenHandler.sendAllDataToRemote();
                    }
                } catch (Throwable ignored) {

                }
                info.cancel();
            }

        }
    }

    @Inject(method = "handleContainerClose", at = @At("TAIL"))
    private void sgui$executeClosing(ServerboundContainerClosePacket packet, CallbackInfo info) {
        try {
            if (this.previousMenu != null) {
                if (this.previousMenu instanceof VirtualScreenHandlerInterface screenHandler) {
                    screenHandler.getGui().close(true);
                }
            }
        } catch (Throwable e) {
            if (this.previousMenu instanceof VirtualScreenHandlerInterface screenHandler) {
                screenHandler.getGui().handleException(e);
            } else {
                e.printStackTrace();
            }
        }

        this.previousMenu = null;
    }

    @Inject(
            method = "handlePlaceRecipe",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;resetLastActionTime()V",
                    shift = At.Shift.BEFORE)
    )
    private void sgui$catchRecipeRequests(ServerboundPlaceRecipePacket packet, CallbackInfo ci) {
        if (this.player.containerMenu instanceof VirtualScreenHandler handler && handler.getGui() instanceof SimpleGui gui) {
            try {
                gui.onCraftRequest(packet.getRecipe(), packet.isShiftDown());
            } catch (Throwable e) {
                handler.getGui().handleException(e);
            }
        }
    }

    @Inject(method = "updateSignText", at = @At("HEAD"), cancellable = true)
    private void sgui$catchSignUpdate(ServerboundSignUpdatePacket packet, List<FilteredText> signComponent, CallbackInfo ci) {
        try {
            if (this.player.containerMenu instanceof FakeScreenHandler fake && fake.getGui() instanceof SignGui gui) {
                for (int i = 0; i < packet.getLines().length; i++) {
                    gui.setLineInternal(i, Component.literal(packet.getLines()[i]));
                }
                gui.close(true);
                ci.cancel();
            }
        } catch (Throwable e) {
            if (this.player.containerMenu instanceof VirtualScreenHandlerInterface handler) {
                handler.getGui().handleException(e);
            } else {
                e.printStackTrace();
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
    private void sgui$cancelCreativeAction(ServerboundSetCreativeModeSlotPacket packet, CallbackInfo ci) {
        if (this.player.containerMenu instanceof VirtualScreenHandlerInterface) {
            ci.cancel();
        }
    }
}
