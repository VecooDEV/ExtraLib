package com.vecoo.extralib.world;

import com.vecoo.extralib.ExtraLib;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.storage.LevelResource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UtilWorld {
    @Nullable
    public static ServerLevel getLevelByName(@NotNull String levelName) {
        for (ServerLevel level : ExtraLib.getInstance().getServer().getAllLevels()) {
            if (level.dimension().location().getPath().equals(levelName.toLowerCase())) {
                return level;
            }
        }

        return null;
    }

    @NotNull
    public static String worldDirectory(@NotNull String file, @NotNull MinecraftServer server) {
        if (server.isDedicatedServer()) {
            return file.replace("%directory%", "world");
        }

        return file.replace("%directory%", "saves/" + server.getWorldPath(LevelResource.LEVEL_DATA_FILE).normalize().getParent().getFileName().toString());
    }

    public static int countBlocksInChunk(@NotNull LevelChunk chunk, @NotNull Block targetBlock) {
        int[] count = {0};

        for (LevelChunkSection section : chunk.getSections()) {
            if (section == null || section.hasOnlyAir()) {
                continue;
            }

            section.getStates().count((state, amount) -> {
                if (state.is(targetBlock)) {
                    count[0] += amount;
                }
            });
        }

        return count[0];
    }
}