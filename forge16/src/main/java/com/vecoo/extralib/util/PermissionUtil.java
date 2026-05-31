package com.vecoo.extralib.util;

import com.mojang.authlib.GameProfile;
import com.vecoo.extralib.ExtraLib;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.context.Context;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public final class PermissionUtil {
    private PermissionUtil() {
    }

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
    public static boolean hasPermission(@Nullable CommandSource source, @NotNull String node) {
        if (source == null) {
            return false;
        }

        Entity entity = source.getEntity();

        if (!(entity instanceof ServerPlayerEntity)) {
            return true;
        }

        return hasPermission((ServerPlayerEntity) entity, node);
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
    public static boolean hasPermission(@Nullable ServerPlayerEntity player, @NotNull String node) {
        if (player == null) {
            return false;
        }

        if (PermissionAPI.getPermissionHandler().getRegisteredNodes().contains(node)) {
            return PermissionAPI.hasPermission(player, node) || player.hasPermissions(4);
        }

        ExtraLib.getLogger().error("No permission found for node: {}.", node);
        return false;
    }

    public static boolean hasPermission(@NotNull UUID playerUUID, @NotNull String playerName, @NotNull String node) {
        if (PermissionAPI.getPermissionHandler().getRegisteredNodes().contains(node)) {
            return PermissionAPI.hasPermission(new GameProfile(playerUUID, playerName), node, new Context());
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
     * @param value          the initial value to compare against
     * @param player         the player whose permissions are evaluated
     * @param permissionList a set of permission nodes containing numeric suffixes
     * @return the minimum numeric suffix among permissions the player has, or the original value if unchanged
     */
    public static int minValue(int value, @Nullable ServerPlayerEntity player, @NotNull Set<String> permissionList) {
        for (String permission : permissionList) {
            if (hasPermission(player, permission)) {
                value = Math.min(value, Integer.parseInt(permission.substring(permission.lastIndexOf('.') + 1)));
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
     * @param value          the initial value to compare against
     * @param player         the player whose permissions are evaluated
     * @param permissionList a set of permission nodes containing numeric suffixes
     * @return the maximum numeric suffix among permissions the player has, or the original value if unchanged
     */
    public static int maxValue(int value, @Nullable ServerPlayerEntity player, @NotNull Set<String> permissionList) {
        for (String permission : permissionList) {
            if (hasPermission(player, permission)) {
                value = Math.max(value, Integer.parseInt(permission.substring(permission.lastIndexOf('.') + 1)));
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
     * @param value          the initial value to compare
     * @param playerUUID     the UUID of the player
     * @param permissionList a set of permission nodes containing numeric suffixes
     * @return the minimum value allowed based on player permissions
     */
    public static int minValue(int value, @NotNull UUID playerUUID, @NotNull String playerName, @NotNull Set<String> permissionList) {
        for (String permission : permissionList) {
            if (hasPermission(playerUUID, playerName, permission)) {
                value = Math.min(value, Integer.parseInt(permission.substring(permission.lastIndexOf('.') + 1)));
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
     * @param value          the initial value to compare
     * @param playerUUID     the UUID of the player
     * @param permissionList a set of permission nodes containing numeric suffixes
     * @return the maximum value allowed based on player permissions
     */
    public static int maxValue(int value, @NotNull UUID playerUUID, @NotNull String playerName, @NotNull Set<String> permissionList) {
        for (String permission : permissionList) {
            if (hasPermission(playerUUID, playerName, permission)) {
                value = Math.max(value, Integer.parseInt(permission.substring(permission.lastIndexOf('.') + 1)));
            }
        }

        return value;
    }
}