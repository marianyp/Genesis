package dev.mariany.genesis.recipe.display;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import java.util.List;

public record AssemblyCraftingRecipeDisplay(
        Identifier identifier,
        int width,
        int height,
        List<SlotDisplay> ingredients,
        SlotDisplay pattern,
        SlotDisplay result,
        SlotDisplay craftingStation
) implements IdentifiedRecipeDisplay {
    public static final MapCodec<AssemblyCraftingRecipeDisplay> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                                        Identifier.CODEC
                                                .fieldOf("identifier")
                                                .forGetter(AssemblyCraftingRecipeDisplay::identifier),
                                        Codec.INT
                                                .fieldOf("width")
                                                .forGetter(AssemblyCraftingRecipeDisplay::width),
                                        Codec.INT
                                                .fieldOf("height")
                                                .forGetter(AssemblyCraftingRecipeDisplay::height),
                                        SlotDisplay.CODEC
                                                .listOf()
                                                .fieldOf("ingredients")
                                                .forGetter(AssemblyCraftingRecipeDisplay::ingredients),
                                        SlotDisplay.CODEC
                                                .fieldOf("pattern")
                                                .forGetter(AssemblyCraftingRecipeDisplay::pattern),
                                        SlotDisplay.CODEC
                                                .fieldOf("result")
                                                .forGetter(AssemblyCraftingRecipeDisplay::result),
                                        SlotDisplay.CODEC
                                                .fieldOf("crafting_station")
                                                .forGetter(AssemblyCraftingRecipeDisplay::craftingStation)
                                )
                                .apply(instance, AssemblyCraftingRecipeDisplay::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, AssemblyCraftingRecipeDisplay> STREAM_CODEC =
            StreamCodec.composite(
                    Identifier.STREAM_CODEC,
                    AssemblyCraftingRecipeDisplay::identifier,
                    ByteBufCodecs.VAR_INT,
                    AssemblyCraftingRecipeDisplay::width,
                    ByteBufCodecs.VAR_INT,
                    AssemblyCraftingRecipeDisplay::height,
                    SlotDisplay.STREAM_CODEC.apply(ByteBufCodecs.list()),
                    AssemblyCraftingRecipeDisplay::ingredients,
                    SlotDisplay.STREAM_CODEC,
                    AssemblyCraftingRecipeDisplay::pattern,
                    SlotDisplay.STREAM_CODEC,
                    AssemblyCraftingRecipeDisplay::result,
                    SlotDisplay.STREAM_CODEC,
                    AssemblyCraftingRecipeDisplay::craftingStation,
                    AssemblyCraftingRecipeDisplay::new
            );

    public static final RecipeDisplay.Type<AssemblyCraftingRecipeDisplay> SERIALIZER =
            new RecipeDisplay.Type<>(CODEC, STREAM_CODEC);

    public AssemblyCraftingRecipeDisplay {
        if (ingredients.size() != width * height) {
            throw new IllegalArgumentException("Invalid assembly recipe display contents");
        }
    }

    @Override
    public RecipeDisplay.Type<AssemblyCraftingRecipeDisplay> type() {
        return SERIALIZER;
    }

    public RecipeDisplayContents<SlotDisplay> contents() {
        return new RecipeDisplayContents<>(List.of(this.pattern), this.ingredients, List.of(this.result));
    }

    @Override
    public boolean isEnabled(FeatureFlagSet enabledFeatures) {
        return this.ingredients.stream().allMatch(ingredient -> ingredient.isEnabled(enabledFeatures)) &&
                this.pattern.isEnabled(enabledFeatures) &&
                IdentifiedRecipeDisplay.super.isEnabled(enabledFeatures);
    }
}
