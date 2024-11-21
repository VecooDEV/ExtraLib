package com.vecoo.extralib;

import com.vecoo.extralib.storage.player.PlayerProvider;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ExtraLib.MOD_ID)
public class ExtraLib {
    public static final String MOD_ID = "extralib";
    private static final Logger LOGGER = LogManager.getLogger("ExtraLib");

    private static ExtraLib instance;

    private PlayerProvider playerProvider;

    private MinecraftServer server;

    public ExtraLib() {
        instance = this;

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        this.server = event.getServer();
        this.loadStorage();
    }

    public void loadStorage() {
        try {
            this.playerProvider = new PlayerProvider("/%directory%/storage/ExtraLib/players/", this.server);
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

    public PlayerProvider getPlayerProvider() {
        return instance.playerProvider;
    }
}