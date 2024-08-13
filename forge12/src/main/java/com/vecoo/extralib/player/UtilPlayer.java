package com.vecoo.extralib.player;

import net.minecraftforge.common.UsernameCache;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class UtilPlayer {
    private static Map<String, UUID> username = UtilPlayer.invertMap(UsernameCache.getMap());

    public static <K, V> Map<V, K> invertMap(Map<K, V> map) {
        return map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    }

    public static UUID getUUID(String player) {
        return username.get(player);
    }

    public static boolean hasUUID(String player) {
        return username.containsKey(player);
    }
}