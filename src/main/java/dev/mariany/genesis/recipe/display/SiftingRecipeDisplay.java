package dev.mariany.genesis.recipe.display;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.block.custom.cauldron.FilledPrimitiveCauldronBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import java.util.List;

public record SiftingRecipeDisplay(
        Identifier identifier,
        SlotDisplay input,
        SlotDisplay result,
        SlotDisplay craftingStation
) implements IdentifiedRecipeDisplay {
    public static final MapCodec<SiftingRecipeDisplay> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                                        Identifier.CODEC.fieldOf("identifier").forGetter(SiftingRecipeDisplay::identifier),
                                        SlotDisplay.CODEC.fieldOf("input").forGetter(SiftingRecipeDisplay::input),
                                        SlotDisplay.CODEC.fieldOf("result").forGetter(SiftingRecipeDisplay::result),
                                        SlotDisplay.CODEC
                                                .fieldOf("crafting_station")
                                                .forGetter(SiftingRecipeDisplay::craftingStation)
                                )
                                .apply(instance, SiftingRecipeDisplay::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, SiftingRecipeDisplay> STREAM_CODEC =
            StreamCodec.composite(
                    Identifier.STREAM_CODEC,
                    SiftingRecipeDisplay::identifier,
                    SlotDisplay.STREAM_CODEC,
                    SiftingRecipeDisplay::input,
                    SlotDisplay.STREAM_CODEC,
                    SiftingRecipeDisplay::result,
                    SlotDisplay.STREAM_CODEC,
                    SiftingRecipeDisplay::craftingStation,
                    SiftingRecipeDisplay::new
            );

    public static final RecipeDisplay.Type<SiftingRecipeDisplay> SERIALIZER = new RecipeDisplay.Type<>(
            CODEC,
            STREAM_CODEC
    );

    public SiftingRecipeDisplay(FilledPrimitiveCauldronBlock filledPrimitiveCauldronBlock, SlotDisplay result) {
        this(
                id(filledPrimitiveCauldronBlock),
                new SlotDisplay.ItemSlotDisplay(filledPrimitiveCauldronBlock.asItem()),
                result,
                new SlotDisplay.ItemSlotDisplay(filledPrimitiveCauldronBlock.getTurnsInto().asItem())
        );
    }

    private static Identifier id(FilledPrimitiveCauldronBlock block) {
        Identifier blockId = BuiltInRegistries.BLOCK.getKey(block);
        return Genesis.id("sifting/" + blockId.getNamespace() + "/" + blockId.getPath());
    }

    @Override
    public Type<SiftingRecipeDisplay> type() {
        return SERIALIZER;
    }

    public RecipeDisplayContents<SlotDisplay> contents() {
        return new RecipeDisplayContents<>(List.of(), List.of(this.input), List.of(this.result));
    }

    @Override
    public boolean isEnabled(FeatureFlagSet enabledFeatures) {
        return this.input.isEnabled(enabledFeatures)
                && this.result.isEnabled(enabledFeatures)
                && IdentifiedRecipeDisplay.super.isEnabled(enabledFeatures);
    }
}
