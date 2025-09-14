package com.vecoo.extralib.permission;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.vecoo.extralib.ExtraLib;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.server.permission.PermissionAPI;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import net.neoforged.neoforge.server.permission.nodes.PermissionTypes;

import java.util.Set;
import java.util.UUID;

public class UtilPermission {
    public static boolean hasPermission(CommandSourceStack source, PermissionNode<Boolean> node) {
        try {
            if (PermissionAPI.getRegisteredNodes().contains(node)) {
                return PermissionAPI.getPermission(source.getPlayerOrException(), node) || source.hasPermission(4);
            }
        } catch (CommandSyntaxException e) {
            return true;
        }

        ExtraLib.getLogger().error("[ExtraLib] No permission found for node: " + node);
        return false;
    }

    public static boolean hasPermission(ServerPlayer player, PermissionNode<Boolean> node) {
        if (PermissionAPI.getRegisteredNodes().contains(node)) {
            return PermissionAPI.getPermission(player, node) || player.hasPermissions(4);
        }

        ExtraLib.getLogger().error("[ExtraLib] No permission found for node: " + node);
        return false;
    }

    public static boolean hasPermission(UUID playerUUID, PermissionNode<Boolean> node) {
        if (PermissionAPI.getRegisteredNodes().contains(node)) {
            return PermissionAPI.getOfflinePermission(playerUUID, node);
        }

        ExtraLib.getLogger().error("[ExtraLib] No permission found for node: " + node);
        return false;
    }

    public static int minValue(int value, ServerPlayer player, Set<PermissionNode<Boolean>> nodeList) {
        for (PermissionNode<Boolean> permission : nodeList) {
            if (UtilPermission.hasPermission(player, permission)) {
                value = Math.min(value, Integer.parseInt(permission.getNodeName().substring(permission.getNodeName().lastIndexOf('.') + 1)));
            }
        }
        return value;
    }

    public static int maxValue(int value, ServerPlayer player, Set<PermissionNode<Boolean>> nodeList) {
        for (PermissionNode<Boolean> permission : nodeList) {
            if (UtilPermission.hasPermission(player, permission)) {
                value = Math.max(value, Integer.parseInt(permission.getNodeName().substring(permission.getNodeName().lastIndexOf('.') + 1)));
            }
        }
        return value;
    }

    public static int minValue(int value, UUID playerUUID, Set<PermissionNode<Boolean>> nodeList) {
        for (PermissionNode<Boolean> permission : nodeList) {
            if (UtilPermission.hasPermission(playerUUID, permission)) {
                value = Math.min(value, Integer.parseInt(permission.getNodeName().substring(permission.getNodeName().lastIndexOf('.') + 1)));
            }
        }
        return value;
    }

    public static int maxValue(int value, UUID playerUUID, Set<PermissionNode<Boolean>> nodeList) {
        for (PermissionNode<Boolean> permission : nodeList) {
            if (UtilPermission.hasPermission(playerUUID, permission)) {
                value = Math.max(value, Integer.parseInt(permission.getNodeName().substring(permission.getNodeName().lastIndexOf('.') + 1)));
            }
        }
        return value;
    }

    public static PermissionNode<Boolean> getPermissionNode(String node) {
        String[] nodeSplit = node.split("\\.", 2);

        return new PermissionNode<>(nodeSplit[0], nodeSplit[1], PermissionTypes.BOOLEAN, (p, uuid, permissionDynamicContexts) -> false);
    }
}