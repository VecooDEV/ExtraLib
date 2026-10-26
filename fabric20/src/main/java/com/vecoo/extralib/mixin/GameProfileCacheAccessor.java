package com.vecoo.extralib.mixin;

import net.minecraft.server.players.GameProfileCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.Map;
import java.util.UUID;

@Mixin(GameProfileCache.class)
public interface GameProfileCacheAccessor {
    @Accessor("profilesByUUID")
    Map<UUID, ?> extraLib$getProfilesByUUID();
}
