package com.vecoo.extralib.permissiontest;

import com.mojang.logging.LogUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.ServerOpListEntry;
import org.slf4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class PermissionTest {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Bridge bridge;
    private final boolean luckPerms = Boolean.getBoolean("extralib.permissions.luckperms");
    private final Map<String, Boolean> results = new LinkedHashMap<>();
    private boolean started;

    public PermissionTest(Bridge bridge) {
        this.bridge = bridge;
    }

    public void tick(MinecraftServer server) {
        ServerPlayer player = server.getPlayerList().getPlayerByName("ExtraPermTest");
        if (this.started || player == null) {
            return;
        }

        this.started = true;
        UUID uuid = player.getUUID();
        CompletableFuture<Void> setup = this.luckPerms ? LuckPermsSetup.prepare(uuid) : CompletableFuture.completedFuture(null);

        setup.thenRunAsync(() -> {
            check("null player", this.bridge.online(null, "deny"), false);
            check("null source", this.bridge.source(null, "deny"), false);
            check("console", this.bridge.source(server.createCommandSourceStack(), "deny"), true);
            check("unset false", this.bridge.online(player, "unset"), false);
            check("unset true", this.bridge.online(player, "default"), true);
            check("explicit allow", this.bridge.online(player, "allow"), this.luckPerms);
            check("explicit deny overrides default", this.bridge.online(player, "deny"), !this.luckPerms);
            check("command source allow", this.bridge.source(player.createCommandSourceStack(), "allow"), this.luckPerms);
            check("command source deny", this.bridge.source(player.createCommandSourceStack(), "deny"), !this.luckPerms);
            check("group inheritance", this.bridge.online(player, "inherited"), this.luckPerms);
            check("online min", this.bridge.minimum(player) == (this.luckPerms ? 5 : 100), true);
            check("online max", this.bridge.maximum(player) == (this.luckPerms ? 20 : 0), true);

            server.getPlayerList().getOps().add(new ServerOpListEntry(player.getGameProfile(), 2, false));
            check("OP 2 does not bypass", this.bridge.online(player, "unset"), false);
            server.getPlayerList().getOps().add(new ServerOpListEntry(player.getGameProfile(), 4, false));
            check("OP 4 bypass", this.bridge.online(player, "unset"), true);
            check("OP 4 explicit deny bypass", this.bridge.online(player, "deny"), true);
            server.getPlayerList().getOps().remove(player.getGameProfile());
            this.bridge.details();
        }, server).thenCompose(ignored -> this.bridge.offline(uuid, "unset").thenAccept(value -> check("UUID unset false", value, false)))
                .thenCompose(ignored -> this.bridge.offline(uuid, "default").thenAccept(value -> check("UUID unset true", value, true)))
                .thenCompose(ignored -> this.bridge.offline(uuid, "allow").thenAccept(value -> check("UUID explicit allow", value, this.luckPerms)))
                .thenCompose(ignored -> this.bridge.offline(uuid, "deny").thenAccept(value -> check("UUID explicit deny", value, !this.luckPerms)))
                .thenCompose(ignored -> this.bridge.minimum(uuid).thenAccept(value -> check("UUID min", value == (this.luckPerms ? 5 : 100), true)))
                .thenCompose(ignored -> this.bridge.maximum(uuid).thenAccept(value -> check("UUID max", value == (this.luckPerms ? 20 : 0), true)))
                .thenCompose(ignored -> this.bridge.offline(UUID.fromString("8e0534a0-36dc-4473-aac9-97d2511c0f29"), "unset")
                        .thenAccept(value -> check("unknown UUID", value, false)))
                .orTimeout(45, java.util.concurrent.TimeUnit.SECONDS)
                .whenComplete((ignored, error) -> server.execute(() -> finish(server, error)));
    }

    private void check(String name, boolean actual, boolean expected) {
        this.results.put(name, actual == expected);
        LOGGER.info("PERMISSION_TEST {}: actual={}, expected={}", name, actual, expected);
    }

    private void finish(MinecraftServer server, Throwable error) {
        if (error != null) {
            LOGGER.error("PERMISSION_TEST failed", error);
            this.results.put("exception", false);
        }

        boolean passed = !this.results.isEmpty() && this.results.values().stream().allMatch(Boolean::booleanValue);

        try {
            Files.writeString(Path.of("permission-results.txt"), "passed=" + passed + "\nluckperms=" + this.luckPerms + "\n" + this.results);
        } catch (Exception e) {
            LOGGER.error("Unable to write permission test results", e);
        }

        LOGGER.info("PERMISSION_TEST_RESULT passed={} checks={}", passed, this.results.size());
        server.halt(false);
    }

    public interface Bridge {
        boolean online(ServerPlayer player, String node);

        boolean source(CommandSourceStack source, String node);

        CompletableFuture<Boolean> offline(UUID uuid, String node);

        int minimum(ServerPlayer player);

        int maximum(ServerPlayer player);

        CompletableFuture<Integer> minimum(UUID uuid);

        CompletableFuture<Integer> maximum(UUID uuid);

        void details();
    }
}

