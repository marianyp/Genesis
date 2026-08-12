package dev.mariany.genesis.compat.rei.display;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.mariany.genesis.compat.rei.display.adapter.REIRecipeDisplayAdapters;
import dev.mariany.genesis.recipe.display.RecipeDisplayContents;
import dev.mariany.genesis.recipe.display.SiftingRecipeDisplay;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCraftingDisplay;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Optional;

public class REISiftingDisplay extends DefaultCraftingDisplay {
    public static final DisplaySerializer<REISiftingDisplay> SERIALIZER = DisplaySerializer.of(
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                            EntryIngredient.codec()
                                    .fieldOf("input")
                                    .forGetter(REISiftingDisplay::getInput),
                            EntryIngredient.codec()
                                    .fieldOf("output")
                                    .forGetter(REISiftingDisplay::getOutput),
                            Identifier.CODEC
                                    .optionalFieldOf("location")
                                    .forGetter(REISiftingDisplay::getDisplayLocation)
                    )
                    .apply(instance, REISiftingDisplay::new)
            ),
            StreamCodec.composite(
                    EntryIngredient.streamCodec(),
                    REISiftingDisplay::getInput,
                    EntryIngredient.streamCodec(),
                    REISiftingDisplay::getOutput,
                    ByteBufCodecs.optional(Identifier.STREAM_CODEC),
                    REISiftingDisplay::getDisplayLocation,
                    REISiftingDisplay::new
            )
    );

    public REISiftingDisplay(SiftingRecipeDisplay display) {
        this(
                EntryIngredients.ofSlotDisplay(display.input()),
                EntryIngredients.ofSlotDisplay(display.result()),
                Optional.of(display.identifier())
        );
    }

    private REISiftingDisplay(
            EntryIngredient input,
            EntryIngredient output,
            Optional<Identifier> location
    ) {
        super(List.of(input), List.of(output), location);
    }

    private EntryIngredient getInput() {
        return this.inputs.getFirst();
    }

    private EntryIngredient getOutput() {
        return this.outputs.getFirst();
    }

    public RecipeDisplayContents<EntryIngredient> getContents() {
        return new RecipeDisplayContents<>(List.of(), this.inputs, this.outputs);
    }

    @Override
    public DisplaySerializer<? extends Display> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return REIRecipeDisplayAdapters.SIFTING.categoryIdentifier();
    }

    @Override
    public boolean isShapeless() {
        return false;
    }

    @Override
    public int getWidth() {
        return 1;
    }

    @Override
    public int getHeight() {
        return 1;
    }
}
