package com.vecoo.extralib;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExtraLib implements ModInitializer {
    public static final String MOD_ID = "extralib";
    private static final Logger LOGGER = LogManager.getLogger("ExtraLib");

    private static ExtraLib instance;

    @Override
    public void onInitialize() {
        instance = this;
    }

    public static ExtraLib getInstance() {
        return instance;
    }

    public static Logger getLogger() {
        return LOGGER;
    }
}