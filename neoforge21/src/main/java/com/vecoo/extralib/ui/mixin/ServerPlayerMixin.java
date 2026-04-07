package com.vecoo.extralib.ui.mixin;

import com.mojang.authlib.GameProfile;
import com.vecoo.extralib.ui.impl.PlayerExtensions;
import com.vecoo.extralib.ui.virtual.ScreenHandlerFactory;
import com.vecoo.extralib.ui.virtual.VirtualScreenHandlerInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.OptionalInt;
import java.util.function.Consumer;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player implements PlayerExtensions {
    @Shadow
    public abstract void doCloseContainer();

    @Unique
    private boolean extraLib$ignoreNext = false;

    public ServerPlayerMixin(ServerLevel world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(
            method = "openMenu(Lnet/minecraft/world/MenuProvider;Ljava/util/function/Consumer;)Ljava/util/OptionalInt;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;closeContainer()V",
                    shift = At.Shift.BEFORE)
    )
    private void openMenu(MenuProvider factory, @Nullable Consumer<RegistryFriendlyByteBuf> extraDataWriter, CallbackInfoReturnable<OptionalInt> cir) {
        if (factory instanceof ScreenHandlerFactory<?> sguiScreenHandlerFactory && !sguiScreenHandlerFactory.gui().resetMousePosition()) {
            this.extraLib$ignoreNext = true;
        }
    }

    @Inject(method = "closeContainer", at = @At("HEAD"), cancellable = true)
    private void closeContainer(CallbackInfo ci) {
        if (this.extraLib$ignoreNext) {
            this.extraLib$ignoreNext = false;
            this.doCloseContainer();
            ci.cancel();
        }
    }

    @Inject(method = "die", at = @At("TAIL"))
    private void die(DamageSource source, CallbackInfo ci) {
        if (this.containerMenu instanceof VirtualScreenHandlerInterface handler) {
            handler.getGui().close(true);
        }
    }

    @Override
    public void extraLib$ignoreNextClose() {
        this.extraLib$ignoreNext = true;
    }
}
