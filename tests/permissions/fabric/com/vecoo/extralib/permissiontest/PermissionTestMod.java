package com.vecoo.extralib.permissiontest;

import com.mojang.logging.LogUtils;
import com.vecoo.extralib.util.PermissionUtil;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PermissionTestMod implements ModInitializer, PermissionTest.Bridge {
    private static final Set<String> LIMITS = Set.of("extralibtest.limit.5", "extralibtest.limit.20", "extralibtest.limit.50");

    @Override
    public void onInitialize() {
        PermissionTest test = new PermissionTest(this);
        ServerTickEvents.END_SERVER_TICK.register(test::tick);
    }

    private boolean defaultValue(String node) {
        return node.equals("default") || node.equals("deny");
    }

    @Override
    public boolean online(ServerPlayer player, String node) {
        return PermissionUtil.hasPermission(player, "extralibtest." + node, defaultValue(node));
    }

    @Override
    public boolean source(CommandSourceStack source, String node) {
        return PermissionUtil.hasPermission(source, "extralibtest." + node, defaultValue(node));
    }

    @Override
    public CompletableFuture<Boolean> offline(UUID uuid, String node) {
        return PermissionUtil.hasPermission(uuid, "extralibtest." + node, defaultValue(node));
    }

    @Override
    public int minimum(ServerPlayer player) {
        return PermissionUtil.minValue(100, player, LIMITS);
    }

    @Override
    public int maximum(ServerPlayer player) {
        return PermissionUtil.maxValue(0, player, LIMITS);
    }

    @Override
    public CompletableFuture<Integer> minimum(UUID uuid) {
        return PermissionUtil.minValue(100, uuid, LIMITS);
    }

    @Override
    public CompletableFuture<Integer> maximum(UUID uuid) {
        return PermissionUtil.maxValue(0, uuid, LIMITS);
    }

    @Override
    public void details() {
        LogUtils.getLogger().info("PERMISSION_TEST API={}", me.lucko.fabric.api.permissions.v0.Permissions.class.getProtectionDomain().getCodeSource());
    }
}
