package com.vecoo.extralib.permissiontest;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.server.players.ServerOpListEntry;

public final class PlatformPermissions {
    private PlatformPermissions() {
    }

    public static void setLevel(ServerPlayer player, int level) {
        player.level().getServer().getPlayerList().getOps().add(new ServerOpListEntry(player.nameAndId(), LevelBasedPermissionSet.forLevel(PermissionLevel.byId(level)), false));
    }

    public static void clear(ServerPlayer player) {
        player.level().getServer().getPlayerList().getOps().remove(player.nameAndId());
    }
}
