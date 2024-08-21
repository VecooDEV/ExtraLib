package com.vecoo.extralib;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = ExtraLib.MOD_ID,
        name = "ExtraLib",
        version = "1.1.3",
        acceptableRemoteVersions = "*"
)
public class ExtraLib {
    public static final String MOD_ID = "extralib";
    private static final Logger LOGGER = LogManager.getLogger("ExtraLib");

    private static ExtraLib instance;

    private MinecraftServer server;

    @Mod.EventHandler
    public void onPreInitialization(FMLPreInitializationEvent event) {
        instance = this;
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        this.server = event.getServer();
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