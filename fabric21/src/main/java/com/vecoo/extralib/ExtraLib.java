package com.vecoo.extralib;

import com.vecoo.extralib.task.TaskTimer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExtraLib implements ModInitializer {
    public static final String MOD_ID = "extralib";
    private static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    private static ExtraLib instance;

    private MinecraftServer server;

    @Override
    public void onInitialize() {
        instance = this;

        ServerLifecycleEvents.SERVER_STARTING.register(server -> this.server = server);
        ServerTickEvents.END_SERVER_TICK.register(server -> TaskTimer.onServerTick());
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