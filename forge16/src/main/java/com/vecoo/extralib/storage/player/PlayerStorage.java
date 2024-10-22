package com.vecoo.extralib.storage.player;

import com.vecoo.extralib.ExtraLib;

import java.util.HashMap;
import java.util.UUID;

public class PlayerStorage {
    private final UUID uuid;
    private final HashMap<String, Long> commandCooldown;

    public PlayerStorage(UUID playerUUID) {
        this.uuid = playerUUID;
        this.commandCooldown = new HashMap<>();
        ExtraLib.getInstance().getPlayerProvider().updatePlayerStorage(this);
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public HashMap<String, Long> getCommandCooldown() {
        return this.commandCooldown;
    }

    public boolean hasCommandCooldown(String command) {
        return this.commandCooldown.containsKey(command);
    }

    public void addCommandCooldown(String command, long time) {
        this.commandCooldown.put(command, time);
        ExtraLib.getInstance().getPlayerProvider().updatePlayerStorage(this);
    }

    public void removeCommandCooldown(String command) {
        this.commandCooldown.remove(command);
        ExtraLib.getInstance().getPlayerProvider().updatePlayerStorage(this);
    }
}
