package com.vecoo.extralib.util;

import com.vecoo.extralib.ExtraLib;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public final class WorldUtil {
    private WorldUtil() {
    }

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
            if (level.dimension().location().getPath().equals(levelName.toLowerCase(Locale.ROOT))) {
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

    /**
     * Converts a biome ID into a human-readable biome name.
     * For example, "minecraft:dark_forest" becomes "Dark Forest".
     *
     * @param biomeId the biome ID in the format "namespace:biome_name"
     * @return a formatted, human-readable biome name with each word capitalized
     */
    @NotNull
    public static String formatBiomeName(@NotNull String biomeId) {
        String[] split = biomeId.split(":");
        StringBuilder name = new StringBuilder();

        if (split[1].contains("_")) {
            String[] fullName = split[1].split("_");

            for (String s : fullName) {
                String word = s.substring(0, 1).toUpperCase() + s.substring(1);
                name.append(word).append(" ");
            }

            name = new StringBuilder(name.substring(0, name.length() - 1));
        } else {
            name = new StringBuilder(split[1].substring(0, 1).toUpperCase() + split[1].substring(1));
        }

        return name.toString();
    }

    /**
     * Calculates the three-dimensional boundaries of the level as an {@link AABB}.
     * <p>
     * This combines the horizontal limits defined by the {@link WorldBorder} with the
     * vertical build height limits of the specific dimension.
     *
     * @param level the level whose boundaries are being calculated. Must not be null.
     * @return a new {@link AABB} representing the total playable volume of the world.
     */
    @NotNull
    public static AABB getWorldBounds(@NotNull Level level) {
        WorldBorder worldBorder = level.getWorldBorder();

        return new AABB(worldBorder.getMinX(), level.getMinBuildHeight(), worldBorder.getMinZ(),
                worldBorder.getMaxX(), level.getMaxBuildHeight(), worldBorder.getMaxZ());
    }
}