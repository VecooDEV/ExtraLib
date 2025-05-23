package com.vecoo.extralib.chat;

import net.minecraft.network.chat.*;
import net.minecraft.server.MinecraftServer;

public class UtilChat {
    public static MutableComponent formatMessage(String message) {
        return Component.literal(message.replace("&", "\u00a7"));
    }

    public static MutableComponent clickableMessageCommand(String message, String command) {
        return formatMessage(message).setStyle(Style.EMPTY.withClickEvent(
                new ClickEvent(ClickEvent.Action.RUN_COMMAND, command)));
    }

    public static MutableComponent clickableMessageURL(String message, String url) {
        return formatMessage(message).setStyle(Style.EMPTY.withClickEvent(
                new ClickEvent(ClickEvent.Action.OPEN_URL, url)));
    }

    public static MutableComponent hoverMessageText(String message, String text) {
        return formatMessage(message).setStyle(Style.EMPTY.withHoverEvent(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, formatMessage(text))));
    }

    public static MutableComponent clickableHoverMessageCommandText(String message, String command, String hoverText) {
        return formatMessage(message).setStyle(Style.EMPTY.withClickEvent(
                new ClickEvent(ClickEvent.Action.RUN_COMMAND, command))).withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, formatMessage(hoverText))));
    }

    public static void broadcast(String message, MinecraftServer server) {
        server.getPlayerList().broadcastSystemMessage(formatMessage(message), false);
    }

    public static void clickableBroadcastCommand(String message, String command, MinecraftServer server) {
        server.getPlayerList().broadcastSystemMessage(clickableMessageCommand(message, command), false);
    }
}