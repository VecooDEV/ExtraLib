package com.vecoo.extralib;

import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ExtraLib.MOD_ID)
public class ExtraLib {
    public static final String MOD_ID = "extralib";
    private static final Logger LOGGER = LogManager.getLogger("ExtraLib");

    private static ExtraLib instance;

    public ExtraLib() {
        instance = this;
    }

    public static ExtraLib getInstance() {
        return instance;
    }

    public static Logger getLogger() {
        return LOGGER;
    }
}