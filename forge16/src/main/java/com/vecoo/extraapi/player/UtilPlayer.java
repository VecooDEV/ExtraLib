package com.vecoo.extraapi.player;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.server.permission.PermissionAPI;

public class UtilPlayer {
    public static boolean hasPermission(Entity source, String permission) {
        if (source == null || permission == null) {
            return false;
        }
        return PermissionAPI.hasPermission((ServerPlayerEntity) source, permission);
    }
}