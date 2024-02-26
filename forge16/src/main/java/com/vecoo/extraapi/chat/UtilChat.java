package com.vecoo.extraapi.chat;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.event.ClickEvent;

public class UtilChat {
    public static ITextComponent formatMessage(String message) {
        return ITextComponent.Serializer.fromJson(message.replace("&", "§").trim());
    }

    public static ITextComponent clickableMessage(String message, String command) {
        return ITextComponent.Serializer.fromJson(message.replace("&", "§").trim()).setStyle(Style.EMPTY.withClickEvent(
                new ClickEvent(ClickEvent.Action.RUN_COMMAND, command)));
    }

    public static void clickableBroadcast(String message, String command, MinecraftServer server) {
        ITextComponent component = ITextComponent.Serializer.fromJson(message.replace("&", "§").trim()).setStyle(Style.EMPTY.withClickEvent(
                new ClickEvent(ClickEvent.Action.RUN_COMMAND, command)));

        server.getPlayerList().broadcastMessage(component, ChatType.CHAT, Util.NIL_UUID);
    }
}