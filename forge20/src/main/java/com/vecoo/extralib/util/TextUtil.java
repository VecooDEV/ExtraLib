package com.vecoo.extralib.util;

import net.minecraft.network.chat.*;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TextUtil {
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([a-fA-F0-9]{6})");

    private TextUtil() {
    }

    /**
     * Formats a string into a Minecraft {@link Component}, replacing
     * standard color codes from '&' to '§' and parsing '&#RRGGBB' HEX codes.
     *
     * @param message the raw message string with color codes
     * @return a {@link Component} with color codes applied
     */
    @NotNull
    public static Component formatMessage(@NotNull String message) {
        Matcher matcher = HEX_PATTERN.matcher(message);
        MutableComponent result = Component.empty();

        int lastEnd = 0;
        TextColor currentColor = null;

        while (matcher.find()) {
            String textSegment = message.substring(lastEnd, matcher.start());

            if (!textSegment.isEmpty()) {
                MutableComponent part = Component.literal(textSegment.replace("&", "§"));

                if (currentColor != null) {
                    part.withStyle(Style.EMPTY.withColor(currentColor));
                }

                result.append(part);
            }

            int rgb = Integer.parseInt(matcher.group(1), 16);

            currentColor = TextColor.fromRgb(rgb);

            lastEnd = matcher.end();
        }

        String tail = message.substring(lastEnd);

        if (!tail.isEmpty()) {
            MutableComponent part = Component.literal(tail.replace("&", "§"));

            if (currentColor != null) {
                part.withStyle(Style.EMPTY.withColor(currentColor));
            }

            result.append(part);
        }

        return result;
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
}