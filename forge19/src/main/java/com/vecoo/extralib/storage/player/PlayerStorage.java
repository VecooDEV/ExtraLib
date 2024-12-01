package com.vecoo.extralib.storage.player;

import com.vecoo.extralib.ExtraLib;

import java.util.HashMap;
import java.util.UUID;

public class PlayerStorage {
    private final UUID uuid;
    private final HashMap<String, Long> keyCooldown;

    public PlayerStorage(UUID playerUUID) {
        this.uuid = playerUUID;
        this.keyCooldown = new HashMap<>();
        ExtraLib.getInstance().getPlayerProvider().updatePlayerStorage(this);
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public HashMap<String, Long> getKeyCooldown() {
        return this.keyCooldown;
    }

    public void addKeyCooldown(String command, long time) {
        this.keyCooldown.put(command, time);
        ExtraLib.getInstance().getPlayerProvider().updatePlayerStorage(this);
    }

    public void removeKeyCooldown(String command) {
        this.keyCooldown.remove(command);
        ExtraLib.getInstance().getPlayerProvider().updatePlayerStorage(this);
    }
}
