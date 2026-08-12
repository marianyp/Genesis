package dev.mariany.genesis.packet.clientbound;

import dev.mariany.genesis.Genesis;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record UpdatePrimitiveCauldronLootPayload(Map<ResourceKey<LootTable>, List<Ingredient>> possibleDrops)
        implements CustomPacketPayload {
    public static final Type<UpdatePrimitiveCauldronLootPayload> ID = new Type<>(
            Genesis.id("update_primitive_cauldron_loot")
    );

    private static final StreamCodec<RegistryFriendlyByteBuf, List<Ingredient>> INGREDIENT_LIST_STREAM_CODEC =
            Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list());

    private static final StreamCodec<ByteBuf, ResourceKey<LootTable>> LOOT_TABLE_KEY_STREAM_CODEC =
            ResourceKey.streamCodec(Registries.LOOT_TABLE);

    public static final StreamCodec<RegistryFriendlyByteBuf, UpdatePrimitiveCauldronLootPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.map(HashMap::new, LOOT_TABLE_KEY_STREAM_CODEC, INGREDIENT_LIST_STREAM_CODEC),
                    UpdatePrimitiveCauldronLootPayload::possibleDrops,
                    UpdatePrimitiveCauldronLootPayload::new
            );

    public UpdatePrimitiveCauldronLootPayload {
        Map<ResourceKey<LootTable>, List<Ingredient>> copy = new HashMap<>();

        possibleDrops.forEach((lootTable, ingredients) -> copy.put(
                lootTable,
                List.copyOf(ingredients)
        ));

        possibleDrops = Map.copyOf(copy);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
