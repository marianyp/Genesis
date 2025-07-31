package dev.mariany.genesis.recipe.display;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.display.RecipeDisplay;
import net.minecraft.recipe.display.SlotDisplay;
import net.minecraft.resource.featuretoggle.FeatureSet;

import java.util.List;

public record AssemblyCraftingRecipeDisplay(
        int width,
        int height,
        List<SlotDisplay> ingredients,
        SlotDisplay result,
        SlotDisplay craftingStation
) implements RecipeDisplay {
    public static final MapCodec<AssemblyCraftingRecipeDisplay> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                            Codec.INT.fieldOf("width").forGetter(AssemblyCraftingRecipeDisplay::width),
                            Codec.INT.fieldOf("height").forGetter(AssemblyCraftingRecipeDisplay::height),
                            SlotDisplay.CODEC.listOf().fieldOf("ingredients").forGetter(AssemblyCraftingRecipeDisplay::ingredients),
                            SlotDisplay.CODEC.fieldOf("result").forGetter(AssemblyCraftingRecipeDisplay::result),
                            SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(AssemblyCraftingRecipeDisplay::craftingStation)
                    )
                    .apply(instance, AssemblyCraftingRecipeDisplay::new)
    );

    public static final PacketCodec<RegistryByteBuf, AssemblyCraftingRecipeDisplay> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT,
            AssemblyCraftingRecipeDisplay::width,
            PacketCodecs.VAR_INT,
            AssemblyCraftingRecipeDisplay::height,
            SlotDisplay.PACKET_CODEC.collect(PacketCodecs.toList()),
            AssemblyCraftingRecipeDisplay::ingredients,
            SlotDisplay.PACKET_CODEC,
            AssemblyCraftingRecipeDisplay::result,
            SlotDisplay.PACKET_CODEC,
            AssemblyCraftingRecipeDisplay::craftingStation,
            AssemblyCraftingRecipeDisplay::new
    );

    public static final RecipeDisplay.Serializer<AssemblyCraftingRecipeDisplay> SERIALIZER = new RecipeDisplay.Serializer<>(CODEC, PACKET_CODEC);

    public AssemblyCraftingRecipeDisplay {
        if (ingredients.size() != width * height) {
            throw new IllegalArgumentException("Invalid assembly recipe display contents");
        }
    }

    @Override
    public RecipeDisplay.Serializer<AssemblyCraftingRecipeDisplay> serializer() {
        return SERIALIZER;
    }

    @Override
    public boolean isEnabled(FeatureSet features) {
        return this.ingredients.stream().allMatch(ingredient -> ingredient.isEnabled(features)) && RecipeDisplay.super.isEnabled(features);
    }
}
