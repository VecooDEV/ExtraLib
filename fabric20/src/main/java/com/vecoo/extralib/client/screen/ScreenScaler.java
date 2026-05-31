package com.vecoo.extralib.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class for consistent UI scaling across different screen resolutions and GUI scales.
 * <p>
 * This helper ensures that custom GUI elements maintain a relative size based on a target
 * reference resolution, preventing the UI from appearing too small on high-resolution displays
 * or too large on small windows.
 */
public final class ScreenScaler {
    private static final float GUI_BASE_WIDTH = 460.0F;
    private static final float GUI_BASE_HEIGHT = 250.0F;
    private static final float TARGET_SCALE = 5.0F;

    private ScreenScaler() {
    }

    /**
     * Applies a calculated scale factor to the graphics matrix and executes the provided action.
     * <p>
     * The method calculates a factor based on the current window scale and dimensions,
     * centers the transformation, and automatically adjusts mouse coordinates so that
     * button clicks and hover effects remain accurate within the scaled coordinate space.
     *
     * @param guiGraphics the current graphics context.
     * @param width       the current screen/window width.
     * @param height      the current screen/window height.
     * @param mouseX      the raw X coordinate of the mouse.
     * @param mouseY      the raw Y coordinate of the mouse.
     * @param action      the functional interface containing the rendering/logic to be scaled.
     */
    public static void scale(@NotNull GuiGraphics guiGraphics, int width, int height, int mouseX, int mouseY, @NotNull ScaleAction action) {
        double currentScale = Minecraft.getInstance().getWindow().getGuiScale();
        float scaleFactor = TARGET_SCALE / (float) currentScale;
        float centerX = width / 2.0F;
        float centerY = height / 2.0F;

        scaleFactor = Math.min(scaleFactor, Math.min((float) width / GUI_BASE_WIDTH, (float) height / GUI_BASE_HEIGHT));

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(centerX, centerY, 0.0F);
        guiGraphics.pose().scale(scaleFactor, scaleFactor, 1.0F);
        guiGraphics.pose().translate(-centerX, -centerY, 0.0F);
        action.scale((int) (centerX + (mouseX - centerX) / scaleFactor), (int) (centerY + (mouseY - centerY) / scaleFactor));
        guiGraphics.pose().popPose();
    }
}