package com.vecoo.extralib.server;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class UtilCommand {
    /**
     * Provides a suggestion provider for online player names.
     * <p>
     * This can be used in commands to suggest names of currently online players
     * that match the user's partially typed input.
     * </p>
     *
     * @return a SuggestionProvider that suggests online player names
     */
    @NotNull
    public static SuggestionProvider<CommandSourceStack> suggestOnlinePlayers() {
        return (context, builder) -> {
            for (String playerName : context.getSource().getOnlinePlayerNames()) {
                if (playerName.toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                    builder.suggest(playerName);
                }
            }

            return builder.buildFuture();
        };
    }

    /**
     * Provides a suggestion provider for a collection of strings.
     * <p>
     * Each string in the provided collection will be suggested regardless of
     * the user's current input.
     * </p>
     *
     * @param collection the collection of strings to suggest
     * @return a SuggestionProvider that suggests the provided strings
     */
    @NotNull
    public static SuggestionProvider<CommandSourceStack> suggestString(@NotNull Collection<String> collection) {
        return (context, builder) -> {
            for (String name : collection) {
                builder.suggest(name);
            }

            return builder.buildFuture();
        };
    }

    /**
     * Provides a suggestion provider for a collection of integers.
     * <p>
     * Each integer in the provided collection will be suggested as a number
     * for command arguments.
     * </p>
     *
     * @param collection the collection of integers to suggest
     * @return a SuggestionProvider that suggests the provided integers
     */
    @NotNull
    public static SuggestionProvider<CommandSourceStack> suggestAmount(@NotNull Collection<Integer> collection) {
        return (context, builder) -> {
            for (int amount : collection) {
                builder.suggest(amount);
            }

            return builder.buildFuture();
        };
    }
}
