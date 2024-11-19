package com.vecoo.extralib.player;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.common.UsernameCache;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class UtilPlayer {
    public static UUID getUUID(String player) {
        return UsernameCache.getMap().entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey)).get(player);
    }

    public static boolean hasUUID(String player) {
        return UsernameCache.getMap().entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey)).containsKey(player);
    }

    public static boolean isOp(ServerPlayerEntity player) {
        return player.hasPermissions(2);
    }
}