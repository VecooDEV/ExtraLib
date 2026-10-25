package com.vecoo.extralib.functionaltest;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class FunctionalTestMod implements ModInitializer {
    @Override
    public void onInitialize() {
        FunctionalTest test = new FunctionalTest();
        ServerTickEvents.END_SERVER_TICK.register(test::tick);
    }
}
