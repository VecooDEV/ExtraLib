package com.vecoo.extralib.server;

import com.vecoo.extralib.ExtraLib;
import org.jetbrains.annotations.NotNull;

public class UtilServer {
    public void executeCommand(@NotNull String command) {
        ExtraLib.getInstance().getServer().getCommands().performPrefixedCommand(
                ExtraLib.getInstance().getServer().createCommandSourceStack(), command);
    }
}
