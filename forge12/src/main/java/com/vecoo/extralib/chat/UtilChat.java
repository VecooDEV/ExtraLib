package com.vecoo.extralib.chat;

import com.vecoo.extralib.ExtraLib;
import net.minecraft.util.text.TextComponentString;

public class UtilChat {
    private static String formattedString(String unformattedString) {
        return unformattedString.replace("&", "\u00a7");
    }

    public static TextComponentString formatMessage(String unformattedText) {
        return new TextComponentString(formattedString(unformattedText));
    }

    public static void broadcast(String message) {
        ExtraLib.getInstance().getServer().getPlayerList().sendMessage(formatMessage(message));
    }
}