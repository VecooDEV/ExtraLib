package com.vecoo.extralib.storage;

import com.vecoo.extralib.ExtraLib;

import java.util.HashMap;
import java.util.UUID;

public class LibFactory {
    public static boolean hasCommandCooldown(UUID playerUUID, String command) {
        return ExtraLib.getInstance().getPlayerProvider().getPlayerStorage(playerUUID).hasKeyCooldown(command);
    }

    public static HashMap<String, Long> getCommandCooldown(UUID playerUUID) {
        return ExtraLib.getInstance().getPlayerProvider().getPlayerStorage(playerUUID).getKeyCooldown();
    }

    public static void addCommandCooldown(UUID playerUUID, String command, long time) {
        ExtraLib.getInstance().getPlayerProvider().getPlayerStorage(playerUUID).addKeyCooldown(command, time);
    }

    public static void removeCommandCooldown(UUID playerUUID, String command) {
        ExtraLib.getInstance().getPlayerProvider().getPlayerStorage(playerUUID).removeKeyCooldown(command);
    }
}
