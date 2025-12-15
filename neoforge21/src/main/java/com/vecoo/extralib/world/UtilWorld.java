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

public final class UtilWorld {
    /**
     * Finds for a level by its name.
     *
     * <p>The level name should be provided **without a namespace** (e.g. "overworld").
     * The method compares the name with the path part of the dimension ID.</p>
     *
     * @param levelName the level name without a namespace (must not be null)
     * @return the found {@link ServerLevel}, or {@code null} if none match
     */
    @Nullable
    public static ServerLevel findLevelByName(@NotNull String levelName) {
        for (ServerLevel level : ExtraLib.getInstance().getServer().getAllLevels()) {
            if (level.dimension().location().getPath().equals(levelName.toLowerCase())) {
                return level;
            }
        }

        return null;
    }

    /**
     * Resolves the world directory path depending on whether the server is dedicated
     * or running in singleplayer/integrated mode.
     * <p>
     * Replaces the placeholder {@code %directory%} in the provided file path with:
     * <ul>
     *     <li>{@code world} for dedicated servers</li>
     *     <li>{@code saves/<worldName>} for singleplayer/integrated servers</li>
     * </ul>
     *
     * @param file   the file path template that contains the placeholder {@code %directory%}
     * @param server the current Minecraft server instance
     * @return the resolved file path with the actual world directory substituted
     */
    @NotNull
    public static String resolveWorldDirectory(@NotNull String file, @NotNull MinecraftServer server) {
        if (server.isDedicatedServer()) {
            return file.replace("%directory%", "world");
        }

        return file.replace("%directory%", "saves/" + server.getWorldPath(LevelResource.LEVEL_DATA_FILE)
                .normalize().getParent().getFileName().toString());
    }

    /**
     * Counts the total number of blocks of the specified type inside the given chunk.
     * <p>
     * The method iterates through all chunk sections (skipping empty ones) and sums
     * the occurrences of the target block using the palette’s internal counting logic.
     *
     * @param chunk       the chunk in which to search for the blocks
     * @param targetBlock the block type to count
     * @return the total amount of blocks of the given type in the chunk
     */
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