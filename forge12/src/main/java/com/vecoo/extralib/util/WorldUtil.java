package com.vecoo.extralib.util;

import com.vecoo.extralib.ExtraLib;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public final class WorldUtil {
    private WorldUtil() {
    }

    /**
     * Finds for a world by its name.
     *
     * <p>The world name should be provided **without a namespace** (e.g. "overworld").
     * The method compares the name with the path part of the dimension ID.</p>
     *
     * @param levelName the level name without a namespace (must not be null)
     * @return the found {@link WorldServer }, or {@code null} if none match
     */
    @Nullable
    public static WorldServer findLevelByName(@NotNull String levelName) {
        for (WorldServer level : ExtraLib.getInstance().getServer().worlds) {
            if (level.getWorldInfo().getWorldName().equals(levelName.toLowerCase(Locale.ROOT))) {
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

        return file.replace("%directory%", "saves/" + server.getFolderName());
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
}