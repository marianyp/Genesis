package dev.mariany.genesis.recipe.override;

import dev.mariany.genesis.item.GenesisItems;
import dev.mariany.genesis.mixin.accessor.ShapedRecipeAccessor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.*;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;

import java.util.*;

public class ToolRecipeOverrider {
    private final RegistryWrapper.WrapperLookup wrapperLookup;

    public ToolRecipeOverrider(RegistryWrapper.WrapperLookup wrapperLookup) {
        this.wrapperLookup = wrapperLookup;
    }

    public PreparedRecipes override(Collection<RecipeEntry<?>> oldRecipes) {
        List<RecipeEntry<?>> newRecipes = new ArrayList<>();

        for (RecipeEntry<?> entry : oldRecipes) {
            Recipe<?> recipe = entry.value();

            if (recipe instanceof ShapedRecipe shapedRecipe) {
                if (isSword(shapedRecipe)) {
                    Optional<Ingredient> optionalMaterial = getSwordMaterial(shapedRecipe);
                    if (optionalMaterial.isPresent()) {
                        newRecipes.add(createToolRecipe(entry.id(), shapedRecipe, optionalMaterial.get(), GenesisItems.SWORD_CAST));
                        continue;
                    }
                } else if (isShovel(shapedRecipe)) {
                    Optional<Ingredient> optionalMaterial = getShovelMaterial(shapedRecipe);
                    if (optionalMaterial.isPresent()) {
                        newRecipes.add(createToolRecipe(entry.id(), shapedRecipe, optionalMaterial.get(), GenesisItems.SHOVEL_CAST));
                        continue;
                    }
                } else if (isPickaxe(shapedRecipe)) {
                    Optional<Ingredient> optionalMaterial = getPickaxeMaterial(shapedRecipe);
                    if (optionalMaterial.isPresent()) {
                        newRecipes.add(createToolRecipe(entry.id(), shapedRecipe, optionalMaterial.get(), GenesisItems.PICKAXE_CAST));
                        continue;
                    }
                } else if (isAxe(shapedRecipe)) {
                    Optional<Ingredient> optionalMaterial = getAxeMaterial(shapedRecipe);
                    if (optionalMaterial.isPresent()) {
                        newRecipes.add(createToolRecipe(entry.id(), shapedRecipe, optionalMaterial.get(), GenesisItems.AXE_CAST));
                        continue;
                    }
                } else if (isHoe(shapedRecipe)) {
                    Optional<Ingredient> optionalMaterial = getHoeMaterial(shapedRecipe);
                    if (optionalMaterial.isPresent()) {
                        newRecipes.add(createToolRecipe(entry.id(), shapedRecipe, optionalMaterial.get(), GenesisItems.HOE_CAST));
                        continue;
                    }
                }
            }

            newRecipes.add(entry);
        }

        return PreparedRecipes.of(newRecipes);
    }

    private RecipeEntry<?> createToolRecipe(RegistryKey<Recipe<?>> id, ShapedRecipe oldRecipe, Ingredient material, Item cast) {
        return createCustomRecipe(id, oldRecipe, RawShapedRecipe.create(
                        Map.of(
                                'M', material,
                                'S', Ingredient.ofItem(Items.STICK),
                                'C', Ingredient.ofItem(cast)
                        ),
                        "MMM",
                        "SCS",
                        "MMM"
                )
        );
    }

    private RecipeEntry<?> createCustomRecipe(RegistryKey<Recipe<?>> id, ShapedRecipe oldRecipe, RawShapedRecipe raw) {
        ItemStack output = getShapedRecipeResult(oldRecipe);

        ShapedRecipe newRecipe = new ShapedRecipe(
                oldRecipe.getGroup(),
                oldRecipe.getCategory(),
                raw,
                output,
                oldRecipe.showNotification()
        );

        return new RecipeEntry<>(id, newRecipe);
    }

    private ItemStack getShapedRecipeResult(ShapedRecipe shapedRecipe) {
        return shapedRecipe.craft(CraftingRecipeInput.EMPTY, this.wrapperLookup);
    }

    private Optional<Ingredient> validateAndExtractMaterial(ShapedRecipe shapedRecipe, ToolShape pattern) {
        RawShapedRecipe raw = ((ShapedRecipeAccessor) shapedRecipe).genesis$raw();

        if (raw.data.isEmpty()) {
            return Optional.empty();
        }

        RawShapedRecipe.Data data = raw.data.get();
        List<String> recipePattern = data.pattern();

        if (ShapeComparator.haveSameShape(recipePattern, pattern.rows)) {
            Optional<Character> optionalMaterialKey = ShapeComparator.getMappedKey(pattern.rows, recipePattern, pattern.materialKey);

            if (optionalMaterialKey.isPresent()) {
                return Optional.of(data.key().get(optionalMaterialKey.get()));
            }
        }

        return Optional.empty();
    }

    private Optional<Ingredient> getSwordMaterial(ShapedRecipe shapedRecipe) {
        return validateAndExtractMaterial(shapedRecipe, ToolShape.SWORD);
    }

    private Optional<Ingredient> getShovelMaterial(ShapedRecipe shapedRecipe) {
        return validateAndExtractMaterial(shapedRecipe, ToolShape.SHOVEL);
    }

    private Optional<Ingredient> getPickaxeMaterial(ShapedRecipe shapedRecipe) {
        return validateAndExtractMaterial(shapedRecipe, ToolShape.PICKAXE);
    }

    private Optional<Ingredient> getAxeMaterial(ShapedRecipe shapedRecipe) {
        return validateAndExtractMaterial(shapedRecipe, ToolShape.AXE);
    }

    private Optional<Ingredient> getHoeMaterial(ShapedRecipe shapedRecipe) {
        return validateAndExtractMaterial(shapedRecipe, ToolShape.HOE);
    }

    private boolean isSword(ShapedRecipe shapedRecipe) {
        return getShapedRecipeResult(shapedRecipe).isIn(ItemTags.SWORDS);
    }

    private boolean isShovel(ShapedRecipe shapedRecipe) {
        return getShapedRecipeResult(shapedRecipe).isIn(ItemTags.SHOVELS);
    }

    private boolean isPickaxe(ShapedRecipe shapedRecipe) {
        return getShapedRecipeResult(shapedRecipe).isIn(ItemTags.PICKAXES);
    }

    private boolean isAxe(ShapedRecipe shapedRecipe) {
        return getShapedRecipeResult(shapedRecipe).isIn(ItemTags.AXES);
    }

    private boolean isHoe(ShapedRecipe shapedRecipe) {
        return getShapedRecipeResult(shapedRecipe).isIn(ItemTags.HOES);
    }
}
