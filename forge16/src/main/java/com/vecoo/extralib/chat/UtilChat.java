package com.vecoo.extralib.chat;

import com.vecoo.extralib.ExtraLib;
import net.minecraft.util.Util;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import javax.annotation.Nonnull;

public class UtilChat {
    @Nonnull
    public static StringTextComponent formatMessage(@Nonnull String message) {
        return new StringTextComponent(message.replace("&", "§"));
    }

    @Nonnull
    public static StringTextComponent clickableMessageCommand(@Nonnull String message, @Nonnull String command) {
        return (StringTextComponent) formatMessage(message).setStyle(Style.EMPTY.withClickEvent(
                new ClickEvent(ClickEvent.Action.RUN_COMMAND, command)));
    }

    @Nonnull
    public static ITextComponent clickableMessageCommand(@Nonnull ITextComponent message, @Nonnull String command) {
        return message.copy().setStyle(Style.EMPTY.withClickEvent(
                new ClickEvent(ClickEvent.Action.RUN_COMMAND, command)));
    }

    @Nonnull
    public static StringTextComponent clickableMessageURL(@Nonnull String message, @Nonnull String url) {
        return (StringTextComponent) formatMessage(message).setStyle(Style.EMPTY.withClickEvent(
                new ClickEvent(ClickEvent.Action.OPEN_URL, url)));
    }

    @Nonnull
    public static ITextComponent clickableMessageURL(@Nonnull ITextComponent message, @Nonnull String url) {
        return message.copy().setStyle(Style.EMPTY.withClickEvent(
                new ClickEvent(ClickEvent.Action.OPEN_URL, url)));
    }

    @Nonnull
    public static StringTextComponent hoverMessageText(@Nonnull String message, @Nonnull String text) {
        return (StringTextComponent) formatMessage(message).setStyle(Style.EMPTY.withHoverEvent(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, formatMessage(text))));
    }

    @Nonnull
    public static ITextComponent hoverMessageText(@Nonnull ITextComponent message, @Nonnull String text) {
        return message.copy().setStyle(Style.EMPTY.withHoverEvent(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, formatMessage(text))));
    }

    public static void broadcast(@Nonnull String message) {
        ExtraLib.getInstance().getServer().getPlayerList().broadcastMessage(formatMessage(message), ChatType.CHAT, Util.NIL_UUID);
    }

    public static void clickableBroadcastCommand(@Nonnull String message, @Nonnull String command) {
        ExtraLib.getInstance().getServer().getPlayerList().broadcastMessage(clickableMessageCommand(message, command), ChatType.CHAT, Util.NIL_UUID);
    }
}