package dev.mariany.genesis.packet.serverbound;

import dev.mariany.genesis.Genesis;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;

public record QuickCraftAssemblyPayload(ResourceKey<Recipe<?>> recipeKey) implements CustomPacketPayload {
    public static final Type<QuickCraftAssemblyPayload> ID = new Type<>(Genesis.id("quick_craft_assembly"));

    private static final StreamCodec<ByteBuf, ResourceKey<Recipe<?>>> RECIPE_KEY_STREAM_CODEC = ResourceKey.streamCodec(
            Registries.RECIPE
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, QuickCraftAssemblyPayload> STREAM_CODEC =
            StreamCodec.composite(
                    RECIPE_KEY_STREAM_CODEC,
                    QuickCraftAssemblyPayload::recipeKey,
                    QuickCraftAssemblyPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
