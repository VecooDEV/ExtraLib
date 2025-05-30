package com.vecoo.extralib.chat;

import com.vecoo.extralib.ExtraLib;
import net.minecraft.network.chat.*;
import net.minecraft.server.MinecraftServer;

public class UtilChat {
    public static MutableComponent formatMessage(String message) {
        return Component.literal(message.replace("&", "\u00a7"));
    }

    public static Component clickableMessageCommand(String message, String command) {
        return Component.literal(formatMessage(message).getString()).setStyle(Style.EMPTY.withClickEvent(
                new ClickEvent(ClickEvent.Action.RUN_COMMAND, command)));
    }

    public static MutableComponent clickableMessageCommand(MutableComponent message, String command) {
        return message.copy().setStyle(Style.EMPTY.withClickEvent(
                new ClickEvent(ClickEvent.Action.RUN_COMMAND, command)));
    }

    public static Component clickableMessageURL(String message, String url) {
        return Component.literal(formatMessage(message).getString()).setStyle(Style.EMPTY.withClickEvent(
                new ClickEvent(ClickEvent.Action.OPEN_URL, url)));
    }

    public static MutableComponent clickableMessageURL(MutableComponent message, String url) {
        return message.copy().setStyle(Style.EMPTY.withClickEvent(
                new ClickEvent(ClickEvent.Action.OPEN_URL, url)));
    }

    public static Component hoverMessageText(String message, String text) {
        return Component.literal(formatMessage(message).getString()).setStyle(Style.EMPTY.withHoverEvent(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, formatMessage(text))));
    }

    public static MutableComponent hoverMessageText(MutableComponent message, String text) {
        return message.copy().setStyle(Style.EMPTY.withHoverEvent(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, formatMessage(text))));
    }

    @Deprecated
    public static void broadcast(String message, MinecraftServer server) {
        server.getPlayerList().broadcastSystemMessage(formatMessage(message), false);
    }

    public static void broadcast(String message) {
        ExtraLib.getInstance().getServer().getPlayerList().broadcastSystemMessage(formatMessage(message), false);
    }

    @Deprecated
    public static void clickableBroadcastCommand(String message, String command, MinecraftServer server) {
        server.getPlayerList().broadcastSystemMessage(clickableMessageCommand(message, command), false);
    }

    public static void clickableBroadcastCommand(String message, String command) {
        ExtraLib.getInstance().getServer().getPlayerList().broadcastSystemMessage(clickableMessageCommand(message, command), false);
    }
}