package com.vecoo.extralib.world;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.storage.LevelResource;

public class UtilWorld {
    public static ServerLevel getWorldByName(String worldName, MinecraftServer server) {
        for (ServerLevel world : server.getAllLevels()) {
            if (world.dimension().location().getPath().equals(worldName.toLowerCase())) {
                return world;
            }
        }
        return null;
    }

    public static String worldDirectory(String file, MinecraftServer server) {
        if (server.isDedicatedServer()) {
            return file.replace("%directory%", "world");
        }

        String directory = server.getWorldPath(new LevelResource("")).toString().replace("\\", "/");
        return file.replace("%directory%", "saves/" + directory.substring(directory.lastIndexOf("/") + 1));
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