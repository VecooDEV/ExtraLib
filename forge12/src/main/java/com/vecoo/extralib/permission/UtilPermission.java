package com.vecoo.extralib.permission;

import com.mojang.authlib.GameProfile;
import com.vecoo.extralib.ExtraLib;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.context.Context;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.UUID;

public class UtilPermission {
    public static boolean hasPermission(@Nonnull ICommandSender source, @Nonnull String node) {
        if (!(source instanceof EntityPlayer)) {
            return true;
        }

        if (PermissionAPI.getPermissionHandler().getRegisteredNodes().contains(node)) {
            return PermissionAPI.hasPermission((EntityPlayer) source, node) || source.canUseCommand(4, node);
        }

        ExtraLib.getLogger().error("No permission found for node: " + node);
        return false;
    }

    public static boolean hasPermission(@Nonnull EntityPlayer player, @Nonnull String node) {
        if (PermissionAPI.getPermissionHandler().getRegisteredNodes().contains(node)) {
            return PermissionAPI.hasPermission(player, node) || player.canUseCommand(4, node);
        }

        ExtraLib.getLogger().error("No permission found for node: " + node);
        return false;
    }

    public static boolean hasPermission(@Nonnull UUID playerUUID, @Nonnull String playerName, @Nonnull String node) {
        if (PermissionAPI.getPermissionHandler().getRegisteredNodes().contains(node)) {
            return PermissionAPI.hasPermission(new GameProfile(playerUUID, playerName), node, new Context());
        }

        ExtraLib.getLogger().error("No permission found for node: " + node);
        return false;
    }

    public static int minValue(int value, @Nonnull EntityPlayer player, @Nonnull Set<String> permissionList) {
        for (String permission : permissionList) {
            if (UtilPermission.hasPermission(player, permission)) {
                value = Math.min(value, Integer.parseInt(permission.substring(permission.lastIndexOf('.') + 1)));
            }
        }
        return value;
    }

    public static int maxValue(int value, @Nonnull EntityPlayer player, @Nonnull Set<String> permissionList) {
        for (String permission : permissionList) {
            if (UtilPermission.hasPermission(player, permission)) {
                value = Math.max(value, Integer.parseInt(permission.substring(permission.lastIndexOf('.') + 1)));
            }
        }
        return value;
    }

    public static int minValue(int value, @Nonnull UUID playerUUID, @Nonnull String playerName, @Nonnull Set<String> permissionList) {
        for (String permission : permissionList) {
            if (UtilPermission.hasPermission(playerUUID, playerName, permission)) {
                value = Math.min(value, Integer.parseInt(permission.substring(permission.lastIndexOf('.') + 1)));
            }
        }
        return value;
    }

    public static int maxValue(int value, @Nonnull UUID playerUUID, @Nonnull String playerName, @Nonnull Set<String> permissionList) {
        for (String permission : permissionList) {
            if (UtilPermission.hasPermission(playerUUID, playerName, permission)) {
                value = Math.max(value, Integer.parseInt(permission.substring(permission.lastIndexOf('.') + 1)));
            }
        }
        return value;
    }
}