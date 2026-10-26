package com.vecoo.extralib.functionaltest;

import com.vecoo.extralib.util.PacketUtil;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class FunctionalTestMod implements ModInitializer {
    private boolean packetsSent;

    @Override
    public void onInitialize() {
        FunctionalTest test = new FunctionalTest();
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            ServerPlayer player = server.getPlayerList().getPlayerByName("ExtraLibTest");

            if (!this.packetsSent && player != null) {
                this.packetsSent = true;
                PacketUtil.sendToPlayer(player, new ProbePacket("player"));
                PacketUtil.sendToPlayer((Player) player, new ProbePacket("generic"));
                PacketUtil.sendToPlayer(player.getUUID(), new ProbePacket("uuid"));
                PacketUtil.sendToPlayer(player.getGameProfile().getName(), new ProbePacket("name"));
                PacketUtil.sendToAllPlayers(new ProbePacket("all"));
                PacketUtil.sendToPlayersInDimension(player.serverLevel(), new ProbePacket("dimension"));
                PacketUtil.sendToPlayer(player, new ProbePacket("first"), new ProbePacket("second"));
                PacketUtil.sendToPlayer((ServerPlayer) null, new ProbePacket("unexpected-null-player"));
                PacketUtil.sendToPlayer((UUID) null, new ProbePacket("unexpected-null-uuid"));
                PacketUtil.sendToPlayer((String) null, new ProbePacket("unexpected-null-name"));
                PacketUtil.sendToPlayer("MissingTestPlayer", new ProbePacket("unexpected-missing-player"));
            }

            test.tick(server);
        });
    }

    private record ProbePacket(String value) implements FabricPacket {
        private static final PacketType<ProbePacket> TYPE = PacketType.create(new ResourceLocation("extralib", "functional"), buffer -> new ProbePacket(buffer.readUtf()));

        @Override
        public void write(FriendlyByteBuf buffer) {
            buffer.writeUtf(this.value);
        }

        @Override
        public PacketType<?> getType() {
            return TYPE;
        }
    }
}
