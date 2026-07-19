package dev.mariany.genesis.packet.clientbound;

import dev.mariany.genesis.Genesis;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record UpdateSolaceLogicPayload(double campfireRadius) implements CustomPacketPayload {
    public static final Type<UpdateSolaceLogicPayload> ID = new Type<>(
            Genesis.id("update_solace_logic")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateSolaceLogicPayload> STREAM_CODEC = StreamCodec
            .composite(
                    ByteBufCodecs.DOUBLE,
                    UpdateSolaceLogicPayload::campfireRadius,
                    UpdateSolaceLogicPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
