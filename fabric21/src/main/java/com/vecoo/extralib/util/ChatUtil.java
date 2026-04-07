package com.vecoo.extralib.util;

import com.vecoo.extralib.ExtraLib;
import org.jetbrains.annotations.NotNull;

public final class ChatUtil {
    private ChatUtil() {
    }

    /**
     * Broadcasts a formatted message to all online players.
     *
     * @param message the message to broadcast
     */
    public static void broadcast(@NotNull String message) {
        ExtraLib.getInstance().getServer().getPlayerList().broadcastSystemMessage(TextUtil.formatMessage(message), false);
    }

    /**
     * Broadcasts a clickable command message to all online players.
     *
     * @param message the message text
     * @param command the command to execute when clicked
     */
    public static void clickableBroadcastCommand(@NotNull String message, @NotNull String command) {
        ExtraLib.getInstance().getServer().getPlayerList().broadcastSystemMessage(TextUtil.clickableMessageCommand(message, command), false);
    }
}
