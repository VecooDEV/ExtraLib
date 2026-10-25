package com.vecoo.extralib.permissiontest;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.InheritanceNode;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class LuckPermsSetup {
    public static CompletableFuture<Void> prepare(UUID uuid) {
        var api = LuckPermsProvider.get();
        return api.getGroupManager().createAndLoadGroup("extralibtest").thenCompose(group -> {
            group.data().clear();
            group.data().add(Node.builder("extralibtest.inherited").build());
            group.getCachedData().invalidate();
            return api.getGroupManager().saveGroup(group);
        }).thenCompose(ignored -> api.getUserManager().loadUser(uuid)).thenCompose(user -> {
            user.data().clear();
            user.data().add(Node.builder("extralibtest.allow").build());
            user.data().add(Node.builder("extralibtest.deny").value(false).build());
            user.data().add(Node.builder("extralibtest.limit.5").build());
            user.data().add(Node.builder("extralibtest.limit.20").build());
            user.data().add(Node.builder("extralibtest.limit.50").value(false).build());
            user.data().add(InheritanceNode.builder("extralibtest").build());
            user.getCachedData().invalidate();
            return api.getUserManager().saveUser(user);
        });
    }

}
