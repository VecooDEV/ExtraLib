package com.vecoo.extralib.permission;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.vecoo.extralib.ExtraLib;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import net.minecraftforge.server.permission.nodes.PermissionTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

public class UtilPermission {
    public static boolean hasPermission(@NotNull CommandSourceStack source, @NotNull PermissionNode<Boolean> node) {
        try {
            if (PermissionAPI.getRegisteredNodes().contains(node)) {
                ServerPlayer player = source.getPlayerOrException();

                return PermissionAPI.getPermission(player, node) || player.hasPermissions(4);
            }
        } catch (CommandSyntaxException e) {
            return true;
        }

        ExtraLib.getLogger().error("No permission found for node: " + node);
        return false;
    }

    public static boolean hasPermission(@NotNull ServerPlayer player, @NotNull PermissionNode<Boolean> node) {
        if (PermissionAPI.getRegisteredNodes().contains(node)) {
            return PermissionAPI.getPermission(player, node) || player.hasPermissions(4);
        }

        ExtraLib.getLogger().error("No permission found for node: " + node);
        return false;
    }

    public static boolean hasPermission(@NotNull UUID playerUUID, @NotNull PermissionNode<Boolean> node) {
        if (PermissionAPI.getRegisteredNodes().contains(node)) {
            return PermissionAPI.getOfflinePermission(playerUUID, node);
        }

        ExtraLib.getLogger().error("No permission found for node: " + node);
        return false;
    }

    public static int minValue(int value, @NotNull ServerPlayer player, @NotNull Set<PermissionNode<Boolean>> nodeList) {
        for (PermissionNode<Boolean> permission : nodeList) {
            if (UtilPermission.hasPermission(player, permission)) {
                value = Math.min(value, Integer.parseInt(permission.getNodeName().substring(permission.getNodeName().lastIndexOf('.') + 1)));
            }
        }
        return value;
    }

    public static int maxValue(int value, @NotNull ServerPlayer player, @NotNull Set<PermissionNode<Boolean>> nodeList) {
        for (PermissionNode<Boolean> permission : nodeList) {
            if (UtilPermission.hasPermission(player, permission)) {
                value = Math.max(value, Integer.parseInt(permission.getNodeName().substring(permission.getNodeName().lastIndexOf('.') + 1)));
            }
        }
        return value;
    }

    public static int minValue(int value, @NotNull UUID playerUUID, @NotNull Set<PermissionNode<Boolean>> nodeList) {
        for (PermissionNode<Boolean> permission : nodeList) {
            if (UtilPermission.hasPermission(playerUUID, permission)) {
                value = Math.min(value, Integer.parseInt(permission.getNodeName().substring(permission.getNodeName().lastIndexOf('.') + 1)));
            }
        }
        return value;
    }

    public static int maxValue(int value, @NotNull UUID playerUUID, @NotNull Set<PermissionNode<Boolean>> nodeList) {
        for (PermissionNode<Boolean> permission : nodeList) {
            if (UtilPermission.hasPermission(playerUUID, permission)) {
                value = Math.max(value, Integer.parseInt(permission.getNodeName().substring(permission.getNodeName().lastIndexOf('.') + 1)));
            }
        }
        return value;
    }

    @NotNull
    public static PermissionNode<Boolean> getPermissionNode(@NotNull String node) {
        String[] nodeSplit = node.split("\\.", 2);

        return new PermissionNode<>(nodeSplit[0], nodeSplit[1], PermissionTypes.BOOLEAN, (player, uuid, permissionDynamicContexts) -> false);
    }
}