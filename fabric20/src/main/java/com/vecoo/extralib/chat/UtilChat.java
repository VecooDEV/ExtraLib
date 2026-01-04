package com.vecoo.extralib.chat;

import com.vecoo.extralib.ExtraLib;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.NotNull;

public final class UtilChat {
    @NotNull
    public static Component formatMessage(@NotNull String message) {
        return Component.literal(message.replace("&", "§"));
    }

    @NotNull
    public static Component clickableMessageCommand(@NotNull String message, @NotNull String command) {
        return Component.literal(formatMessage(message).getString()).setStyle(Style.EMPTY.withClickEvent(
                new ClickEvent(ClickEvent.Action.RUN_COMMAND, command)));
    }

    @NotNull
    public static Component clickableMessageCommand(@NotNull Component message, @NotNull String command) {
        return message.copy().setStyle(Style.EMPTY.withClickEvent(
                new ClickEvent(ClickEvent.Action.RUN_COMMAND, command)));
    }

    @NotNull
    public static Component clickableMessageURL(@NotNull String message, @NotNull String url) {
        return Component.literal(formatMessage(message).getString()).setStyle(Style.EMPTY.withClickEvent(
                new ClickEvent(ClickEvent.Action.OPEN_URL, url)));
    }

    @NotNull
    public static Component clickableMessageURL(@NotNull Component message, @NotNull String url) {
        return message.copy().setStyle(Style.EMPTY.withClickEvent(
                new ClickEvent(ClickEvent.Action.OPEN_URL, url)));
    }

    @NotNull
    public static Component hoverMessageText(@NotNull String message, @NotNull String text) {
        return Component.literal(formatMessage(message).getString()).setStyle(Style.EMPTY.withHoverEvent(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, formatMessage(text))));
    }

    @NotNull
    public static Component hoverMessageText(@NotNull Component message, @NotNull String text) {
        return message.copy().setStyle(Style.EMPTY.withHoverEvent(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, formatMessage(text))));
    }

    public static void broadcast(@NotNull String message) {
        ExtraLib.getInstance().getServer().getPlayerList().broadcastSystemMessage(formatMessage(message), false);
    }

    public static void clickableBroadcastCommand(@NotNull String message, @NotNull String command) {
        ExtraLib.getInstance().getServer().getPlayerList().broadcastSystemMessage(clickableMessageCommand(message, command), false);
    }
}