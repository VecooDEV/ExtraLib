package com.vecoo.extralib.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import net.minecraft.client.Minecraft;

public class ScreenUtil {
    /** Clears the main render target's depth buffer before rendering an overlay. */
    public static void renderClear() {
        GpuTexture depth = Minecraft.getInstance().getMainRenderTarget().getDepthTexture();

        if (depth != null) {
            RenderSystem.getDevice().createCommandEncoder().clearDepthTexture(depth, 1.0);
        }
    }
}
