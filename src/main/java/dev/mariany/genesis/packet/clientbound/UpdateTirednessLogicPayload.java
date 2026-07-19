package dev.mariany.genesis.packet.clientbound;

import dev.mariany.genesis.Genesis;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record UpdateTirednessLogicPayload(double minimumDaysBeforeSleeping) implements CustomPacketPayload {
    public static final Type<UpdateTirednessLogicPayload> ID = new Type<>(
            Genesis.id("update_tiredness_logic")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateTirednessLogicPayload> STREAM_CODEC = StreamCodec
            .composite(
                    ByteBufCodecs.DOUBLE,
                    UpdateTirednessLogicPayload::minimumDaysBeforeSleeping,
                    UpdateTirednessLogicPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
