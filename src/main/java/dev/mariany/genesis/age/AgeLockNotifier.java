package dev.mariany.genesis.age;

import dev.mariany.genesis.packet.clientbound.NotifyAgeLockedPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

public class AgeLockNotifier {
    public static void notifyAgeLocked(String locked, Age age, ServerPlayerEntity serverPlayer) {
        notifyAgeLocked(locked, age, false, serverPlayer);
    }

    public static void notifyAgeLockedClick(String locked, Age age, ServerPlayerEntity serverPlayer) {
        notifyAgeLocked(locked, age, true, serverPlayer);
    }

    private static void notifyAgeLocked(String locked, Age age, boolean clickInteraction, ServerPlayerEntity serverPlayer) {
        ServerPlayNetworking.send(serverPlayer, new NotifyAgeLockedPayload(locked, age.display().title().getString(), clickInteraction));
    }
}
