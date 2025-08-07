package dev.mariany.genesis.compat.rei.display;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.mariany.genesis.compat.rei.REICategoryIdentifiers;
import dev.mariany.genesis.recipe.AssemblyRecipe;
import it.unimi.dsi.fastutil.ints.IntIntImmutablePair;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.InputIngredient;
import me.shedaniel.rei.api.common.util.CollectionUtils;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.plugin.common.displays.crafting.CraftingDisplay;
import net.minecraft.item.ItemConvertible;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.Identifier;

import java.util.*;

public class AssemblyDisplay extends BasicDisplay implements CraftingDisplay {
    public static final DisplaySerializer<AssemblyDisplay> SERIALIZER = DisplaySerializer.of(
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                                    EntryIngredient
                                            .codec()
                                            .listOf()
                                            .fieldOf("inputs")
                                            .forGetter(AssemblyDisplay::getInputEntriesWithoutPattern),
                                    EntryIngredient
                                            .codec()
                                            .listOf()
                                            .fieldOf("outputs")
                                            .forGetter(AssemblyDisplay::getOutputEntries),
                                    Identifier.CODEC
                                            .optionalFieldOf("location")
                                            .forGetter(AssemblyDisplay::getDisplayLocation),
                                    EntryIngredient.codec()
                                            .fieldOf("patterns")
                                            .forGetter(AssemblyDisplay::getPatterns),
                                    Codec.INT.fieldOf("width").forGetter(AssemblyDisplay::getWidth),
                                    Codec.INT.fieldOf("height").forGetter(AssemblyDisplay::getHeight)
                            )
                            .apply(instance, AssemblyDisplay::new)
            ),
            PacketCodec.tuple(
                    EntryIngredient.streamCodec().collect(PacketCodecs.toList()),
                    AssemblyDisplay::getInputEntriesWithoutPattern,
                    EntryIngredient.streamCodec().collect(PacketCodecs.toList()),
                    AssemblyDisplay::getOutputEntries,
                    PacketCodecs.optional(Identifier.PACKET_CODEC),
                    AssemblyDisplay::getDisplayLocation,
                    EntryIngredient.streamCodec(),
                    AssemblyDisplay::getPatterns,
                    PacketCodecs.INTEGER,
                    AssemblyDisplay::getWidth,
                    PacketCodecs.INTEGER,
                    AssemblyDisplay::getHeight,
                    AssemblyDisplay::new
            )
    );

    protected EntryIngredient patterns;
    private final int width;
    private final int height;

    public AssemblyDisplay(RecipeEntry<AssemblyRecipe> recipe) {
        this(
                CollectionUtils.map(
                        recipe.value().getIngredients(),
                        opt -> opt
                                .map(EntryIngredients::ofIngredient)
                                .orElse(EntryIngredient.empty())
                ),
                List.of(EntryIngredients.of(recipe.value().craft())),
                Optional.of(recipe.id().getValue()),
                EntryIngredients.ofItems(recipe.value().getPatterns()
                        .stream()
                        .map(assemblyPatternItem -> (ItemConvertible) assemblyPatternItem)
                        .toList()
                ),
                recipe.value().getWidth(),
                recipe.value().getHeight()
        );
    }

    public AssemblyDisplay(
            List<EntryIngredient> inputs,
            List<EntryIngredient> outputs,
            Optional<Identifier> location,
            EntryIngredient patterns,
            int width,
            int height
    ) {
        super(inputs, outputs, location);
        this.patterns = patterns;
        this.width = width;
        this.height = height;
    }

    public EntryIngredient getPatterns() {
        return this.patterns;
    }

    @Override
    public DisplaySerializer<? extends Display> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return REICategoryIdentifiers.ASSEMBLY;
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
        int inputWidth = getInputWidth(craftingWidth, craftingHeight);

        Map<IntIntPair, InputIngredient<EntryStack<?>>> grid = new HashMap<>();

        for (int i = 0; i < this.inputs.size(); i++) {
            EntryIngredient stacks = this.inputs.get(i);

            if (stacks.isEmpty()) {
                continue;
            }

            int index = CraftingDisplay.getSlotWithSize(inputWidth, i, craftingWidth);
            int x = i % inputWidth;
            int y = i / inputWidth;

            grid.put(new IntIntImmutablePair(x, y), InputIngredient.of(index, 3 * y + x, stacks));
        }

        List<InputIngredient<EntryStack<?>>> ingredients = new ArrayList<>(craftingWidth * craftingHeight);

        for (int i = 0, n = craftingWidth * craftingHeight; i < n; i++) {
            ingredients.add(InputIngredient.empty(i));
        }

        for (int x = 0; x < craftingWidth; x++) {
            for (int y = 0; y < craftingHeight; y++) {
                InputIngredient<EntryStack<?>> ingredient = grid.get(new IntIntImmutablePair(x, y));

                if (ingredient != null) {
                    int index = craftingWidth * y + x;
                    ingredients.set(index, ingredient);
                }
            }
        }

        return ingredients;
    }

    @Override
    public List<EntryIngredient> getOrganisedInputEntries(int menuWidth, int menuHeight) {
        List<EntryIngredient> entries = new ArrayList<>((menuWidth * menuHeight) + 1);

        for (int i = 0; i < menuWidth * menuHeight; i++) {
            entries.add(EntryIngredient.empty());
        }

        for (int i = 0; i < this.inputs.size(); i++) {
            entries.set(CraftingDisplay.getSlotWithSize(this, i, menuWidth), this.inputs.get(i));
        }

        entries.set(entries.size() - 1, this.patterns);

        return entries;
    }
}
