package com.vecoo.extralib.storage.player;

import com.google.gson.Gson;
import com.vecoo.extralib.ExtraLib;
import com.vecoo.extralib.gson.UtilGson;
import com.vecoo.extralib.world.UtilWorld;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerProvider {
    private final String filePath = UtilWorld.worldDirectory(ExtraLib.getInstance().getConfig().getPlayerStorage());
    private final HashMap<UUID, PlayerStorage> map;

    public PlayerProvider() {
        this.map = new HashMap<>();
    }

    public PlayerStorage getPlayerStorage(UUID playerUUID) {
        if (this.map.get(playerUUID) == null) {
            new PlayerStorage(playerUUID);
        }
        return this.map.get(playerUUID);
    }

    public void updatePlayerStorage(PlayerStorage player) {
        this.map.put(player.getUuid(), player);
        if (!write(player)) {
            getPlayerStorage(player.getUuid());
        }
    }

    private boolean write(PlayerStorage player) {
        Gson gson = UtilGson.newGson();
        CompletableFuture<Boolean> future = UtilGson.writeFileAsync(filePath, player.getUuid() + ".json", gson.toJson(player));
        return future.join();
    }

    public void init() {
        File dir = UtilGson.checkForDirectory(filePath);
        String[] list = dir.list();

        if (list == null) {
            return;
        }

        for (String file : list) {
            UtilGson.readFileAsync(filePath, file, el -> {
                Gson gson = UtilGson.newGson();
                PlayerStorage player = gson.fromJson(el, PlayerStorage.class);
                this.map.put(player.getUuid(), player);
            });
        }
    }
}