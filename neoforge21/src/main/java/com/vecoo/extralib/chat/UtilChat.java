package com.vecoo.extralib.chat;

import com.vecoo.extralib.ExtraLib;
import net.minecraft.network.chat.*;
import org.jetbrains.annotations.NotNull;

public final class UtilChat {
    /**
     * Formats a string into a Minecraft {@link Component} and replaces
     * color codes from '&' to '§'.
     *
     * @param message the raw message string with '&' color codes
     * @return a {@link Component} with color codes applied
     */
    @NotNull
    public static Component formatMessage(@NotNull String message) {
        return Component.literal(message.replace("&", "§"));
    }

    /**
     * Creates a clickable {@link Component} that runs a command when clicked.
     *
     * @param message the text of the message
     * @param command the command to run when clicked (e.g., "/say hello")
     * @return a {@link Component} with click action applied
     */
    @NotNull
    public static Component clickableMessageCommand(@NotNull String message, @NotNull String command) {
        return Component.literal(formatMessage(message).getString()).setStyle(Style.EMPTY.withClickEvent(
                new ClickEvent(ClickEvent.Action.RUN_COMMAND, command)));
    }

    /**
     * Creates a clickable {@link MutableComponent} that runs a command when clicked.
     *
     * @param message the {@link MutableComponent} message
     * @param command the command to run when clicked
     * @return a new {@link MutableComponent} with click action applied
     */
    @NotNull
    public static Component clickableMessageCommand(@NotNull Component message, @NotNull String command) {
        return message.copy().setStyle(Style.EMPTY.withClickEvent(
                new ClickEvent(ClickEvent.Action.RUN_COMMAND, command)));
    }

    /**
     * Creates a clickable {@link Component} that opens a URL when clicked.
     *
     * @param message the text of the message
     * @param url     the URL to open
     * @return a {@link Component} with click action applied
     */
    @NotNull
    public static Component clickableMessageURL(@NotNull String message, @NotNull String url) {
        return Component.literal(formatMessage(message).getString()).setStyle(Style.EMPTY.withClickEvent(
                new ClickEvent(ClickEvent.Action.OPEN_URL, url)));
    }

    /**
     * Creates a clickable {@link MutableComponent} that opens a URL when clicked.
     *
     * @param message the {@link MutableComponent} message
     * @param url     the URL to open
     * @return a new {@link MutableComponent} with click action applied
     */
    @NotNull
    public static Component clickableMessageURL(@NotNull Component message, @NotNull String url) {
        return message.copy().setStyle(Style.EMPTY.withClickEvent(
                new ClickEvent(ClickEvent.Action.OPEN_URL, url)));
    }

    /**
     * Creates a {@link Component} with a hover text when the mouse hovers over the message.
     *
     * @param message the text of the message
     * @param text    the hover text to display
     * @return a {@link Component} with hover event applied
     */
    @NotNull
    public static Component hoverMessageText(@NotNull String message, @NotNull String text) {
        return Component.literal(formatMessage(message).getString()).setStyle(Style.EMPTY.withHoverEvent(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, formatMessage(text))));
    }

    /**
     * Creates a {@link MutableComponent} with a hover text when the mouse hovers over the message.
     *
     * @param message the {@link MutableComponent} message
     * @param text    the hover text to display
     * @return a new {@link MutableComponent} with hover event applied
     */
    @NotNull
    public static Component hoverMessageText(@NotNull Component message, @NotNull String text) {
        return message.copy().setStyle(Style.EMPTY.withHoverEvent(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, formatMessage(text))));
    }

    /**
     * Broadcasts a formatted message to all online players.
     *
     * @param message the message to broadcast
     */
    public static void broadcast(@NotNull String message) {
        ExtraLib.getInstance().getServer().getPlayerList().broadcastSystemMessage(formatMessage(message), false);
    }

    /**
     * Broadcasts a clickable command message to all online players.
     *
     * @param message the message text
     * @param command the command to execute when clicked
     */
    public static void clickableBroadcastCommand(@NotNull String message, @NotNull String command) {
        ExtraLib.getInstance().getServer().getPlayerList().broadcastSystemMessage(clickableMessageCommand(message, command), false);
    }
}