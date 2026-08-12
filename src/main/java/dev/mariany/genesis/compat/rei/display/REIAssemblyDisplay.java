package dev.mariany.genesis.compat.rei.display;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.mariany.genesis.compat.rei.display.adapter.REIRecipeDisplayAdapters;
import dev.mariany.genesis.recipe.AssemblyRecipe;
import dev.mariany.genesis.recipe.display.AssemblyCraftingRecipeDisplay;
import dev.mariany.genesis.recipe.display.RecipeDisplayContents;
import dev.mariany.genesis.recipe.display.type.RecipeDisplayTypes;
import dev.mariany.genesis.recipe.display.RecipeDisplayLayout;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.InputIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCraftingDisplay;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class REIAssemblyDisplay extends DefaultCraftingDisplay {
    private static final RecipeDisplayLayout LAYOUT = RecipeDisplayTypes.ASSEMBLY.category().layout();

    public static final DisplaySerializer<REIAssemblyDisplay> SERIALIZER = DisplaySerializer.of(
            RecordCodecBuilder.mapCodec(instance -> instance
                    .group(
                            EntryIngredient
                                    .codec()
                                    .listOf()
                                    .fieldOf("inputs")
                                    .forGetter(REIAssemblyDisplay::getInputEntriesWithoutPattern),
                            EntryIngredient
                                    .codec()
                                    .listOf()
                                    .fieldOf("outputs")
                                    .forGetter(REIAssemblyDisplay::getOutputEntries),
                            Identifier.CODEC
                                    .optionalFieldOf("location")
                                    .forGetter(REIAssemblyDisplay::getDisplayLocation),
                            EntryIngredient.codec()
                                           .fieldOf("patterns")
                                           .forGetter(REIAssemblyDisplay::getPatterns),
                            Codec.INT.fieldOf("width").forGetter(REIAssemblyDisplay::getWidth),
                            Codec.INT.fieldOf("height").forGetter(REIAssemblyDisplay::getHeight)
                    )
                    .apply(
                            instance,
                            (
                                    inputs,
                                    outputs,
                                    location,
                                    patterns,
                                    width,
                                    height
                            ) -> new REIAssemblyDisplay(
                                    inputs,
                                    outputs,
                                    location.orElse(null),
                                    patterns,
                                    width,
                                    height
                            )
                    )
            ),
            StreamCodec.composite(
                    EntryIngredient.streamCodec().apply(ByteBufCodecs.list()),
                    REIAssemblyDisplay::getInputEntriesWithoutPattern,
                    EntryIngredient.streamCodec().apply(ByteBufCodecs.list()),
                    REIAssemblyDisplay::getOutputEntries,
                    ByteBufCodecs.optional(Identifier.STREAM_CODEC),
                    REIAssemblyDisplay::getDisplayLocation,
                    EntryIngredient.streamCodec(),
                    REIAssemblyDisplay::getPatterns,
                    ByteBufCodecs.INT,
                    REIAssemblyDisplay::getWidth,
                    ByteBufCodecs.INT,
                    REIAssemblyDisplay::getHeight,
                    (
                            inputs,
                            outputs,
                            location,
                            patterns,
                            width,
                            height
                    ) -> new REIAssemblyDisplay(
                            inputs,
                            outputs,
                            location.orElse(null),
                            patterns,
                            width,
                            height
                    )
            )
    );

    protected EntryIngredient patterns;
    private final int width;
    private final int height;

    public REIAssemblyDisplay(RecipeHolder<AssemblyRecipe> recipe) {
        this(recipe, recipe.value().getDisplay());
    }

    private REIAssemblyDisplay(RecipeHolder<AssemblyRecipe> recipe, AssemblyCraftingRecipeDisplay display) {
        this(
                EntryIngredients.ofSlotDisplays(display.ingredients()),
                List.of(EntryIngredients.ofSlotDisplay(display.result())),
                recipe.id().identifier(),
                EntryIngredients.ofSlotDisplay(display.pattern()),
                display.width(),
                display.height()
        );
    }

    private REIAssemblyDisplay(
            List<EntryIngredient> inputs,
            List<EntryIngredient> outputs,
            @Nullable Identifier location,
            EntryIngredient patterns,
            int width,
            int height
    ) {
        super(inputs, outputs, Optional.ofNullable(location));
        this.patterns = patterns;
        this.width = width;
        this.height = height;
    }

    public EntryIngredient getPatterns() {
        return this.patterns;
    }

    public RecipeDisplayContents<EntryIngredient> getContents() {
        return new RecipeDisplayContents<>(
                List.of(this.patterns),
                this.inputs,
                this.outputs
        );
    }

    @Override
    public DisplaySerializer<? extends Display> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return REIRecipeDisplayAdapters.ASSEMBLY.categoryIdentifier();
    }

    @Override
    public boolean isShapeless() {
        return false;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        ArrayList<EntryIngredient> inputs = new ArrayList<>(this.inputs);
        inputs.add(this.patterns);
        return inputs;
    }

    public List<EntryIngredient> getInputEntriesWithoutPattern() {
        return this.inputs;
    }

    @Override
    public List<InputIngredient<EntryStack<?>>> getInputIngredients(int craftingWidth, int craftingHeight) {
        List<InputIngredient<EntryStack<?>>> ingredients = new ArrayList<>(craftingWidth * craftingHeight);

        for (int i = 0, n = craftingWidth * craftingHeight; i < n; i++) {
            ingredients.add(InputIngredient.empty(i));
        }

        LAYOUT.populate(getInputWidth(craftingWidth, craftingHeight), getContents())
              .stream()
              .filter(RecipeDisplayLayout.PopulatedSlot::isCraftingInput)
              .filter(slot -> slot.slot().index() < ingredients.size())
              .filter(slot -> slot.content().isPresent() && !slot.content().get().isEmpty())
              .forEach(slot -> {
                  int index = slot.slot().index();
                  ingredients.set(index, InputIngredient.of(index, index, slot.content().get()));
              });

        return ingredients;
    }

    @Override
    public List<EntryIngredient> getOrganisedInputEntries(int menuWidth, int menuHeight) {
        List<EntryIngredient> entries = new ArrayList<>(menuWidth * menuHeight);

        for (int i = 0; i < menuWidth * menuHeight; i++) {
            entries.add(EntryIngredient.empty());
        }

        LAYOUT.populate(getInputWidth(menuWidth, menuHeight), getContents())
              .stream()
              .filter(RecipeDisplayLayout.PopulatedSlot::isCraftingInput)
              .filter(slot -> slot.slot().index() < entries.size())
              .forEach(slot -> slot
                      .content()
                      .ifPresent(content -> entries.set(slot.slot().index(), content))
              );

        entries.add(this.patterns);

        return entries;
    }
}
