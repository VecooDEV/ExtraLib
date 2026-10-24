package com.vecoo.extralib.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Shared logger independent of Minecraft and the mod loader. */
public final class ExtraLibLogger {
    private static final Logger LOGGER = LoggerFactory.getLogger("com.vecoo.extralib.ExtraLib");

    private ExtraLibLogger() {
    }

    public static Logger getLogger() {
        return LOGGER;
    }
}
