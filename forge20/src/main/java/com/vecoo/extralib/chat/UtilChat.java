package com.vecoo.extralib.chat;

import com.vecoo.extralib.ExtraLib;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

public class UtilChat {
    private static Component formatMessage(String message) {
        return Component.literal(message.replace("&", "\u00a7").trim());
    }

    public static Component clickableMessage(String message, String command) {
        return Component.literal(formatMessage(message).getString()).setStyle(Style.EMPTY.withClickEvent(
                new ClickEvent(ClickEvent.Action.RUN_COMMAND, command)));
    }

    public static void broadcast(String message) {
        ExtraLib.getInstance().getServer().getPlayerList().broadcastSystemMessage(formatMessage(message), false);
    }

    public static void clickableBroadcast(String message, String command) {
        Component component = Component.literal(formatMessage(message).getString()).setStyle(Style.EMPTY.withClickEvent(
                new ClickEvent(ClickEvent.Action.RUN_COMMAND, command)));
        ExtraLib.getInstance().getServer().getPlayerList().broadcastSystemMessage(component, false);
    }
}