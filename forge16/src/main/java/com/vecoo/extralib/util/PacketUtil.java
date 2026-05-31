package com.vecoo.extralib.util;

import com.vecoo.extralib.ExtraLib;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class PacketUtil {
    private PacketUtil() {
    }

    /**
     * Sends one or more packets to a specific server-side player.
     *
     * @param player the target server player, can be null
     * @param packet the primary packet to send
     */
    public static void sendToPlayer(@Nullable ServerPlayerEntity player, @NotNull IPacket<?> packet) {
        if (player != null) {
            PacketDistributor.PLAYER.with(() -> player).send(packet);
        }
    }

    /**
     * Safely casts a generic player to a server player and sends the specified packets.
     * If the player is not an instance of {@link ServerPlayerEntity} (e.g., on the client), nothing happens.
     *
     * @param player the player to send the packet to, can be null
     * @param packet the primary packet to send
     */
    public static void sendToPlayer(@Nullable PlayerEntity player, @NotNull IPacket<?> packet) {
        if (player instanceof ServerPlayerEntity) {
            sendToPlayer((ServerPlayerEntity) player, packet);
        }
    }

    /**
     * Finds an online player by their UUID and sends the specified packets.
     *
     * @param playerUUID the UUID of the player, can be null
     * @param packet     the primary packet to send
     */
    public static void sendToPlayer(@Nullable UUID playerUUID, @NotNull IPacket<?> packet) {
        if (playerUUID != null) {
            MinecraftServer server = ExtraLib.getInstance().getServer();

            if (server != null) {
                sendToPlayer(server.getPlayerList().getPlayer(playerUUID), packet);
            }
        }
    }

    /**
     * Finds an online player by their username and sends the specified packets.
     *
     * @param playerName the name of the player, can be null
     * @param packet     the primary packet to send
     */
    public static void sendToPlayer(@Nullable String playerName, @NotNull IPacket<?> packet) {
        if (playerName != null) {
            sendToPlayer(PlayerUtil.findUUID(playerName), packet);
        }
    }

    /**
     * Broadcasts packets to all players currently connected to the server.
     *
     * @param packet the primary packet to broadcast
     */
    public static void sendToAllPlayers(@NotNull IPacket<?> packet) {
        PacketDistributor.ALL.noArg().send(packet);
    }

    /**
     * Sends packets to all players currently present in a specific dimension (world).
     *
     * @param world  the dimension/world to target, can be null
     * @param packet the primary packet to send
     */
    public static void sendToPlayersInDimension(@Nullable ServerWorld world, @NotNull IPacket<?> packet) {
        if (world != null) {
            PacketDistributor.DIMENSION.with(world::dimension).send(packet);
        }
    }

    /**
     * Sends packets from the client to the server.
     * <b>Note:</b> This must only be called from the client side.
     *
     * @param packet the primary packet to send to the server
     */
    public static void sendToServer(@NotNull IPacket<?> packet) {
        PacketDistributor.SERVER.noArg().send(packet);
    }
}