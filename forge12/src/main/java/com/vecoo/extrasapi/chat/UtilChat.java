package com.vecoo.extrasapi.chat;

import com.vecoo.extrasapi.ExtrasAPI;
import net.minecraft.util.text.TextComponentString;

public class UtilChat {
    public static String formattedString(String unformattedString) {
        return unformattedString.replace("&", "\u00a7");
    }

    public static TextComponentString formatMessage(String unformattedText) {
        return new TextComponentString(formattedString(unformattedText));
    }

    public static void broadcast(String message) {
        ExtrasAPI.getInstance().getServer().getPlayerList().sendMessage(formatMessage(message));
    }
}