package dev.mariany.genesis.packet;

import dev.mariany.genesis.packet.clientbound.NotifyAgeLockedPayload;
import dev.mariany.genesis.packet.clientbound.UpdateAgeItemUnlocksPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;

public class GenesisPackets {
    public static void register() {
        clientbound(PayloadTypeRegistry.playS2C());
        serverbound(PayloadTypeRegistry.playC2S());
    }

    private static void clientbound(PayloadTypeRegistry<RegistryByteBuf> registry) {
        registry.register(UpdateAgeItemUnlocksPayload.ID, UpdateAgeItemUnlocksPayload.CODEC);
        registry.register(NotifyAgeLockedPayload.ID, NotifyAgeLockedPayload.CODEC);
    }

    private static void serverbound(PayloadTypeRegistry<RegistryByteBuf> registry) {
    }
}
