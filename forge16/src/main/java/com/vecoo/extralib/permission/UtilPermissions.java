package com.vecoo.extralib.permission;

import com.vecoo.extralib.ExtraLib;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

import java.util.HashMap;
import java.util.Map;

public class UtilPermissions {
    public static void registerPermission(HashMap<String, Integer> permissionMap) {
        for (Map.Entry<String, Integer> permissions : permissionMap.entrySet()) {
            String permission = permissions.getKey();
            int permissionLevel = permissions.getValue();

            switch (permissionLevel) {
                case 0: {
                    PermissionAPI.registerNode(permission, DefaultPermissionLevel.ALL, "");
                    break;
                }

                case 1:
                case 2:
                case 3:
                case 4: {
                    PermissionAPI.registerNode(permission, DefaultPermissionLevel.OP, "");
                    break;
                }

                default: {
                    PermissionAPI.registerNode(permission, DefaultPermissionLevel.NONE, "");
                    break;
                }
            }
        }
    }

    public static boolean hasPermission(CommandSource source, String node, HashMap<String, Integer> permissionMap) {
        try {
            return PermissionAPI.hasPermission(source.getPlayerOrException(), node) || permissionMap.get(node) == 0;
        } catch (Exception e) {
            ExtraLib.getLogger().error("[ExtraLib] Unknown source of permission.");
        }
        return false;
    }

    public static boolean hasPermission(ServerPlayerEntity player, String node, HashMap<String, Integer> permissionMap) {
        return PermissionAPI.hasPermission(player, node) || permissionMap.get(node) == 0;
    }
}