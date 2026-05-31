package com.vecoo.extralib.util;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import org.jetbrains.annotations.NotNull;

public final class TextUtil {
    private TextUtil() {
    }

    @NotNull
    public static TextComponentString formatMessage(@NotNull String message) {
        return new TextComponentString(message.replace("&", "§"));
    }

    /**
     * Creates a clickable {@link ITextComponent } that runs a command when clicked.
     *
     * @param message the text of the message
     * @param command the command to run when clicked (e.g., "/say hello")
     * @return a {@link ITextComponent } with click action applied
     */
    @NotNull
    public static ITextComponent clickableMessageCommand(@NotNull String message, @NotNull String command) {
        return formatMessage(message).setStyle(new Style().setClickEvent(
                new ClickEvent(ClickEvent.Action.RUN_COMMAND, command)));
    }

    /**
     * Creates a clickable {@link ITextComponent } that runs a command when clicked.
     *
     * @param message the {@link ITextComponent } message
     * @param command the command to run when clicked
     * @return a new {@link ITextComponent } with click action applied
     */
    @NotNull
    public static ITextComponent clickableMessageCommand(@NotNull ITextComponent message, @NotNull String command) {
        return message.createCopy().setStyle(new Style().setClickEvent(
                new ClickEvent(ClickEvent.Action.RUN_COMMAND, command)));
    }

    /**
     * Creates a clickable {@link ITextComponent} that opens a URL when clicked.
     *
     * @param message the text of the message
     * @param url     the URL to open
     * @return a {@link ITextComponent } with click action applied
     */
    @NotNull
    public static ITextComponent clickableMessageURL(@NotNull String message, @NotNull String url) {
        return formatMessage(message).setStyle(new Style().setClickEvent(
                new ClickEvent(ClickEvent.Action.OPEN_URL, url)));
    }

    /**
     * Creates a clickable {@link ITextComponent } that opens a URL when clicked.
     *
     * @param message the {@link ITextComponent } message
     * @param url     the URL to open
     * @return a new {@link ITextComponent } with click action applied
     */
    @NotNull
    public static ITextComponent clickableMessageURL(@NotNull ITextComponent message, @NotNull String url) {
        return message.createCopy().setStyle(new Style().setClickEvent(
                new ClickEvent(ClickEvent.Action.OPEN_URL, url)));
    }

    /**
     * Creates a {@link ITextComponent } with a hover text when the mouse hovers over the message.
     *
     * @param message the text of the message
     * @param text    the hover text to display
     * @return a {@link ITextComponent } with hover event applied
     */
    @NotNull
    public static ITextComponent hoverMessageText(@NotNull String message, @NotNull String text) {
        return formatMessage(message).setStyle(new Style().setHoverEvent(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, formatMessage(text))));
    }

    /**
     * Creates a {@link ITextComponent } with a hover text when the mouse hovers over the message.
     *
     * @param message the {@link ITextComponent } message
     * @param text    the hover text to display
     * @return a new {@link ITextComponent } with hover event applied
     */
    @NotNull
    public static ITextComponent hoverMessageText(@NotNull ITextComponent message, @NotNull String text) {
        return message.createCopy().setStyle(new Style().setHoverEvent(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, formatMessage(text))));
    }
}