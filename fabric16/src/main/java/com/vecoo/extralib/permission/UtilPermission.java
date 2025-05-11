package com.vecoo.extralib.permission;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.vecoo.extralib.ExtraLib;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.UUID;

public class UtilPermission {
    public static boolean hasPermission(CommandSourceStack source, String node, int levelPermission) {
        if (!ExtraLib.isLuckpermsLoaded()) {
            return source.hasPermission(levelPermission);
        }

        try {
            return LuckPermsProvider.get().getPlayerAdapter(ServerPlayer.class).getUser(source.getPlayerOrException()).getNodes().contains(Node.builder(node).build()) || source.hasPermission(4);
        } catch (CommandSyntaxException e) {
            return true;
        }
    }

    public static boolean hasPermission(ServerPlayer player, String node, int levelPermission) {
        if (!ExtraLib.isLuckpermsLoaded()) {
            return player.hasPermissions(levelPermission);
        }

        return LuckPermsProvider.get().getPlayerAdapter(ServerPlayer.class).getUser(player).getNodes().contains(Node.builder(node).build()) || player.hasPermissions(4);
    }

    public static boolean hasPermission(UUID playerUUID, String node) {
        if (!ExtraLib.isLuckpermsLoaded()) {
            return false;
        }

        User user = LuckPermsProvider.get().getUserManager().getUser(playerUUID);

        if (user == null) {
            return false;
        }

        return user.getNodes().contains(Node.builder(node).build());
    }

    public static int minValue(int value, UUID playerUUID, List<String> permissionList) {
        for (String permission : permissionList) {
            if (UtilPermission.hasPermission(playerUUID, permission)) {
                value = Math.min(value, Integer.parseInt(permission.substring(permission.lastIndexOf('.') + 1)));
            }
        }
        return value;
    }

    public static int maxValue(int value, UUID playerUUID, List<String> permissionList) {
        for (String permission : permissionList) {
            if (UtilPermission.hasPermission(playerUUID, permission)) {
                value = Math.max(value, Integer.parseInt(permission.substring(permission.lastIndexOf('.') + 1)));
            }
        }
        return value;
    }
}