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
    public static boolean hasPermission(@NotNull CommandSourceStack source, @NotNull String node) {
        try {
            return Permissions.check(source, node) || source.getPlayerOrException().hasPermissions(4);
        } catch (CommandSyntaxException e) {
            return true;
        }
    }

    public static boolean hasPermission(@NotNull ServerPlayer player, @NotNull String node) {
        return Permissions.check(player, node) || player.hasPermissions(4);
    }

    @NotNull
    public static CompletableFuture<Boolean> hasPermission(@NotNull UUID playerUUID, @NotNull String node) {
        return Permissions.check(playerUUID, node);
    }

    public static int minValue(int value, @NotNull ServerPlayer player, @NotNull Set<String> permissionList) {
        for (String permission : permissionList) {
            if (UtilPermission.hasPermission(player, permission)) {
                value = Math.min(value, Integer.parseInt(permission.substring(permission.lastIndexOf('.') + 1)));
            }
        }
        return value;
    }

    public static int maxValue(int value, @NotNull ServerPlayer player, @NotNull Set<String> permissionList) {
        for (String permission : permissionList) {
            if (UtilPermission.hasPermission(player, permission)) {
                value = Math.max(value, Integer.parseInt(permission.substring(permission.lastIndexOf('.') + 1)));
            }
        }
        return value;
    }
}