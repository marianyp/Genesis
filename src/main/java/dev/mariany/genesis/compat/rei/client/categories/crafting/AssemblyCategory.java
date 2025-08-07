package dev.mariany.genesis.compat.rei.client.categories.crafting;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import dev.mariany.genesis.block.GenesisBlocks;
import dev.mariany.genesis.compat.rei.REICategoryIdentifiers;
import dev.mariany.genesis.compat.rei.display.AssemblyDisplay;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.DisplayMerger;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.InputIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AssemblyCategory implements DisplayCategory<AssemblyDisplay> {
    @Override
    public CategoryIdentifier<? extends AssemblyDisplay> getCategoryIdentifier() {
        return REICategoryIdentifiers.ASSEMBLY;
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(GenesisBlocks.ASSEMBLY_TABLE);
    }

    @Override
    public Text getTitle() {
        return Text.translatable("category.rei.genesis.assembly");
    }

    @Override
    public int getDisplayWidth(AssemblyDisplay display) {
        return 175;
    }

    @Override
    public List<Widget> setupDisplay(AssemblyDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();

        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createArrow(new Point(bounds.x + 102, bounds.y + 24)));
        widgets.add(Widgets.createResultSlotBackground(new Point(bounds.x + 137, bounds.y + 25)));

        widgets.add(createPatternSlot(display, bounds));
        widgets.addAll(addInputSlots(display, bounds));

        widgets.add(
                Widgets.createSlot(new Point(bounds.x + 137, bounds.y + 25))
                        .entries(display.getOutputEntries().getFirst())
                        .disableBackground()
                        .markOutput()
        );

        return widgets;
    }

    private Slot createPatternSlot(AssemblyDisplay display, Rectangle bounds) {
        Slot patternSlot = Widgets.createSlot(new Point(bounds.x + 18, bounds.y + 25)).markInput();
        patternSlot.entries(display.getPatterns());
        return patternSlot;
    }

    private List<Slot> addInputSlots(AssemblyDisplay display, Rectangle bounds) {
        List<InputIngredient<EntryStack<?>>> ingredients = display.getInputIngredients(3, 3);
        List<Slot> slots = new ArrayList<>();

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                int slotX = bounds.x + 43 + x * 18;
                int slotY = bounds.y + 7 + y * 18;

                slots.add(Widgets.createSlot(new Point(slotX, slotY)).markInput());
            }
        }

        for (InputIngredient<EntryStack<?>> ingredient : ingredients) {
            slots.get(ingredient.getIndex()).entries(ingredient.get());
        }

        return slots;
    }

    @Override
    @Nullable
    public DisplayMerger<AssemblyDisplay> getDisplayMerger() {
        return new DisplayMerger<>() {
            @Override
            public boolean canMerge(AssemblyDisplay displayA, AssemblyDisplay displayB) {
                if (displayA.getWidth() != displayB.getWidth()) {
                    return false;
                }

                if (displayA.getHeight() != displayB.getHeight()) {
                    return false;
                }

                if (!displayA.getCategoryIdentifier().equals(displayB.getCategoryIdentifier())) {
                    return false;
                }

                List<EntryIngredient> displayAInputs = displayA.getOrganisedInputEntries(3, 3);
                List<EntryIngredient> displayBInputs = displayB.getOrganisedInputEntries(3, 3);

                if (!equals(displayAInputs, displayBInputs)) {
                    return false;
                }

                return equals(displayA.getOutputEntries(), displayB.getOutputEntries());
            }

            @Override
            public int hashOf(AssemblyDisplay display) {
                return Objects.hash(
                        display.getCategoryIdentifier(),
                        display.getOrganisedInputEntries(3, 3),
                        display.getOutputEntries()
                );
            }

            private <T> boolean equals(List<T> inputsA, List<T> inputsB) {
                Multiset<T> multisetA = HashMultiset.create(inputsA);
                Multiset<T> multisetB = HashMultiset.create(inputsB);
                return multisetA.equals(multisetB);
            }
        };
    }
}
