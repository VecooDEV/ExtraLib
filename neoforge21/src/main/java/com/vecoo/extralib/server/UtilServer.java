package com.vecoo.extralib.server;

import com.vecoo.extralib.ExtraLib;
import org.jetbrains.annotations.NotNull;

public class UtilServer {
    /**
     * Executes a server command as if it was run by the console.
     * <p>
     * This method creates a command source stack for the server console and performs the
     * specified command. It does not require a player and runs with full permissions.
     * </p>
     *
     * @param command the command to execute, without a leading slash
     */
    public static void executeCommand(@NotNull String command) {
        ExtraLib.getInstance().getServer().getCommands().performPrefixedCommand(
                ExtraLib.getInstance().getServer().createCommandSourceStack(), command);
    }
}
