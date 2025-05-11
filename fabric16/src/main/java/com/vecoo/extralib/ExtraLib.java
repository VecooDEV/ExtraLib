package com.vecoo.extralib;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExtraLib implements ModInitializer {
    public static final String MOD_ID = "extralib";
    private static final Logger LOGGER = LogManager.getLogger("ExtraLib");

    private static ExtraLib instance;

    private boolean luckpermsLoaded;

    @Override
    public void onInitialize() {
        instance = this;

        this.luckpermsLoaded = FabricLoader.getInstance().isModLoaded("luckperms");
    }

    public static ExtraLib getInstance() {
        return instance;
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    public static boolean isLuckpermsLoaded() {
        return instance.luckpermsLoaded;
    }
}