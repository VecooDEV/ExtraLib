package com.vecoo.extralib.permissiontest;

import com.mojang.logging.LogUtils;
import com.vecoo.extralib.util.PermissionUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.server.permission.PermissionAPI;
import net.neoforged.neoforge.server.permission.events.PermissionGatherEvent;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Mod("extralib_permission_test")
public class PermissionTestMod implements PermissionTest.Bridge {
    private final Map<String, PermissionNode<Boolean>> nodes = new LinkedHashMap<>();
    private final Set<PermissionNode<Boolean>> limits;

    public PermissionTestMod() {
        for (String node : Set.of("unset", "default", "allow", "deny", "inherited", "limit.5", "limit.20", "limit.50")) {
            this.nodes.put(node, PermissionUtil.getPermissionNode("extralibtest." + node, node.equals("default") || node.equals("deny")));
        }

        this.limits = Set.of(this.nodes.get("limit.5"), this.nodes.get("limit.20"), this.nodes.get("limit.50"));
        PermissionTest test = new PermissionTest(this);
        NeoForge.EVENT_BUS.addListener((PermissionGatherEvent.Nodes event) -> event.addNodes(this.nodes.values().toArray(PermissionNode<?>[]::new)));
        NeoForge.EVENT_BUS.addListener((ServerTickEvent.Post event) -> test.tick(event.getServer()));
    }

    @Override
    public boolean online(ServerPlayer player, String node) {
        return PermissionUtil.hasPermission(player, this.nodes.get(node));
    }

    @Override
    public boolean source(CommandSourceStack source, String node) {
        return PermissionUtil.hasPermission(source, this.nodes.get(node));
    }

    @Override
    public CompletableFuture<Boolean> offline(UUID uuid, String node) {
        return CompletableFuture.completedFuture(PermissionUtil.hasPermission(uuid, this.nodes.get(node)));
    }

    @Override
    public int minimum(ServerPlayer player) {
        return PermissionUtil.minValue(100, player, this.limits);
    }

    @Override
    public int maximum(ServerPlayer player) {
        return PermissionUtil.maxValue(0, player, this.limits);
    }

    @Override
    public CompletableFuture<Integer> minimum(UUID uuid) {
        return CompletableFuture.completedFuture(PermissionUtil.minValue(100, uuid, this.limits));
    }

    @Override
    public CompletableFuture<Integer> maximum(UUID uuid) {
        return CompletableFuture.completedFuture(PermissionUtil.maxValue(0, uuid, this.limits));
    }

    @Override
    public void details() {
        LogUtils.getLogger().info("PERMISSION_TEST handler={}", PermissionAPI.getActivePermissionHandler());
    }
}
