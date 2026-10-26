package com.vecoo.extralib.util;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

public final class ChatUtil {
    private ChatUtil() {
    }

    /**
     * Broadcasts a formatted message to all online players.
     *
     * @param message the component message to broadcast
     */
    public static void broadcast(@NotNull Component message) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

        if (server == null) {
            return;
        }

        server.getPlayerList().broadcastSystemMessage(message, false);
    }

    /**
     * Broadcasts a formatted message to all online players.
     *
     * @param message the message to broadcast
     */
    public static void broadcast(@NotNull String message) {
        broadcast(TextUtil.formatMessage(message));
    }

    public static void clickableBroadcastCommand(@NotNull Component message, @NotNull String command) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

        if (server == null) {
            return;
        }

        server.getPlayerList().broadcastSystemMessage(TextUtil.clickableMessageCommand(message, command), false);
    }

    public static void hoverBroadcastCommand(@NotNull Component message, @NotNull String text) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

        if (server == null) {
            return;
        }

        server.getPlayerList().broadcastSystemMessage(TextUtil.hoverMessageText(message, text), false);
    }

    /**
     * Broadcasts a clickable command message to all online players.
     *
     * @param message the message text
     * @param command the command to execute when clicked
     */
    public static void clickableBroadcastCommand(@NotNull String message, @NotNull String command) {
        clickableBroadcastCommand(TextUtil.formatMessage(message), command);
    }

    public static void hoverBroadcastCommand(@NotNull String message, @NotNull String text) {
        hoverBroadcastCommand(TextUtil.formatMessage(message), text);
    }
}
