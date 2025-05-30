package com.vecoo.extralib.permission;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.vecoo.extralib.ExtraLib;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.context.Context;

import java.util.List;
import java.util.UUID;

public class UtilPermission {
    public static boolean hasPermission(CommandSource source, String node) {
        try {
            if (PermissionAPI.getPermissionHandler().getRegisteredNodes().contains(node)) {
                return PermissionAPI.hasPermission(source.getPlayerOrException(), node) || source.hasPermission(4);
            }
        } catch (CommandSyntaxException e) {
            return true;
        }

        ExtraLib.getLogger().error("[ExtraLib] No permission found for node: " + node);
        return false;
    }

    public static boolean hasPermission(PlayerEntity player, String node) {
        if (PermissionAPI.getPermissionHandler().getRegisteredNodes().contains(node)) {
            return PermissionAPI.hasPermission(player, node) || player.hasPermissions(4);
        }

        ExtraLib.getLogger().error("[ExtraLib] No permission found for node: " + node);
        return false;
    }

    public static boolean hasPermission(UUID playerUUID, String playerName, String node) {
        if (PermissionAPI.getPermissionHandler().getRegisteredNodes().contains(node)) {
            return PermissionAPI.hasPermission(new GameProfile(playerUUID, playerName), node, new Context());
        }

        ExtraLib.getLogger().error("[ExtraLib] No permission found for node: " + node);
        return false;
    }

    public static int minValue(int value, PlayerEntity player, List<String> permissionList) {
        for (String permission : permissionList) {
            if (UtilPermission.hasPermission(player, permission)) {
                value = Math.min(value, Integer.parseInt(permission.substring(permission.lastIndexOf('.') + 1)));
            }
        }
        return value;
    }

    public static int maxValue(int value, PlayerEntity player, List<String> permissionList) {
        for (String permission : permissionList) {
            if (UtilPermission.hasPermission(player, permission)) {
                value = Math.max(value, Integer.parseInt(permission.substring(permission.lastIndexOf('.') + 1)));
            }
        }
        return value;
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