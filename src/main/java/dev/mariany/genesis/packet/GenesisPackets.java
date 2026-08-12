package dev.mariany.genesis.packet;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.packet.clientbound.UpdateSolaceLogicPayload;
import dev.mariany.genesis.packet.clientbound.UpdateTirednessLogicPayload;
import dev.mariany.genesis.packet.clientbound.UpdatePrimitiveCauldronLootPayload;
import dev.mariany.genesis.packet.serverbound.QuickCraftAssemblyPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;

public class GenesisPackets {
    public static void bootstrap() {
        Genesis.bootstrapLog("Packets");
        clientBound(PayloadTypeRegistry.clientboundPlay());
        serverBound(PayloadTypeRegistry.serverboundPlay());
    }

    private static void clientBound(PayloadTypeRegistry<RegistryFriendlyByteBuf> registry) {
        registry.register(UpdateTirednessLogicPayload.ID, UpdateTirednessLogicPayload.STREAM_CODEC);
        registry.register(UpdateSolaceLogicPayload.ID, UpdateSolaceLogicPayload.STREAM_CODEC);
        registry.register(UpdatePrimitiveCauldronLootPayload.ID, UpdatePrimitiveCauldronLootPayload.STREAM_CODEC);
    }

    private static void serverBound(PayloadTypeRegistry<RegistryFriendlyByteBuf> registry) {
        registry.register(QuickCraftAssemblyPayload.ID, QuickCraftAssemblyPayload.STREAM_CODEC);
    }
}
