package com.vecoo.extralib;

import com.mojang.logging.LogUtils;
import com.vecoo.extralib.task.TaskTimer;
import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import org.slf4j.Logger;

@Mod(ExtraLib.MOD_ID)
public class ExtraLib {
    public static final String MOD_ID = "extralib";
    private static final Logger LOGGER = LogUtils.getLogger();

    private static ExtraLib instance;

    private MinecraftServer server;

    public ExtraLib() {
        instance = this;

        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(new TaskTimer.EventHandler());
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        this.server = event.getServer();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onServerStopping(ServerStoppingEvent event) {
        TaskTimer.cancelAll();
    }

    public static ExtraLib instance() {
        return instance;
    }

    public static Logger logger() {
        return LOGGER;
    }

    public MinecraftServer server() {
        return instance.server;
    }
}