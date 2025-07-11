package dev.mariany.genesis.packet.clientbound;

import dev.mariany.genesis.Genesis;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.recipe.Ingredient;

import java.util.List;

public record UpdateAgeUnlocksPayload(List<Ingredient> unlocks) implements CustomPayload {
    public static final CustomPayload.Id<UpdateAgeUnlocksPayload> ID = new CustomPayload.Id<>(
            Genesis.id("update_age_unlocks"));
    public static final PacketCodec<RegistryByteBuf, UpdateAgeUnlocksPayload> CODEC = PacketCodec.tuple(
            Ingredient.PACKET_CODEC.collect(PacketCodecs.toList()), UpdateAgeUnlocksPayload::unlocks, UpdateAgeUnlocksPayload::new);

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
