package com.vecoo.extralib.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class ServerConfig {
    @Comment("Will there be notification messages when the server starts?")
    private boolean notification = true;

    public boolean isNotification() {
        return this.notification;
    }
}
