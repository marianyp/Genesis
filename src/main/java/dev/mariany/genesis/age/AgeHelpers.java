package dev.mariany.genesis.age;

import dev.mariany.genesis.packet.clientbound.NotifyAgeLockedPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

public class AgeHelpers {
    public static void notifyAgeLocked(String locked, Age age, ServerPlayerEntity serverPlayer) {
        ServerPlayNetworking.send(serverPlayer, new NotifyAgeLockedPayload(locked, age.display().title().getString()));
    }
}
