package com.vecoo.extralib.functionaltest;

import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@Mod("extralib_functional_test")
public class FunctionalTestMod {
    public FunctionalTestMod() {
        FunctionalTest test = new FunctionalTest();
        NeoForge.EVENT_BUS.addListener((ServerTickEvent.Post event) -> test.tick(event.getServer()));
    }
}
