package dev.mariany.genesis.event.server;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.age.AgeShareManager;
import dev.mariany.genesis.gamerule.GenesisGamerules;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class ServerPlayConnectionHandler {
    public static void onJoin(
            ServerPlayNetworkHandler serverPlayNetworkHandler,
            PacketSender packetSender,
            MinecraftServer server
    ) {
        ServerPlayerEntity serverPlayer = serverPlayNetworkHandler.player;
        AgeShareManager ageShareManager = AgeShareManager.getServerState(server);

        if (server.getGameRules().getBoolean(GenesisGamerules.SHARED_AGE_PROGRESSION)) {
            Genesis.LOGGER.info("Preparing to apply shared ages to {}", serverPlayer.toString());
            ageShareManager.applySharedAges(serverPlayer);
        }
    }
}
