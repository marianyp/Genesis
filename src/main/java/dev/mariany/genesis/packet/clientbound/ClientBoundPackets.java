package dev.mariany.genesis.packet.clientbound;

import dev.mariany.genesis.Genesis;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ClientBoundPackets {
    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(
                UpdateTirednessLogicPayload.ID,
                (payload, context) -> Genesis.TIREDNESS_LOGIC
                        .setMinimumDaysBeforeSleeping(payload.minimumDaysBeforeSleeping())
        );

        ClientPlayNetworking.registerGlobalReceiver(
                UpdateSolaceLogicPayload.ID,
                (payload, context) -> Genesis.SOLACE_LOGIC.setCampfireRadius(
                        payload.campfireRadius()
                )
        );
    }
}
