package com.vecoo.extralib.server;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class UtilCommand {
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

    @NotNull
    public static SuggestionProvider<CommandSourceStack> suggestString(@NotNull Collection<String> collection) {
        return (context, builder) -> {
            for (String name : collection) {
                builder.suggest(name);
            }

            return builder.buildFuture();
        };
    }

    @NotNull
    public static SuggestionProvider<CommandSourceStack> suggestOnlinePlayers(@NotNull Collection<Integer> collection) {
        return (context, builder) -> {
            for (int amount : collection) {
                builder.suggest(amount);
            }

            return builder.buildFuture();
        };
    }
}
