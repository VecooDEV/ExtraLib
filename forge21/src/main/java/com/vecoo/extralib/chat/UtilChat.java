package com.vecoo.extralib.chat;

import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.MinecraftServer;

public class UtilChat {
    public static Component formatMessage(String message) {
        return Component.literal(message.replace("&", "\u00a7"));
    }

    public static Component clickableMessageCommand(String message, String command) {
        return Component.literal(formatMessage(message).getString()).setStyle(Style.EMPTY.withClickEvent(
                new ClickEvent(ClickEvent.Action.RUN_COMMAND, command)));
    }

    public static Component clickableMessageURL(String message, String url) {
        return Component.literal(formatMessage(message).getString()).setStyle(Style.EMPTY.withClickEvent(
                new ClickEvent(ClickEvent.Action.OPEN_URL, url)));
    }

    public static Component hoverMessageText(String message, String text) {
        return Component.literal(formatMessage(message).getString()).setStyle(Style.EMPTY.withHoverEvent(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, formatMessage(text))));
    }

    public static Component clickableHoverMessageCommandText(String message, String command, String text) {
        return Component.literal(formatMessage(message).getString()).setStyle(Style.EMPTY.withClickEvent(
                new ClickEvent(ClickEvent.Action.RUN_COMMAND, command))).withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, formatMessage(text))));
    }

    public static void broadcast(String message, MinecraftServer server) {
        server.getPlayerList().broadcastSystemMessage(formatMessage(message), false);
    }

    public static void clickableBroadcastCommand(String message, String command, MinecraftServer server) {
        server.getPlayerList().broadcastSystemMessage(clickableMessageCommand(message, command), false);
    }

    public static void clickableBroadcastURL(String message, String url, MinecraftServer server) {
        server.getPlayerList().broadcastSystemMessage(clickableMessageURL(message, url), false);
    }
}