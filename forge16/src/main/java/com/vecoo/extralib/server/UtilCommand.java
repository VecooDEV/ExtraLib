package com.vecoo.extralib.server;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandSource;

import javax.annotation.Nonnull;
import java.util.Collection;

public final class UtilCommand {
    @Nonnull
    public static SuggestionProvider<CommandSource> suggestOnlinePlayers() {
        return (context, builder) -> {
            for (String playerName : context.getSource().getOnlinePlayerNames()) {
                if (playerName.toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                    builder.suggest(playerName);
                }
            }

            return builder.buildFuture();
        };
    }

    @Nonnull
    public static SuggestionProvider<CommandSource> suggestString(@Nonnull Collection<String> collection) {
        return (context, builder) -> {
            for (String name : collection) {
                builder.suggest(name);
            }

            return builder.buildFuture();
        };
    }

    @Nonnull
    public static SuggestionProvider<CommandSource> suggestAmount(@Nonnull Collection<Integer> collection) {
        return (context, builder) -> {
            for (int amount : collection) {
                builder.suggest(amount);
            }

            return builder.buildFuture();
        };
    }
}
