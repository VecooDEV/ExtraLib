package com.vecoo.extralib.world;

import com.vecoo.extralib.ExtraLib;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

import javax.annotation.Nonnull;

public class UtilWorld {
    @javax.annotation.Nullable
    public static WorldServer getWorldByName(@Nonnull String worldName) {
        for (WorldServer world : ExtraLib.getInstance().getServer().worlds) {
            if (world.getWorldInfo().getWorldName().equalsIgnoreCase(worldName)) {
                return world;
            }
        }

        return null;
    }

    @Nonnull
    public static String worldDirectory(@Nonnull String file, @Nonnull MinecraftServer server) {
        if (server.isDedicatedServer()) {
            return file.replace("%directory%", "world");
        }

        return file.replace("%directory%", "saves/" + server.getFolderName());
    }
}