package dev.mariany.genesis.packet.clientbound;

import dev.mariany.genesis.Genesis;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record NotifyAgeLockedPayload(String itemTranslation, String ageTranslation) implements CustomPayload {
    public static final CustomPayload.Id<NotifyAgeLockedPayload> ID = new CustomPayload.Id<>(
            Genesis.id("notify_age_locked"));
    public static final PacketCodec<RegistryByteBuf, NotifyAgeLockedPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, NotifyAgeLockedPayload::itemTranslation,
            PacketCodecs.STRING, NotifyAgeLockedPayload::ageTranslation,
            NotifyAgeLockedPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
