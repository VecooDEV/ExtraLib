package com.vecoo.extralib.chat;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public class UtilChat {
    public static StringTextComponent formatMessage(String text) {
        return new StringTextComponent(text.replace("&", "\u00a7"));
    }

    public static StringTextComponent clickableMessageCommand(String message, String command) {
        return (StringTextComponent) formatMessage(message).setStyle(Style.EMPTY.withClickEvent(
                new ClickEvent(ClickEvent.Action.RUN_COMMAND, command)));
    }

    public static StringTextComponent clickableMessageURL(String message, String url) {
        return (StringTextComponent) formatMessage(message).setStyle(Style.EMPTY.withClickEvent(
                new ClickEvent(ClickEvent.Action.OPEN_URL, url)));
    }

    public static StringTextComponent hoverMessageText(String message, String text) {
        return (StringTextComponent) formatMessage(message).setStyle(Style.EMPTY.withHoverEvent(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, formatMessage(text))));
    }

    public static StringTextComponent hoverMessageItem(String message, HoverEvent.ItemHover itemHover) {
        return (StringTextComponent) formatMessage(message).setStyle(Style.EMPTY.withHoverEvent(
                new HoverEvent(HoverEvent.Action.SHOW_ITEM, itemHover)));
    }

    public static StringTextComponent clickableHoverMessageCommandText(String message, String command, String text) {
        return (StringTextComponent) formatMessage(message).setStyle(Style.EMPTY.withClickEvent(
                new ClickEvent(ClickEvent.Action.RUN_COMMAND, command))).withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, formatMessage(text))));
    }

    public static void broadcast(String message, MinecraftServer server) {
        server.getPlayerList().broadcastMessage(formatMessage(message), ChatType.CHAT, Util.NIL_UUID);
    }

    public static void clickableBroadcastCommand(String message, String command, MinecraftServer server) {
        server.getPlayerList().broadcastMessage(clickableMessageCommand(message, command), ChatType.CHAT, Util.NIL_UUID);
    }

    public static void clickableBroadcastURL(String message, String url, MinecraftServer server) {
        server.getPlayerList().broadcastMessage(clickableMessageURL(message, url), ChatType.CHAT, Util.NIL_UUID);
    }
}