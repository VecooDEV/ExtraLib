package com.vecoo.extralib.util;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class PermissionUtil {
    private PermissionUtil() {
    }

    /** Checks a player's permission, or permits a non-player command source. */
    public static boolean hasPermission(@Nullable CommandSourceStack source, @NotNull String node) {
        return hasPermission(source, node, false);
    }

    /** Uses defaultValue only when the permission provider has no result for this node. */
    public static boolean hasPermission(@Nullable CommandSourceStack source, @NotNull String node, boolean defaultValue) {
        if (source == null) {
            return false;
        }

        ServerPlayer player = source.getPlayer();

        if (player == null) {
            return true;
        }

        return hasPermission(player, node, defaultValue);
    }

    /** Checks a permission with a default of false. Operator level 4 bypasses the check. */
    public static boolean hasPermission(@Nullable ServerPlayer player, @NotNull String node) {
        return hasPermission(player, node, false);
    }

    /** An explicit provider denial overrides defaultValue. Operator level 4 still bypasses it. */
    public static boolean hasPermission(@Nullable ServerPlayer player, @NotNull String node, boolean defaultValue) {
        if (player == null) {
            return false;
        }

        if (player.permissions().hasPermission(net.minecraft.server.permissions.Permissions.COMMANDS_OWNER)) {
            return true;
        }

        return Permissions.check(player, node, defaultValue);
    }

    /** Checks a UUID asynchronously, with a default of false and no operator bypass. */
    @NotNull
    public static CompletableFuture<Boolean> hasPermission(@NotNull UUID playerUUID, @NotNull String node) {
        return hasPermission(playerUUID, node, false);
    }

    /** Checks a UUID asynchronously, using defaultValue only when the provider has no result. */
    @NotNull
    public static CompletableFuture<Boolean> hasPermission(@NotNull UUID playerUUID, @NotNull String node, boolean defaultValue) {
        return Permissions.check(playerUUID, node, defaultValue);
    }

    /**
     * Calculates the minimum allowed value for the given player based on a collection of permission nodes.
     * <p>
     * Each permission node must end with an integer suffix (e.g. {@code "example.limit.50"}).
     * If the player has the permission, that suffix is considered a candidate minimum.
     *
     * @param value    the initial value to compare against
     * @param player   the player whose permissions are evaluated
     * @param nodeList a set of permission nodes containing numeric suffixes
     * @return the minimum numeric suffix among permissions the player has, or the original value if unchanged
     */
    public static int minValue(int value, @Nullable ServerPlayer player, @NotNull Set<String> nodeList) {
        for (String permission : nodeList) {
            if (PermissionUtil.hasPermission(player, permission)) {
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
    public static int maxValue(int value, @Nullable ServerPlayer player, @NotNull Set<String> nodeList) {
        for (String permission : nodeList) {
            if (PermissionUtil.hasPermission(player, permission)) {
                value = Math.max(value, Integer.parseInt(permission.substring(permission.lastIndexOf('.') + 1)));
            }
        }

        return value;
    }

    /** Resolves the minimum permission value without blocking the server thread. */
    @NotNull
    public static CompletableFuture<Integer> minValue(int value, @NotNull UUID playerUUID, @NotNull Set<String> nodeList) {
        CompletableFuture<Integer> result = CompletableFuture.completedFuture(value);

        for (String permission : nodeList) {
            CompletableFuture<Boolean> allowed = hasPermission(playerUUID, permission);
            result = result.thenCombine(allowed, (current, granted) -> granted ? Math.min(current, Integer.parseInt(permission.substring(permission.lastIndexOf('.') + 1))) : current);
        }

        return result;
    }


    /** Resolves the maximum permission value without blocking the server thread. */
    @NotNull
    public static CompletableFuture<Integer> maxValue(int value, @NotNull UUID playerUUID, @NotNull Set<String> nodeList) {
        CompletableFuture<Integer> result = CompletableFuture.completedFuture(value);

        for (String permission : nodeList) {
            CompletableFuture<Boolean> allowed = hasPermission(playerUUID, permission);
            result = result.thenCombine(allowed, (current, granted) -> granted ? Math.max(current, Integer.parseInt(permission.substring(permission.lastIndexOf('.') + 1))) : current);
        }

        return result;
    }

}
