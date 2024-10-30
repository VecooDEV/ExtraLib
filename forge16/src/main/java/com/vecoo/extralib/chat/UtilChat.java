package com.vecoo.extralib.chat;

import com.vecoo.extralib.ExtraLib;
import net.minecraft.util.Util;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.event.ClickEvent;

public class UtilChat {
    public static StringTextComponent formatMessage(String unformattedText) {
        return new StringTextComponent(unformattedText.replace("&", "\u00a7"));
    }

    public static StringTextComponent clickableMessage(String message, String command) {
        return (StringTextComponent) formatMessage(message).setStyle(Style.EMPTY.withClickEvent(
                new ClickEvent(ClickEvent.Action.RUN_COMMAND, command)));
    }

    public static void broadcast(String message) {
        ExtraLib.getInstance().getServer().getPlayerList().broadcastMessage(formatMessage(message), ChatType.CHAT, Util.NIL_UUID);
    }

    public static void clickableBroadcast(String message, String command) {
        StringTextComponent text = (StringTextComponent) formatMessage(message).setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command)));
        ExtraLib.getInstance().getServer().getPlayerList().broadcastMessage(text, ChatType.CHAT, Util.NIL_UUID);
    }
}