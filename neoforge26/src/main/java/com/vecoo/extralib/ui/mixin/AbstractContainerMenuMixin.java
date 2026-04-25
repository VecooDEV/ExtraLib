package com.vecoo.extralib.ui.mixin;

import com.vecoo.extralib.ui.virtual.inventory.VirtualInventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerMenu.class)
public abstract class AbstractContainerMenuMixin {
    @Inject(method = "canItemQuickReplace", at = @At("HEAD"), cancellable = true)
    private static void canItemQuickReplace(Slot slot, ItemStack itemStack, boolean allowOverflow, CallbackInfoReturnable<Boolean> cir) {
        if (slot != null && slot.container instanceof VirtualInventory) {
            cir.setReturnValue(false);
        }
    }
}