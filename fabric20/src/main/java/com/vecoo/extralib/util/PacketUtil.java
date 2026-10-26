package com.vecoo.extralib.util;

import com.vecoo.extralib.ExtraLib;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
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
    public static void sendToPlayer(@Nullable ServerPlayer player, @NotNull FabricPacket packet) {
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
    public static void sendToPlayer(@Nullable Player player, @NotNull FabricPacket packet) {
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
    public static void sendToPlayer(@Nullable UUID playerUUID, @NotNull FabricPacket packet) {
        if (playerUUID != null) {
            MinecraftServer server = ExtraLib.getCurrentServer();

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
    public static void sendToPlayer(@Nullable String playerName, @NotNull FabricPacket packet) {
        if (playerName != null) {
            sendToPlayer(PlayerUtil.findUUID(playerName), packet);
        }
    }

    /**
     * Broadcasts packet to all players currently connected to the server.
     *
     * @param packet the primary packet to broadcast
     */
    public static void sendToAllPlayers(@NotNull FabricPacket packet) {
        MinecraftServer server = ExtraLib.getCurrentServer();

        if (server != null) {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                sendToPlayer(player, packet);
            }
        }
    }

    /**
     * Sends packet to all players currently present in a specific dimension (level).
     *
     * @param level  the dimension/level to target, can be null
     * @param packet the primary packet to send
     */
    public static void sendToPlayersInDimension(@Nullable ServerLevel level, @NotNull FabricPacket packet) {
        if (level != null) {
            for (ServerPlayer player : level.players()) {
                sendToPlayer(player, packet);
            }
        }
    }

    /**
     * Sends packet from the client to the server.
     * <b>Note:</b> This must only be called from the client side.
     *
     * @param packet the primary packet to send to the server
     */
    public static void sendToServer(@NotNull FabricPacket packet) {
        ClientPlayNetworking.send(packet);
    }

    public static void sendToPlayer(@Nullable ServerPlayer player, @NotNull FabricPacket packet, @NotNull FabricPacket... packets) {
        sendToPlayer(player, packet);

        for (FabricPacket additional : packets) {
            sendToPlayer(player, additional);
        }
    }


    public static void sendToPlayer(@Nullable Player player, @NotNull FabricPacket packet, @NotNull FabricPacket... packets) {
        sendToPlayer(player, packet);

        for (FabricPacket additional : packets) {
            sendToPlayer(player, additional);
        }
    }


    public static void sendToPlayer(@Nullable UUID playerUUID, @NotNull FabricPacket packet, @NotNull FabricPacket... packets) {
        sendToPlayer(playerUUID, packet);

        for (FabricPacket additional : packets) {
            sendToPlayer(playerUUID, additional);
        }
    }


    public static void sendToPlayer(@Nullable String playerName, @NotNull FabricPacket packet, @NotNull FabricPacket... packets) {
        sendToPlayer(playerName, packet);

        for (FabricPacket additional : packets) {
            sendToPlayer(playerName, additional);
        }
    }


    public static void sendToAllPlayers(@NotNull FabricPacket packet, @NotNull FabricPacket... packets) {
        sendToAllPlayers(packet);

        for (FabricPacket additional : packets) {
            sendToAllPlayers(additional);
        }
    }


    public static void sendToPlayersInDimension(@Nullable ServerLevel level, @NotNull FabricPacket packet, @NotNull FabricPacket... packets) {
        sendToPlayersInDimension(level, packet);

        for (FabricPacket additional : packets) {
            sendToPlayersInDimension(level, additional);
        }
    }


    public static void sendToServer(@NotNull FabricPacket packet, @NotNull FabricPacket... packets) {
        sendToServer(packet);

        for (FabricPacket additional : packets) {
            sendToServer(additional);
        }
    }

}
