package com.vecoo.extralib.functionaltest;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

@Mod("extralib_functional_test")
public class FunctionalTestMod {
    public FunctionalTestMod() {
        FunctionalTest test = new FunctionalTest();
        MinecraftForge.EVENT_BUS.addListener((TickEvent.ServerTickEvent event) -> {
            if (event.phase == TickEvent.Phase.END) {
                test.tick(ServerLifecycleHooks.getCurrentServer());
            }
        });
    }
}
