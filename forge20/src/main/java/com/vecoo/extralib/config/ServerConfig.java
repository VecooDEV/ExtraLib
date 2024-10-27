package com.vecoo.extralib.config;

import com.google.gson.Gson;
import com.vecoo.extralib.ExtraLib;
import com.vecoo.extralib.gson.UtilGson;

import java.util.concurrent.CompletableFuture;

public class ServerConfig {
    private String playerStorage = "/%directory%/storage/ExtraLib/players/";

    public String getPlayerStorage() {
        return this.playerStorage;
    }

    private void write() {
        Gson gson = UtilGson.newGson();
        CompletableFuture<Boolean> future = UtilGson.writeFileAsync("/config/ExtraLib/", "config.json", gson.toJson(this));
        future.join();
    }

    public void init() {
        try {
            CompletableFuture<Boolean> future = UtilGson.readFileAsync("/config/ExtraLib/", "config.json", el -> {
                Gson gson = UtilGson.newGson();
                ServerConfig config = gson.fromJson(el, ServerConfig.class);

                this.playerStorage = config.getPlayerStorage();
            });
            if (!future.join()) {
                write();
            }
        } catch (Exception e) {
            ExtraLib.getLogger().error("[ExtraLib] Error in config.");
        }
    }
}