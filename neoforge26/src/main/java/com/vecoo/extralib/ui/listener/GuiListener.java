package com.vecoo.extralib.ui.listener;

import com.vecoo.extralib.ui.api.gui.SimpleGui;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;

public class GuiListener {
    @SubscribeEvent
    public void onPlayerContainerClose(PlayerContainerEvent.Close event) {
        Player player = event.getEntity();
        Runnable pending = SimpleGui.pendingOpens.remove(player.getUUID());

        if (pending != null && player.getServer() != null) {
            player.getServer().execute(pending);
        }
    }
}
