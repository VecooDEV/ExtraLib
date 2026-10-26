package com.vecoo.extralib.mixin;

import net.minecraft.server.players.CachedUserNameToIdResolver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.Map;
import java.util.UUID;

@Mixin(CachedUserNameToIdResolver.class)
public interface GameProfileCacheAccessor {
    @Accessor("profilesByUUID")
    Map<UUID, ?> extraLib$getProfilesByUUID();
}
