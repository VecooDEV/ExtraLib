package com.vecoo.extralib.ui.listener;

import com.vecoo.extralib.ui.api.gui.SimpleGui;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.TickTask;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;

public class GuiListener {
    @SubscribeEvent
    public void onPlayerContainerClose(PlayerContainerEvent.Close event) {
        Player player = event.getEntity();
        Runnable pending = SimpleGui.PENDING_OPENS.remove(player.getUUID());

        if (pending != null && player.level() instanceof ServerLevel serverLevel) {
            serverLevel.getServer().schedule(new TickTask(serverLevel.getServer().getTickCount(), pending));
        }
    }
}
