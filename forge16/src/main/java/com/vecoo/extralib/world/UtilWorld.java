package com.vecoo.extralib.world;

import net.minecraft.block.Block;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.FolderName;

public class UtilWorld {
    public static ServerWorld getWorldByName(String worldName, MinecraftServer server) {
        for (ServerWorld world : server.getAllLevels()) {
            if (world.dimension().location().getPath().equals(worldName.toLowerCase())) {
                return world;
            }
        }
        return null;
    }

    public static String worldDirectory(String file, MinecraftServer server) {
        if (server.isDedicatedServer()) {
            return file.replace("%directory%", "world");
        } else {
            String directory = server.getWorldPath(new FolderName("")).toString().replace("\\", "/");
            return file.replace("%directory%", "saves/" + directory.substring(directory.lastIndexOf("/") + 1));
        }
    }

    public static int countBlocksInChunk(IChunk chunk, Block block) {
        int blockCount = 0;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < chunk.getMaxBuildHeight(); y++) {
                    if (chunk.getBlockState(new BlockPos(x + chunk.getPos().x * 16, y, z + chunk.getPos().z * 16)).getBlock().equals(block)) {
                        blockCount++;
                    }
                }
            }
        }
        return blockCount;
    }

    public static int countBlocksInChunk(IChunk chunk, String tag) {
        int blockCount = 0;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < chunk.getMaxBuildHeight(); y++) {
                    if (BlockTags.bind(tag).contains(chunk.getBlockState(new BlockPos(x + chunk.getPos().x * 16, y, z + chunk.getPos().z * 16)).getBlock())) {
                        blockCount++;
                    }
                }
            }
        }
        return blockCount;
    }
}