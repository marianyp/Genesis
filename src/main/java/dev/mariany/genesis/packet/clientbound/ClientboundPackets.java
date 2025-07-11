package dev.mariany.genesis.packet.clientbound;

import dev.mariany.genesis.client.age.ClientAgeManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ClientboundPackets {
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(UpdateAgeUnlocksPayload.ID, (payload, context) -> {
            ClientAgeManager.getInstance().updateLocked(payload.unlocks());
        });
    }
}
