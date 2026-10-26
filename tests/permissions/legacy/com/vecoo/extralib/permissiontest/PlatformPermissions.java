package com.vecoo.extralib.permissiontest;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.ServerOpListEntry;

public final class PlatformPermissions {
    private PlatformPermissions() {
    }

    public static void setLevel(ServerPlayer player, int level) {
        player.server.getPlayerList().getOps().add(new ServerOpListEntry(player.getGameProfile(), level, false));
    }

    public static void clear(ServerPlayer player) {
        player.server.getPlayerList().getOps().remove(player.getGameProfile());
    }
}
