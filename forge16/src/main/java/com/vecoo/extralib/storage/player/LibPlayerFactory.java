package com.vecoo.extralib.storage.player;

import com.vecoo.extralib.ExtraLib;

import java.util.HashMap;
import java.util.UUID;

public class LibPlayerFactory {
    public static boolean hasCommandCooldown(UUID playerUUID, String command) {
        return ExtraLib.getInstance().getPlayerProvider().getPlayerStorage(playerUUID).hasCommandCooldown(command);
    }

    public static HashMap<String, Long> getCommandCooldown(UUID playerUUID) {
        return ExtraLib.getInstance().getPlayerProvider().getPlayerStorage(playerUUID).getCommandCooldown();
    }

    public static void addCommandCooldown(UUID playerUUID, String command, long time) {
        ExtraLib.getInstance().getPlayerProvider().getPlayerStorage(playerUUID).addCommandCooldown(command, time);
    }

    public static void removeCommandCooldown(UUID playerUUID, String command) {
        ExtraLib.getInstance().getPlayerProvider().getPlayerStorage(playerUUID).removeCommandCooldown(command);
    }
}
