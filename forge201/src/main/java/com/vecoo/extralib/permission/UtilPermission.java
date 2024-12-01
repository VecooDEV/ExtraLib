package com.vecoo.extralib.permission;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.nodes.PermissionNode;

public class UtilPermission {
    public static boolean hasPermission(CommandSourceStack source, PermissionNode<Boolean> node) {
        try {
            if (PermissionAPI.getRegisteredNodes().contains(node)) {
                return PermissionAPI.getPermission(source.getPlayerOrException(), node) || source.hasPermission(2);
            }

            return false;
        } catch (Exception e) {
            return true;
        }
    }

    public static boolean hasPermission(ServerPlayer player, PermissionNode<Boolean> node) {
        if (PermissionAPI.getRegisteredNodes().contains(node)) {
            return PermissionAPI.getPermission(player, node) || player.hasPermissions(2);
        }

        return false;
    }
}