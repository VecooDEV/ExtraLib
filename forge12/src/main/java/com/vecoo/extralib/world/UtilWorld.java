package com.vecoo.extralib.world;

import com.vecoo.extralib.ExtraLib;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class UtilWorld {
    @Nullable
    public static WorldServer findWorldByName(@Nonnull String worldName) {
        for (WorldServer world : ExtraLib.getInstance().getServer().worlds) {
            if (world.getWorldInfo().getWorldName().equals(worldName.toLowerCase())) {
                return world;
            }
        }

        return null;
    }

    @Nonnull
    public static String resolveWorldDirectory(@Nonnull String file, @Nonnull MinecraftServer server) {
        if (server.isDedicatedServer()) {
            return file.replace("%directory%", "world");
        }

        return file.replace("%directory%", "saves/" + server.getFolderName());
    }
}