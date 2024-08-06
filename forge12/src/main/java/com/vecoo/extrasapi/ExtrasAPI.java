package com.vecoo.extrasapi;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = ExtrasAPI.MOD_ID,
        name = "ExtrasAPI",
        version = "1.1.1",
        acceptableRemoteVersions = "*"
)
public class ExtrasAPI {
    public static final String MOD_ID = "extrasapi";
    private static final Logger LOGGER = LogManager.getLogger("ExtrasAPI");

    @Mod.Instance(ExtrasAPI.MOD_ID)
    private static ExtrasAPI instance;

    private MinecraftServer server;

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        this.server = event.getServer();
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