package com.vecoo.extralib.util;

import com.vecoo.extralib.ExtraLib;
import net.minecraft.util.Util;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.jetbrains.annotations.NotNull;

public final class ChatUtil {
    private ChatUtil() {
    }

    /**
     * Broadcasts a formatted message to all online players.
     *
     * @param message the component message to broadcast
     */
    public static void broadcast(@NotNull IFormattableTextComponent message) {
        ExtraLib.getInstance().getServer().getPlayerList().broadcastMessage(message, ChatType.CHAT, Util.NIL_UUID);
    }

    /**
     * Broadcasts a formatted message to all online players.
     *
     * @param message the message to broadcast
     */
    public static void broadcast(@NotNull String message) {
        broadcast(TextUtil.formatMessage(message));
    }

    /**
     * Broadcasts a clickable command message to all online players.
     *
     * @param message the message text
     * @param command the command to execute when clicked
     */
    public static void clickableBroadcastCommand(@NotNull String message, @NotNull String command) {
        ExtraLib.getInstance().getServer().getPlayerList().broadcastMessage(TextUtil.clickableMessageCommand(message, command), ChatType.CHAT, Util.NIL_UUID);
    }
}