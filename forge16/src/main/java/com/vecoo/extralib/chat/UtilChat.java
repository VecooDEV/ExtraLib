package com.vecoo.extralib.chat;

import com.vecoo.extralib.ExtraLib;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public class UtilChat {
    public static StringTextComponent formatMessage(String message) {
        return new StringTextComponent(message.replace("&", "\u00a7"));
    }

    public static StringTextComponent clickableMessageCommand(String message, String command) {
        return (StringTextComponent) formatMessage(message).setStyle(Style.EMPTY.withClickEvent(
                new ClickEvent(ClickEvent.Action.RUN_COMMAND, command)));
    }

    public static ITextComponent clickableMessageCommand(ITextComponent message, String command) {
        return message.copy().setStyle(Style.EMPTY.withClickEvent(
                new ClickEvent(ClickEvent.Action.RUN_COMMAND, command)));
    }

    public static StringTextComponent clickableMessageURL(String message, String url) {
        return (StringTextComponent) formatMessage(message).setStyle(Style.EMPTY.withClickEvent(
                new ClickEvent(ClickEvent.Action.OPEN_URL, url)));
    }

    public static ITextComponent clickableMessageURL(ITextComponent message, String url) {
        return message.copy().setStyle(Style.EMPTY.withClickEvent(
                new ClickEvent(ClickEvent.Action.OPEN_URL, url)));
    }

    public static StringTextComponent hoverMessageText(String message, String text) {
        return (StringTextComponent) formatMessage(message).setStyle(Style.EMPTY.withHoverEvent(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, formatMessage(text))));
    }

    public static ITextComponent hoverMessageText(ITextComponent message, String text) {
        return message.copy().setStyle(Style.EMPTY.withHoverEvent(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, formatMessage(text))));
    }

    @Deprecated
    public static void broadcast(String message, MinecraftServer server) {
        server.getPlayerList().broadcastMessage(formatMessage(message), ChatType.CHAT, Util.NIL_UUID);
    }

    public static void broadcast(String message) {
        ExtraLib.getInstance().getServer().getPlayerList().broadcastMessage(formatMessage(message), ChatType.CHAT, Util.NIL_UUID);
    }

    @Deprecated
    public static void clickableBroadcastCommand(String message, String command, MinecraftServer server) {
        server.getPlayerList().broadcastMessage(clickableMessageCommand(message, command), ChatType.CHAT, Util.NIL_UUID);
    }

    public static void clickableBroadcastCommand(String message, String command) {
        ExtraLib.getInstance().getServer().getPlayerList().broadcastMessage(clickableMessageCommand(message, command), ChatType.CHAT, Util.NIL_UUID);
    }
}