package com.vecoo.extraapi.player;

import com.google.common.collect.Maps;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import net.minecraftforge.server.permission.nodes.PermissionTypes;

import java.util.Arrays;
import java.util.Map;

public class UtilPlayer {
    private static final Map<String, PermissionNode<Boolean>> PERMISSION_NODES = Maps.newConcurrentMap();

    public static PermissionNode<?> registerPermission(String MOD_ID, String permissionNode) {
        if (!permissionNode.contains(".")) {
            return registerPermissions(MOD_ID, permissionNode);
        }

        String[] split = permissionNode.split("\\.");
        return registerPermissions(split[0], String.join(" ", Arrays.copyOfRange(split, 1, split.length)));
    }

    public static PermissionNode<?> registerPermissions(String modId, String permissionNode) {
        var registeredPermission = new PermissionNode<>(modId, permissionNode,
                PermissionTypes.BOOLEAN, (player, uuid, contexts) -> false);

        PERMISSION_NODES.put(permissionNode, registeredPermission);
        return registeredPermission;
    }

    public static boolean hasPermission(ServerPlayer player, String permission) {
        if (player == null || permission == null) {
            return false;
        }
        var permissionNode = PERMISSION_NODES.get(permission);
        if (permissionNode == null) {
            return false;
        }

        return (PermissionAPI.getPermission(player, permissionNode) || player.hasPermissions(4));
    }
}