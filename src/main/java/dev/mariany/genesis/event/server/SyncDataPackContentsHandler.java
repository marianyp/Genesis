package dev.mariany.genesis.event.server;

import dev.mariany.genesis.age.AgeManager;
import dev.mariany.genesis.instruction.InstructionManager;
import dev.mariany.genesis.packet.clientbound.UpdateAgeItemUnlocksPayload;
import dev.mariany.genesis.packet.clientbound.UpdateInstructionsPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

public class SyncDataPackContentsHandler {
    public static void onSyncDataPackContents(ServerPlayerEntity player, boolean joined) {
        ServerPlayNetworking.send(player, new UpdateAgeItemUnlocksPayload(
                AgeManager.getInstance().getAllItemUnlocks(player))
        );

        ServerPlayNetworking.send(player, new UpdateInstructionsPayload(
                InstructionManager.getInstance().getInstructionAdvancementIds())
        );
    }
}
