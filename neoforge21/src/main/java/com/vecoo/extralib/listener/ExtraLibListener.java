package com.vecoo.extralib.listener;

import com.vecoo.extralib.util.PlayerUtil;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class ExtraLibListener {
    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();

        PlayerUtil.cacheUUID(player.getUUID(), player.getGameProfile().getName());
    }
}
