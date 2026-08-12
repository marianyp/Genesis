package dev.mariany.genesis.packet.clientbound;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.attachment.GenesisAttachmentTypes;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;

public final class ClientBoundPackets {
    private ClientBoundPackets() {
    }

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(UpdateSolaceLogicPayload.ID, ClientBoundPackets::updateSolaceLogic);

        ClientPlayNetworking.registerGlobalReceiver(
                UpdatePrimitiveCauldronLootPayload.ID,
                ClientBoundPackets::updatePrimitiveCauldronLoot
        );

        ClientPlayNetworking.registerGlobalReceiver(
                UpdateTirednessLogicPayload.ID,
                ClientBoundPackets::updateTirednessLogic
        );
    }

    private static void updateSolaceLogic(UpdateSolaceLogicPayload payload, ClientPlayNetworking.Context context) {
        Genesis.SOLACE_LOGIC.setCampfireRadius(payload.campfireRadius());
    }

    private static void updatePrimitiveCauldronLoot(
            UpdatePrimitiveCauldronLootPayload payload,
            ClientPlayNetworking.Context context
    ) {
        Genesis.PRIMITIVE_CAULDRON_LOOT.update(payload.possibleDrops());
    }

    private static void updateTirednessLogic(
            UpdateTirednessLogicPayload payload,
            ClientPlayNetworking.Context context
    ) {
        Genesis.TIREDNESS_LOGIC.setMinimumTicksBeforeSleeping(payload.minimumTicksBeforeSleeping());
        updateAwakeTicks(context.client(), payload.awakeTicks());
    }

    private static void updateAwakeTicks(Minecraft client, int awakeTicks) {
        if (client.player == null) {
            return;
        }

        client.player.setAttached(GenesisAttachmentTypes.AWAKE_TICKS, awakeTicks);
    }
}
