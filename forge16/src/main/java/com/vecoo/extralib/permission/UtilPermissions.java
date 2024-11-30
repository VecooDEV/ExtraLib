package com.vecoo.extralib.permission;

import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

public class UtilPermissions {
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
        try {
            if (PermissionAPI.getPermissionHandler().getRegisteredNodes().contains(node)) {
                return PermissionAPI.hasPermission(player, node) || player.hasPermissions(2);
            } else {
                PermissionAPI.registerNode(node, DefaultPermissionLevel.OP, "");
                return hasPermission(player, node);
            }
        } catch (Exception e) {
            return true;
        }
    }
}