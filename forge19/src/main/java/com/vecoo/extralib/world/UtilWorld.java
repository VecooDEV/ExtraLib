package com.vecoo.extralib.world;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.registries.ForgeRegistries;

public class UtilWorld {
    public static Level getWorldByName(String worldName, MinecraftServer server) {
        for (Level world : server.getAllLevels()) {
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
            String directory = server.getWorldPath(new LevelResource("")).toString().replace("\\", "/");
            return file.replace("%directory%", "saves/" + directory.substring(directory.lastIndexOf("/") + 1));
        }
    }

    public static int countBlocksInChunk(ChunkAccess chunk, Block block) {
        int blockCount = 0;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = chunk.getMinBuildHeight(); y < chunk.getMaxBuildHeight(); y++) {
                    if (chunk.getBlockState(new BlockPos(x + chunk.getPos().x * 16, y, z + chunk.getPos().z * 16)).getBlock().equals(block)) {
                        blockCount++;
                    }
                }
            }
        }
        return blockCount;
    }

    public static int countBlocksInChunk(ChunkAccess chunk, String tag) {
        int blockCount = 0;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = chunk.getMinBuildHeight(); y < chunk.getMaxBuildHeight(); y++) {
                    if (ForgeRegistries.BLOCKS.tags().getTag(BlockTags.create(new ResourceLocation(tag))).contains(chunk.getBlockState(new BlockPos(x + chunk.getPos().x * 16, y, z + chunk.getPos().z * 16)).getBlock())) {
                        blockCount++;
                    }
                }
            }
        }
        return blockCount;
    }
}