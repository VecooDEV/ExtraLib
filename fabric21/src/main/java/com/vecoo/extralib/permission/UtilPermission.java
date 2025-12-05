package com.vecoo.extralib.permission;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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
    public static boolean hasPermission(@NotNull CommandSourceStack source, @NotNull String node) {
        try {
            return Permissions.check(source, node) || source.getPlayerOrException().hasPermissions(4);
        } catch (CommandSyntaxException e) {
            return true;
        }
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
    public static boolean hasPermission(@NotNull ServerPlayer player, @NotNull String node) {
        return Permissions.check(player, node) || player.hasPermissions(4);
    }

    /**
     * Checks whether an offline or online player (identified by UUID) has the specified permission.
     * If the node is not registered, an error is logged and {@code false} is returned.
     *
     * @param playerUUID the UUID of the player
     * @param node       the permission node to evaluate
     * @return {@code true} if the player has the permission; otherwise {@code false}
     */
    @NotNull
    public static CompletableFuture<Boolean> hasPermission(@NotNull UUID playerUUID, @NotNull String node) {
        return Permissions.check(playerUUID, node);
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
    public static int minValue(int value, @NotNull ServerPlayer player, @NotNull Set<String> nodeList) {
        for (String permission : nodeList) {
            if (UtilPermission.hasPermission(player, permission)) {
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
     * @param value    the initial value to compare against
     * @param player   the player whose permissions are evaluated
     * @param nodeList a set of permission nodes containing numeric suffixes
     * @return the maximum numeric suffix among permissions the player has, or the original value if unchanged
     */
    public static int maxValue(int value, @NotNull ServerPlayer player, @NotNull Set<String> nodeList) {
        for (String permission : nodeList) {
            if (UtilPermission.hasPermission(player, permission)) {
                value = Math.max(value, Integer.parseInt(permission.substring(permission.lastIndexOf('.') + 1)));
            }
        }
        return value;
    }
}