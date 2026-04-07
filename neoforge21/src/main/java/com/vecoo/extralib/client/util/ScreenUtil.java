package com.vecoo.extralib.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

public class ScreenUtil {
    /**
     * Clears the OpenGL depth buffer to reset the depth-testing state.
     * <p>
     * This is typically called before rendering custom overlays or GUI elements
     * that need to appear on top of everything else, or when switching between
     * different rendering passes.
     * <p>
     * Note: The {@code Minecraft.ON_OSX} flag is passed to ensure compatibility
     * with specific hardware/driver requirements on macOS systems.
     * * @see RenderSystem#clear(int, boolean)
     */
    public static void renderClear() {
        RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, Minecraft.ON_OSX);
    }
}
