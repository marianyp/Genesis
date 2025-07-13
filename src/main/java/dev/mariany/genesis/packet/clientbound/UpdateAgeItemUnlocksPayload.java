package dev.mariany.genesis.packet.clientbound;

import dev.mariany.genesis.Genesis;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.recipe.Ingredient;

import java.util.List;

public record UpdateAgeItemUnlocksPayload(List<Ingredient> items) implements CustomPayload {
    public static final CustomPayload.Id<UpdateAgeItemUnlocksPayload> ID = new CustomPayload.Id<>(
            Genesis.id("update_age_item_unlocks"));
    public static final PacketCodec<RegistryByteBuf, UpdateAgeItemUnlocksPayload> CODEC = PacketCodec.tuple(
            Ingredient.PACKET_CODEC.collect(PacketCodecs.toList()), UpdateAgeItemUnlocksPayload::items, UpdateAgeItemUnlocksPayload::new);

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
