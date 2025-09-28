package com.vecoo.extralib.world;

import com.vecoo.extralib.ExtraLib;
import net.minecraft.block.Block;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.FolderName;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class UtilWorld {
    @Nullable
    public static ServerWorld getWorldByName(@Nonnull String worldName) {
        for (ServerWorld world : ExtraLib.getInstance().getServer().getAllLevels()) {
            if (world.dimension().location().getPath().equals(worldName.toLowerCase())) {
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

        return file.replace("%directory%", "saves/" + server.getWorldPath(FolderName.LEVEL_DATA_FILE).normalize().getParent().getFileName().toString());
    }

    public static int countBlocksInChunk(@Nonnull IChunk chunk, @Nonnull Block targetBlock) {
        int[] count = {0};

        for (ChunkSection section : chunk.getSections()) {
            if (section == null || section.isEmpty()) {
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