package com.vecoo.extralib.config;

import com.vecoo.extralib.ExtraLib;
import com.vecoo.extralib.gson.UtilGson;

import java.util.concurrent.CompletableFuture;

public class LocaleConfig {
    private String playerNotPermission = "&c(!) You do not have sufficient permissions to use the command.";

    public String getPlayerNotPermission() {
        return this.playerNotPermission;
    }

    private void write() {
        UtilGson.writeFileAsync("/config/ExtraLib/", "locale.json", UtilGson.newGson().toJson(this)).join();
    }

    public void init() {
        try {
            CompletableFuture<Boolean> future = UtilGson.readFileAsync("/config/ExtraLib/", "locale.json", el -> {
                LocaleConfig config = UtilGson.newGson().fromJson(el, LocaleConfig.class);

                this.playerNotPermission = config.getPlayerNotPermission();
            });
            if (!future.join()) {
                write();
            }
        } catch (Exception e) {
            ExtraLib.getLogger().error("[ExtraLib] Error in locale config.");
            write();
        }
    }
}