package com.vecoo.extralib.chat;

import com.vecoo.extralib.ExtraLib;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import javax.annotation.Nonnull;

public class UtilChat {
    @Nonnull
    public static TextComponentString formatMessage(@Nonnull String message) {
        return new TextComponentString(message.replace("&", "§"));
    }

    @Nonnull
    public static TextComponentString clickableMessageCommand(@Nonnull String message, @Nonnull String command) {
        return (TextComponentString) formatMessage(message).setStyle(new Style().setClickEvent(
                new ClickEvent(ClickEvent.Action.RUN_COMMAND, command)));
    }

    @Nonnull
    public static ITextComponent clickableMessageCommand(@Nonnull ITextComponent message, @Nonnull String command) {
        return message.createCopy().setStyle(new Style().setClickEvent(
                new ClickEvent(ClickEvent.Action.RUN_COMMAND, command)));
    }

    @Nonnull
    public static TextComponentString clickableMessageURL(@Nonnull String message, @Nonnull String url) {
        return (TextComponentString) formatMessage(message).setStyle(new Style().setClickEvent(
                new ClickEvent(ClickEvent.Action.OPEN_URL, url)));
    }

    @Nonnull
    public static ITextComponent clickableMessageURL(@Nonnull ITextComponent message, @Nonnull String url) {
        return message.createCopy().setStyle(new Style().setClickEvent(
                new ClickEvent(ClickEvent.Action.OPEN_URL, url)));
    }

    @Nonnull
    public static TextComponentString hoverMessageText(@Nonnull String message, @Nonnull String text) {
        return (TextComponentString) formatMessage(message).setStyle(new Style().setHoverEvent(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, formatMessage(text))));
    }

    @Nonnull
    public static TextComponentString hoverMessageText(@Nonnull TextComponentString message, @Nonnull String text) {
        return (TextComponentString) message.createCopy().setStyle(new Style().setHoverEvent(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, formatMessage(text))));
    }

    public static void broadcast(@Nonnull String message) {
        ExtraLib.getInstance().getServer().getPlayerList().sendMessage(formatMessage(message));
    }

    public static void clickableBroadcastCommand(@Nonnull String message, @Nonnull String command) {
        ExtraLib.getInstance().getServer().getPlayerList().sendMessage(clickableMessageCommand(message, command));
    }
}