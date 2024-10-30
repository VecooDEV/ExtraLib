package com.vecoo.extralib.chat;

import com.vecoo.extralib.ExtraLib;
import net.minecraft.util.text.TextComponentString;

public class UtilChat {
    public static TextComponentString formatMessage(String unformattedText) {
        return new TextComponentString(unformattedText.replace("&", "\u00a7"));
    }

    public static void broadcast(String message) {
        ExtraLib.getInstance().getServer().getPlayerList().sendMessage(formatMessage(message));
    }
}