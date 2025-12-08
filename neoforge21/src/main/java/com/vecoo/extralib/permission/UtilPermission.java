package com.vecoo.extralib.permission;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.vecoo.extralib.ExtraLib;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.server.permission.PermissionAPI;
import net.neoforged.neoforge.server.permission.nodes.PermissionDynamicContext;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import net.neoforged.neoforge.server.permission.nodes.PermissionTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

public class UtilPermission {
    /**
     * Checks whether the given command source has the specified permission.
     * <p>
     * If the source represents a player, the permission is resolved through the PermissionAPI.
     * Players with operator level 4 automatically bypass permissions.
     * <br>
     * If the source is not a player (e.g. console), the operation is treated as having permission.
     * <p>
     * If the permission node is not registered, an error is logged and {@code false} is returned.
     *
     * @param source the command source executing the action
     * @param node   the permission node to check
     * @return {@code true} if the source has permission or is console; otherwise {@code false}
     */
    public static boolean hasPermission(@NotNull CommandSourceStack source, @NotNull PermissionNode<Boolean> node) {
        try {
            if (PermissionAPI.getRegisteredNodes().contains(node)) {
                ServerPlayer player = source.getPlayerOrException();

                return PermissionAPI.getPermission(player, node) || player.hasPermissions(4);
            }
        } catch (CommandSyntaxException e) {
            return true;
        }

        ExtraLib.getLogger().error("No permission found for node: {}.", node);
        return false;
    }

    /**
     * Checks whether the given player has the specified permission.
     * <p>
     * The permission is resolved through the PermissionAPI.
     * Players with operator level 4 automatically bypass the check.
     * <p>
     * If the permission node is not registered, an error is logged and {@code false} is returned.
     *
     * @param player the player whose permissions are checked
     * @param node   the permission node to evaluate
     * @return {@code true} if the player has the permission or is OP-level 4; otherwise {@code false}
     */
    public static boolean hasPermission(@NotNull ServerPlayer player, @NotNull PermissionNode<Boolean> node) {
        if (PermissionAPI.getRegisteredNodes().contains(node)) {
            return PermissionAPI.getPermission(player, node) || player.hasPermissions(4);
        }

        ExtraLib.getLogger().error("No permission found for node: {}.", node);
        return false;
    }

    /**
     * Checks whether an offline or online player (identified by UUID) has the specified permission.
     * <p>
     * Uses {@link PermissionAPI#getOfflinePermission(UUID, PermissionNode, PermissionDynamicContext[])}.
     * <p>
     * If the node is not registered, an error is logged and {@code false} is returned.
     *
     * @param playerUUID the UUID of the player
     * @param node       the permission node to evaluate
     * @return {@code true} if the player has the permission; otherwise {@code false}
     */
    public static boolean hasPermission(@NotNull UUID playerUUID, @NotNull PermissionNode<Boolean> node) {
        if (PermissionAPI.getRegisteredNodes().contains(node)) {
            return PermissionAPI.getOfflinePermission(playerUUID, node);
        }

        ExtraLib.getLogger().error("No permission found for node: {}.", node);
        return false;
    }

    /**
     * Calculates the minimum allowed value for the given player based on a collection of permission nodes.
     * <p>
     * Each permission node must end with an integer suffix (e.g. {@code "example.limit.10"}).
     * If the player has the permission, that suffix is considered a candidate minimum.
     *
     * @param value    the initial value to compare against
     * @param player   the player whose permissions are evaluated
     * @param nodeList a set of permission nodes containing numeric suffixes
     * @return the minimum numeric suffix among permissions the player has, or the original value if unchanged
     */
    public static int minValue(int value, @NotNull ServerPlayer player, @NotNull Set<PermissionNode<Boolean>> nodeList) {
        for (PermissionNode<Boolean> permission : nodeList) {
            if (UtilPermission.hasPermission(player, permission)) {
                value = Math.min(value, Integer.parseInt(permission.getNodeName().substring(permission.getNodeName().lastIndexOf('.') + 1)));
            }
        }
        return value;
    }

    /**
     * Calculates the maximum allowed value for the given player based on a collection of permission nodes.
     * <p>
     * Each permission node must end with an integer suffix (e.g. {@code "example.limit.50"}).
     * If the player has the permission, that suffix is considered a candidate maximum.
     *
     * @param value    the initial value to compare against
     * @param player   the player whose permissions are evaluated
     * @param nodeList a set of permission nodes containing numeric suffixes
     * @return the maximum numeric suffix among permissions the player has, or the original value if unchanged
     */
    public static int maxValue(int value, @NotNull ServerPlayer player, @NotNull Set<PermissionNode<Boolean>> nodeList) {
        for (PermissionNode<Boolean> permission : nodeList) {
            if (UtilPermission.hasPermission(player, permission)) {
                value = Math.max(value, Integer.parseInt(permission.getNodeName().substring(permission.getNodeName().lastIndexOf('.') + 1)));
            }
        }
        return value;
    }

    /**
     * Calculates the minimum allowed value for an offline or online player (identified by UUID),
     * based on a collection of permission nodes.
     * <p>
     * Each permission node must end with an integer suffix (e.g. {@code "example.limit.5"}).
     *
     * @param value      the initial value to compare
     * @param playerUUID the UUID of the player
     * @param nodeList   a set of permission nodes containing numeric suffixes
     * @return the minimum value allowed based on player permissions
     */
    public static int minValue(int value, @NotNull UUID playerUUID, @NotNull Set<PermissionNode<Boolean>> nodeList) {
        for (PermissionNode<Boolean> permission : nodeList) {
            if (UtilPermission.hasPermission(playerUUID, permission)) {
                value = Math.min(value, Integer.parseInt(permission.getNodeName().substring(permission.getNodeName().lastIndexOf('.') + 1)));
            }
        }
        return value;
    }

    /**
     * Calculates the maximum allowed value for an offline or online player (identified by UUID),
     * based on a collection of permission nodes.
     * <p>
     * Each permission node must end with an integer suffix (e.g. {@code "example.limit.100"}).
     *
     * @param value      the initial value to compare
     * @param playerUUID the UUID of the player
     * @param nodeList   a set of permission nodes containing numeric suffixes
     * @return the maximum value allowed based on player permissions
     */
    public static int maxValue(int value, @NotNull UUID playerUUID, @NotNull Set<PermissionNode<Boolean>> nodeList) {
        for (PermissionNode<Boolean> permission : nodeList) {
            if (UtilPermission.hasPermission(playerUUID, permission)) {
                value = Math.max(value, Integer.parseInt(permission.getNodeName().substring(permission.getNodeName().lastIndexOf('.') + 1)));
            }
        }
        return value;
    }

    /**
     * Creates a new boolean permission node from a full node string.
     * <p>
     * The string must contain at least one dot. The part before the first dot is treated as the
     * permission domain (e.g. mod ID), the rest becomes the permission path.
     * <p>
     * Example: {@code "mymod.command.fly"} will produce a node with:
     * <ul>
     *   <li>domain: {@code "mymod"}</li>
     *   <li>path: {@code "command.fly"}</li>
     * </ul>
     *
     * @param nodeName the full permission node string
     * @return a new boolean PermissionNode with default value {@code false}
     */
    @NotNull
    public static PermissionNode<Boolean> getPermissionNode(@NotNull String nodeName) {
        String[] nodeSplit = nodeName.split("\\.", 2);

        return new PermissionNode<>(nodeSplit[0], nodeSplit[1], PermissionTypes.BOOLEAN,
                (player, uuid, permissionDynamicContexts) -> false);
    }
}