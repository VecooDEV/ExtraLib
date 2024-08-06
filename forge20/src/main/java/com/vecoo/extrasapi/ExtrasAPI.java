package com.vecoo.extrasapi;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ExtrasAPI.MOD_ID)
public class ExtrasAPI {
    public static final String MOD_ID = "extrasapi";
    private static final Logger LOGGER = LogManager.getLogger("ExtrasAPI");

    private static ExtrasAPI instance;

    private MinecraftServer server;

    public ExtrasAPI() {
        instance = this;

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        server = event.getServer();
    }

    public static ExtrasAPI getInstance() {
        return instance;
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    public MinecraftServer getServer() {
        return instance.server;
    }
}