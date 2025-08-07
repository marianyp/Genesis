package dev.mariany.genesis.recipe;

import dev.mariany.genesis.item.custom.AssemblyPatternItem;
import dev.mariany.genesis.mixin.accessor.ShapedRecipeAccessor;
import dev.mariany.genesis.recipe.display.AssemblyCraftingRecipeDisplay;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.display.RecipeDisplay;
import net.minecraft.recipe.display.SlotDisplay;

import java.util.List;

public class AssemblyRecipe extends ShapedRecipe {
    final RawShapedRecipe raw;
    final ItemStack result;
    final List<AssemblyPatternItem> patterns;

    public AssemblyRecipe(ShapedRecipe recipe, List<AssemblyPatternItem> patterns) {
        this(
                recipe.getGroup(),
                recipe.getCategory(),
                ((ShapedRecipeAccessor) recipe).genesis$raw(),
                ((ShapedRecipeAccessor) recipe).genesis$result(),
                recipe.showNotification(),
                patterns
        );
    }

    private AssemblyRecipe(
            String group,
            CraftingRecipeCategory category,
            RawShapedRecipe raw,
            ItemStack result,
            boolean showNotification,
            List<AssemblyPatternItem> patterns
    ) {
        super(group, category, raw, result, showNotification);

        this.raw = raw;
        this.result = result;
        this.patterns = patterns;
    }

    public List<AssemblyPatternItem> getPatterns() {
        return this.patterns;
    }

    public ItemStack craft() {
        return this.result.copy();
    }

    @Override
    public RecipeType<CraftingRecipe> getType() {
        return GenesisRecipeTypes.ASSEMBLY;
    }

    @Override
    public List<RecipeDisplay> getDisplays() {
        return List.of(
                new AssemblyCraftingRecipeDisplay(
                        this.raw.getWidth(),
                        this.raw.getHeight(),
                        this.raw
                                .getIngredients()
                                .stream()
                                .map(ingredient -> ingredient
                                        .map(Ingredient::toDisplay)
                                        .orElse(SlotDisplay.EmptySlotDisplay.INSTANCE)
                                )
                                .toList(),
                        new SlotDisplay.StackSlotDisplay(this.result),
                        new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE)
                )
        );
    }
}
