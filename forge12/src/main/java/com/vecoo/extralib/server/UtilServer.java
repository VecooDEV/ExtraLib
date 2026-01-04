package com.vecoo.extralib.server;

import com.vecoo.extralib.ExtraLib;

import javax.annotation.Nonnull;

public final class UtilServer {
    public static void executeCommand(@Nonnull String command) {
        ExtraLib.getInstance().getServer().getCommandManager().executeCommand(ExtraLib.getInstance().getServer(), command);
    }
}
