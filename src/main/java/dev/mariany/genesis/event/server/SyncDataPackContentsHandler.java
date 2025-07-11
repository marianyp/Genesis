package dev.mariany.genesis.event.server;

import dev.mariany.genesis.age.AgeManager;
import dev.mariany.genesis.packet.clientbound.UpdateAgeUnlocksPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

public class SyncDataPackContentsHandler {
    public static void onSyncDataPackContents(ServerPlayerEntity player, boolean joined) {
        ServerPlayNetworking.send(player, new UpdateAgeUnlocksPayload(AgeManager.getInstance().getAllUnlocks(player)));
    }
}
