package com.vecoo.extralib;

import com.vecoo.extralib.config.ServerConfig;
import com.vecoo.extralib.storage.player.PlayerProvider;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ExtraLib.MOD_ID)
public class ExtraLib {
    public static final String MOD_ID = "extralib";
    private static final Logger LOGGER = LogManager.getLogger("ExtraLib");

    private static ExtraLib instance;

    private ServerConfig config;

    private PlayerProvider playerProvider;

    private MinecraftServer server;

    public ExtraLib() {
        instance = this;

        this.loadConfig();

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        server = event.getServer();
        this.loadStorage();
    }

    public void loadConfig() {
        try {
            this.config = new ServerConfig();
            this.config.init();
        } catch (Exception e) {
            LOGGER.error("[ExtraLib] Error load config.");
        }
    }

    public void loadStorage() {
        try {
            this.playerProvider = new PlayerProvider();
            this.playerProvider.init();
        } catch (Exception e) {
            LOGGER.error("[ExtraLib] Error load storage.");
        }
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

    public ServerConfig getConfig() {
        return instance.config;
    }

    public PlayerProvider getPlayerProvider() {
        return instance.playerProvider;
    }
}