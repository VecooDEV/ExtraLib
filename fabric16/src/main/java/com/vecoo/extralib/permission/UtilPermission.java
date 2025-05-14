package com.vecoo.extralib.permission;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class UtilPermission {
    public static boolean hasPermission(CommandSourceStack source, String node) {
        return Permissions.check(source, node) || source.hasPermission(4);
    }

    public static boolean hasPermission(ServerPlayer player, String node) {
        return Permissions.check(player, node) || player.hasPermissions(4);
    }

    public static int minValue(int value, ServerPlayer player, List<String> permissionList) {
        for (String permission : permissionList) {
            if (UtilPermission.hasPermission(player, permission)) {
                value = Math.min(value, Integer.parseInt(permission.substring(permission.lastIndexOf('.') + 1)));
            }
        }
        return value;
    }

    public static int maxValue(int value, ServerPlayer player, List<String> permissionList) {
        for (String permission : permissionList) {
            if (UtilPermission.hasPermission(player, permission)) {
                value = Math.max(value, Integer.parseInt(permission.substring(permission.lastIndexOf('.') + 1)));
            }
        }
        return value;
    }
}