package com.vecoo.extralib.util;

import com.vecoo.extralib.ExtraLib;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class PacketUtil {
    private PacketUtil() {
    }

    /**
     * Sends one packet to a specific server-side player.
     *
     * @param player the target server player, can be null
     * @param packet the primary packet to send
     */
    public static <T extends FabricPacket> void sendToPlayer(@Nullable ServerPlayer player, @NotNull T packet) {
        if (player != null) {
            ServerPlayNetworking.send(player, packet);
        }
    }

    /**
     * Safely casts a generic player to a server player and sends the specified packet.
     * If the player is not an instance of {@link ServerPlayer} (e.g., on the client), nothing happens.
     *
     * @param player the player to send the packet to, can be null
     * @param packet the primary packet to send
     */
    public static void sendToPlayer(@Nullable Player player, @NotNull Packet<?> packet) {
        if (player instanceof ServerPlayer serverPlayer) {
            sendToPlayer(serverPlayer, packet);
        }
    }

    /**
     * Finds an online player by their UUID and sends the specified packet.
     *
     * @param playerUUID the UUID of the player, can be null
     * @param packet     the primary packet to send
     */
    public static void sendToPlayer(@Nullable UUID playerUUID, @NotNull Packet<?> packet) {
        if (playerUUID != null) {
            MinecraftServer server = ExtraLib.getInstance().getServer();

            if (server != null) {
                sendToPlayer(server.getPlayerList().getPlayer(playerUUID), packet);
            }
        }
    }

    /**
     * Finds an online player by their username and sends the specified packet.
     *
     * @param playerName the name of the player, can be null
     * @param packet     the primary packet to send
     */
    public static void sendToPlayer(@Nullable String playerName, @NotNull Packet<?> packet) {
        if (playerName != null) {
            sendToPlayer(PlayerUtil.findUUID(playerName), packet);
        }
    }

    /**
     * Broadcasts packet to all players currently connected to the server.
     *
     * @param packet the primary packet to broadcast
     */
    public static void sendToAllPlayers(@NotNull Packet<?> packet) {
        ExtraLib.getInstance().getServer().getPlayerList().broadcastAll(packet);
    }

    /**
     * Sends packet to all players currently present in a specific dimension (level).
     *
     * @param level  the dimension/level to target, can be null
     * @param packet the primary packet to send
     */
    public static void sendToPlayersInDimension(@Nullable ServerLevel level, @NotNull Packet<?> packet) {
        if (level != null) {
            level.getServer().getPlayerList().broadcastAll(packet, level.dimension());
        }
    }

    /**
     * Sends packet from the client to the server.
     * <b>Note:</b> This must only be called from the client side.
     *
     * @param packet the primary packet to send to the server
     */
    public static <T extends FabricPacket> void sendToServer(@NotNull T packet) {
        ClientPlayNetworking.send(packet);
    }
}