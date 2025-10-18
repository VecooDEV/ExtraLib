package com.vecoo.extralib;

import com.vecoo.extralib.task.TaskTimer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = ExtraLib.MOD_ID, acceptableRemoteVersions = "*", useMetadata = true)
public class ExtraLib {
    public static final String MOD_ID = "extralib";
    private static Logger LOGGER;

    private static ExtraLib instance;

    private MinecraftServer server;

    @Mod.EventHandler
    public void onPreInitialization(FMLPreInitializationEvent event) {
        instance = this;
        LOGGER = event.getModLog();

        MinecraftForge.EVENT_BUS.register(new TaskTimer.EventHandler());
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        this.server = event.getServer();
    }

    @Mod.EventHandler
    public void onServerStopping(FMLServerStoppingEvent event) {
        TaskTimer.cancelAll();
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