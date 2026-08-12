package dev.mariany.genesis.recipe;

import dev.mariany.genesis.block.GenesisBlocks;
import dev.mariany.genesis.item.custom.AssemblyPatternItem;
import dev.mariany.genesis.mixin.accessor.ShapedRecipeAccessor;
import dev.mariany.genesis.recipe.display.AssemblyCraftingRecipeDisplay;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import java.util.List;

public class AssemblyRecipe extends ShapedRecipe {
    final Identifier identifier;
    final ShapedRecipePattern raw;
    final ItemStack result;
    final List<AssemblyPatternItem> patterns;

    public AssemblyRecipe(Identifier identifier, ShapedRecipe recipe, List<AssemblyPatternItem> patterns) {
        this(
                identifier,
                recipe.group(),
                recipe.category(),
                ((ShapedRecipeAccessor) recipe).genesis$raw(),
                ((ShapedRecipeAccessor) recipe).genesis$result().create(),
                recipe.showNotification(),
                patterns
        );
    }

    private AssemblyRecipe(
            Identifier identifier,
            String group,
            CraftingBookCategory category,
            ShapedRecipePattern raw,
            ItemStack result,
            boolean showNotification,
            List<AssemblyPatternItem> patterns
    ) {
        super(
                new Recipe.CommonInfo(showNotification),
                new CraftingRecipe.CraftingBookInfo(category, group),
                raw,
                ItemStackTemplate.fromStack(result)
        );

        this.identifier = identifier;
        this.raw = raw;
        this.result = result;
        this.patterns = patterns;
    }

    public List<AssemblyPatternItem> getPatterns() {
        return this.patterns;
    }

    public AssemblyCraftingRecipeDisplay getDisplay() {
        return new AssemblyCraftingRecipeDisplay(
                this.identifier,
                this.raw.width(),
                this.raw.height(),
                this.raw
                        .ingredients()
                        .stream()
                        .map(ingredient -> ingredient
                                .map(Ingredient::display)
                                .orElse(SlotDisplay.Empty.INSTANCE)
                        )
                        .toList(),
                new SlotDisplay.Composite(
                        this.patterns
                                .stream()
                                .map(pattern -> (SlotDisplay) new SlotDisplay.ItemSlotDisplay(pattern))
                                .toList()
                ),
                new SlotDisplay.ItemStackSlotDisplay(ItemStackTemplate.fromStack(this.result)),
                new SlotDisplay.ItemSlotDisplay(GenesisBlocks.ASSEMBLY_TABLE.asItem())
        );
    }

    @Override
    public RecipeType<CraftingRecipe> getType() {
        return GenesisRecipeTypes.ASSEMBLY;
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(getDisplay());
    }
}
