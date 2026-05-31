package com.vecoo.extralib.util;

import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TextUtil {
    @NotNull
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([a-fA-F0-9]{6})");
    @NotNull
    private static final Pattern URL_PATTERN = Pattern.compile("(?:https?://)?(?:[\\w-]+\\.)+[\\w-]+(?:/[\\w-./?%&=]*)?");

    private TextUtil() {
    }

    /**
     * Formats a string into a Minecraft {@link StringTextComponent }, replacing
     * standard color codes from '&' to '§' and parsing '&#RRGGBB' HEX codes.
     *
     * @param message the raw message string with color codes
     * @return a {@link StringTextComponent } with color codes applied
     */
    @NotNull
    public static IFormattableTextComponent formatMessage(@NotNull String message) {
        Matcher matcher = HEX_PATTERN.matcher(message);
        IFormattableTextComponent result = new StringTextComponent("");
        int lastEnd = 0;
        Color currentColor = null;

        while (matcher.find()) {
            String textSegment = message.substring(lastEnd, matcher.start());

            if (!textSegment.isEmpty()) {
                result.append(processLinks(textSegment.replace("&", "§"), currentColor));
            }

            currentColor = Color.fromRgb(Integer.parseInt(matcher.group(1), 16));
            lastEnd = matcher.end();
        }

        String tail = message.substring(lastEnd);

        if (!tail.isEmpty()) {
            result.append(processLinks(tail.replace("&", "§"), currentColor));
        }

        return result;
    }

    /**
     * Creates a clickable {@link IFormattableTextComponent} that runs a command when clicked.
     *
     * @param message the text of the message
     * @param command the command to run when clicked (e.g., "/say hello")
     * @return a {@link IFormattableTextComponent} with click action applied
     */
    @NotNull
    public static IFormattableTextComponent clickableMessageCommand(@NotNull String message, @NotNull String command) {
        return new StringTextComponent(formatMessage(message).getString()).setStyle(Style.EMPTY.withClickEvent(
                new ClickEvent(ClickEvent.Action.RUN_COMMAND, command)));
    }

    /**
     * Creates a clickable {@link IFormattableTextComponent} that runs a command when clicked.
     *
     * @param message the {@link IFormattableTextComponent} message
     * @param command the command to run when clicked
     * @return a new {@link IFormattableTextComponent} with click action applied
     */
    @NotNull
    public static IFormattableTextComponent clickableMessageCommand(@NotNull StringTextComponent message, @NotNull String command) {
        return message.copy().setStyle(Style.EMPTY.withClickEvent(
                new ClickEvent(ClickEvent.Action.RUN_COMMAND, command)));
    }

    /**
     * Creates a clickable {@link IFormattableTextComponent} that opens a URL when clicked.
     *
     * @param message the text of the message
     * @param url     the URL to open
     * @return a {@link IFormattableTextComponent} with click action applied
     */
    @NotNull
    public static IFormattableTextComponent clickableMessageURL(@NotNull String message, @NotNull String url) {
        return new StringTextComponent(formatMessage(message).getString()).setStyle(Style.EMPTY.withClickEvent(
                new ClickEvent(ClickEvent.Action.OPEN_URL, url)));
    }

    /**
     * Creates a clickable {@link IFormattableTextComponent} that opens a URL when clicked.
     *
     * @param message the {@link IFormattableTextComponent} message
     * @param url     the URL to open
     * @return a new {@link IFormattableTextComponent} with click action applied
     */
    @NotNull
    public static IFormattableTextComponent clickableMessageURL(@NotNull StringTextComponent message, @NotNull String url) {
        return message.copy().setStyle(Style.EMPTY.withClickEvent(
                new ClickEvent(ClickEvent.Action.OPEN_URL, url)));
    }

    /**
     * Creates a {@link IFormattableTextComponent} with a hover text when the mouse hovers over the message.
     *
     * @param message the text of the message
     * @param text    the hover text to display
     * @return a {@link IFormattableTextComponent} with hover event applied
     */
    @NotNull
    public static IFormattableTextComponent hoverMessageText(@NotNull String message, @NotNull String text) {
        return new StringTextComponent(formatMessage(message).getString()).setStyle(Style.EMPTY.withHoverEvent(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, formatMessage(text))));
    }

    /**
     * Creates a {@link IFormattableTextComponent} with a hover text when the mouse hovers over the message.
     *
     * @param message the {@link IFormattableTextComponent} message
     * @param text    the hover text to display
     * @return a new {@link IFormattableTextComponent} with hover event applied
     */
    @NotNull
    public static IFormattableTextComponent hoverMessageText(@NotNull StringTextComponent message, @NotNull String text) {
        return message.copy().setStyle(Style.EMPTY.withHoverEvent(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, formatMessage(text))));
    }

    @NotNull
    private static IFormattableTextComponent processLinks(String text, @Nullable Color color) {
        Matcher urlMatcher = URL_PATTERN.matcher(text);
        IFormattableTextComponent segment = new StringTextComponent("");
        int lastEnd = 0;

        while (urlMatcher.find()) {
            if (urlMatcher.start() > lastEnd) {
                segment.append(new StringTextComponent(text.substring(lastEnd, urlMatcher.start()))
                        .withStyle(s -> color != null ? s.withColor(color) : s));
            }

            String url = urlMatcher.group();
            String absoluteUrl = url.startsWith("http") ? url : "https://" + url;

            segment.append(new StringTextComponent(url)
                    .withStyle(style -> style
                            .withColor(color != null ? color : Color.fromRgb(0x5555FF))
                            .withUnderlined(true)
                            .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, absoluteUrl))));

            lastEnd = urlMatcher.end();
        }

        if (lastEnd < text.length()) {
            segment.append(new StringTextComponent(text.substring(lastEnd))
                    .withStyle(s -> color != null ? s.withColor(color) : s));
        }

        return segment;
    }
}