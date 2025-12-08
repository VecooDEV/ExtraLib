package com.vecoo.extralib;

import com.mojang.logging.LogUtils;
import com.vecoo.extralib.task.TaskTimer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;

public class ExtraLib implements ModInitializer {
    public static final String MOD_ID = "extralib";
    private static final Logger LOGGER = LogUtils.getLogger();

    private static ExtraLib instance;

    private MinecraftServer server;

    @Override
    public void onInitialize() {
        instance = this;

        ServerLifecycleEvents.SERVER_STARTING.register(server -> this.server = server);
        ServerTickEvents.END_SERVER_TICK.register(server -> TaskTimer.onServerTickEnd());
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> TaskTimer.cancelAll());
    }

    public static ExtraLib getInstance() {
        return instance;
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    public MinecraftServer getServer() {
        return instance.server;
    }
}