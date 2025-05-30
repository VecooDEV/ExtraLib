package com.vecoo.extralib.world;

import com.vecoo.extralib.ExtraLib;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.storage.LevelResource;

public class UtilWorld {
    @Deprecated
    public static ServerLevel getWorldByName(String worldName, MinecraftServer server) {
        for (ServerLevel world : server.getAllLevels()) {
            if (world.dimension().location().getPath().equals(worldName.toLowerCase())) {
                return world;
            }
        }
        return null;
    }

    public static ServerLevel getLevelByName(String levelName) {
        for (ServerLevel world : ExtraLib.getInstance().getServer().getAllLevels()) {
            if (world.dimension().location().getPath().equals(levelName.toLowerCase())) {
                return world;
            }
        }
        return null;
    }

    public static String worldDirectory(String file, MinecraftServer server) {
        if (server.isDedicatedServer()) {
            return file.replace("%directory%", "world");
        }

        return file.replace("%directory%", "saves/" + server.getWorldPath(LevelResource.LEVEL_DATA_FILE).normalize().getParent().getFileName().toString());
    }

    public static int countBlocksInChunk(LevelChunk chunk, Block targetBlock) {
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