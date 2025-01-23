package com.vecoo.extralib.permission;

import com.mojang.authlib.GameProfile;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.context.Context;

import java.util.List;
import java.util.UUID;

public class UtilPermission {
    public static boolean hasPermission(CommandSource source, String node) {
        try {
            if (PermissionAPI.getPermissionHandler().getRegisteredNodes().contains(node)) {
                return PermissionAPI.hasPermission(source.getPlayerOrException(), node) || source.hasPermission(2);
            } else {
                PermissionAPI.registerNode(node, DefaultPermissionLevel.OP, "");
                return hasPermission(source, node);
            }
        } catch (Exception e) {
            return true;
        }
    }

    public static boolean hasPermission(ServerPlayerEntity player, String node) {
        if (PermissionAPI.getPermissionHandler().getRegisteredNodes().contains(node)) {
            return PermissionAPI.hasPermission(player, node) || player.hasPermissions(2);
        } else {
            PermissionAPI.registerNode(node, DefaultPermissionLevel.OP, "");
            return hasPermission(player, node);
        }
    }

    public static boolean hasPermission(UUID playerUuid, String playerName, String node) {
        if (PermissionAPI.getPermissionHandler().getRegisteredNodes().contains(node)) {
            return PermissionAPI.hasPermission(new GameProfile(playerUuid, playerName), node, new Context());
        } else {
            PermissionAPI.registerNode(node, DefaultPermissionLevel.OP, "");
            return hasPermission(playerUuid, playerName, node);
        }
    }

    public static int minValue(int value, UUID playerUUID, String playerName, List<String> permissionList) {
        for (String permission : permissionList) {
            if (UtilPermission.hasPermission(playerUUID, playerName, permission)) {
                value = Math.min(value, Integer.parseInt(permission.substring(permission.lastIndexOf('.') + 1)));
            }
        }
        return value;
    }

    public static int maxValue(int value, UUID playerUUID, String playerName, List<String> permissionList) {
        for (String permission : permissionList) {
            if (UtilPermission.hasPermission(playerUUID, playerName, permission)) {
                value = Math.max(value, Integer.parseInt(permission.substring(permission.lastIndexOf('.') + 1)));
            }
        }
        return value;
    }
}