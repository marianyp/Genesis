package dev.mariany.genesis.packet.clientbound;

import dev.mariany.genesis.Genesis;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.List;

public record UpdateInstructionsPayload(List<Identifier> instructions) implements CustomPayload {
    public static final Id<UpdateInstructionsPayload> ID = new Id<>(
            Genesis.id("update_instructions"));
    public static final PacketCodec<RegistryByteBuf, UpdateInstructionsPayload> CODEC = PacketCodec.tuple(
            Identifier.PACKET_CODEC.collect(PacketCodecs.toList()),
            UpdateInstructionsPayload::instructions,
            UpdateInstructionsPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
