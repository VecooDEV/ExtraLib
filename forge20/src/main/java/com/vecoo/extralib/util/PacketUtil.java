package com.vecoo.extralib.util;

import com.vecoo.extralib.ExtraLib;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class PacketUtil {
    private PacketUtil() {
    }

    /**
     * Sends one or more packets to a specific server-side player.
     *
     * @param player  the target server player, can be null
     * @param packet  the primary packet to send
     * @param packets additional packets to send in the same batch
     */
    public static void sendToPlayer(@Nullable ServerPlayer player, @NotNull CustomPacketPayload packet, @NotNull CustomPacketPayload... packets) {
        if (player != null) {
            PacketDistributor.sendToPlayer(player, packet, packets);
        }
    }

    /**
     * Safely casts a generic player to a server player and sends the specified packets.
     * If the player is not an instance of {@link ServerPlayer} (e.g., on the client), nothing happens.
     *
     * @param player  the player to send the packet to, can be null
     * @param packet  the primary packet to send
     * @param packets additional packets to send
     */
    public static void sendToPlayer(@Nullable Player player, @NotNull CustomPacketPayload packet, @NotNull CustomPacketPayload... packets) {
        if (player instanceof ServerPlayer serverPlayer) {
            sendToPlayer(serverPlayer, packet, packets);
        }
    }

    /**
     * Finds an online player by their UUID and sends the specified packets.
     *
     * @param playerUUID the UUID of the player, can be null
     * @param packet     the primary packet to send
     * @param packets    additional packets to send
     */
    public static void sendToPlayer(@Nullable UUID playerUUID, @NotNull CustomPacketPayload packet, @NotNull CustomPacketPayload... packets) {
        if (playerUUID != null) {
            MinecraftServer server = ExtraLib.getInstance().getServer();

            if (server != null) {
                sendToPlayer(server.getPlayerList().getPlayer(playerUUID), packet, packets);
            }
        }
    }

    /**
     * Finds an online player by their username and sends the specified packets.
     *
     * @param playerName the name of the player, can be null
     * @param packet     the primary packet to send
     * @param packets    additional packets to send
     */
    public static void sendToPlayer(@Nullable String playerName, @NotNull CustomPacketPayload packet, @NotNull CustomPacketPayload... packets) {
        if (playerName != null) {
            sendToPlayer(PlayerUtil.findUUID(playerName), packet, packets);
        }
    }

    /**
     * Broadcasts packets to all players currently connected to the server.
     *
     * @param packet  the primary packet to broadcast
     * @param packets additional packets to broadcast
     */
    public static void sendToAllPlayers(@NotNull CustomPacketPayload packet, @NotNull CustomPacketPayload... packets) {
        PacketDistributor.sendToAllPlayers(packet, packets);
    }

    /**
     * Sends packets to all players currently present in a specific dimension (level).
     *
     * @param level   the dimension/level to target, can be null
     * @param packet  the primary packet to send
     * @param packets additional packets to send
     */
    public static void sendToPlayersInDimension(@Nullable ServerLevel level, @NotNull CustomPacketPayload packet, @NotNull CustomPacketPayload... packets) {
        if (level != null) {
            PacketDistributor.sendToPlayersInDimension(level, packet, packets);
        }
    }

    /**
     * Sends packets from the client to the server.
     * <b>Note:</b> This must only be called from the client side.
     *
     * @param packet  the primary packet to send to the server
     * @param packets additional packets to send
     */
    public static void sendToServer(@NotNull CustomPacketPayload packet, @NotNull CustomPacketPayload... packets) {
        PacketDistributor.sendToServer(packet, packets);
    }
}
