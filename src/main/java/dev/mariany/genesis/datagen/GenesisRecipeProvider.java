package dev.mariany.genesis.datagen;

import dev.mariany.genesis.item.GenesisItems;
import dev.mariany.genesis.tag.GenesisTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipe.CookingRecipeJsonBuilder;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class GenesisRecipeProvider extends FabricRecipeProvider {
    public GenesisRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter) {
        return new RecipeGenerator(registryLookup, exporter) {
            @Override
            public void generate() {
                this.offerReversibleCompactingRecipesWithCompactingRecipeGroup(
                        RecipeCategory.MISC, GenesisItems.COPPER_NUGGET, RecipeCategory.MISC, Items.COPPER_INGOT, "copper_ingot_from_nuggets", "copper_ingot"
                );

                this.createShaped(RecipeCategory.COMBAT, GenesisItems.COPPER_SWORD)
                        .input('#', Items.STICK)
                        .input('X', GenesisTags.Items.COPPER_TOOL_MATERIALS)
                        .pattern("X")
                        .pattern("X")
                        .pattern("#")
                        .criterion("has_copper_ingot", this.conditionsFromTag(GenesisTags.Items.COPPER_TOOL_MATERIALS))
                        .offerTo(this.exporter);

                this.createShaped(RecipeCategory.TOOLS, GenesisItems.COPPER_SHOVEL)
                        .input('#', Items.STICK)
                        .input('X', GenesisTags.Items.COPPER_TOOL_MATERIALS)
                        .pattern("X")
                        .pattern("#")
                        .pattern("#")
                        .criterion("has_copper_ingot", this.conditionsFromTag(GenesisTags.Items.COPPER_TOOL_MATERIALS))
                        .offerTo(this.exporter);

                this.createShaped(RecipeCategory.TOOLS, GenesisItems.COPPER_PICKAXE)
                        .input('#', Items.STICK)
                        .input('X', GenesisTags.Items.COPPER_TOOL_MATERIALS)
                        .pattern("XXX")
                        .pattern(" # ")
                        .pattern(" # ")
                        .criterion("has_copper_ingot", this.conditionsFromTag(GenesisTags.Items.COPPER_TOOL_MATERIALS))
                        .offerTo(this.exporter);

                this.createShaped(RecipeCategory.TOOLS, GenesisItems.COPPER_AXE)
                        .input('#', Items.STICK)
                        .input('X', GenesisTags.Items.COPPER_TOOL_MATERIALS)
                        .pattern("XX")
                        .pattern("X#")
                        .pattern(" #")
                        .criterion("has_copper_ingot", this.conditionsFromTag(GenesisTags.Items.COPPER_TOOL_MATERIALS))
                        .offerTo(this.exporter);

                this.createShaped(RecipeCategory.COMBAT, GenesisItems.COPPER_HELMET)
                        .input('X', Items.COPPER_INGOT)
                        .pattern("XXX")
                        .pattern("X X")
                        .criterion("has_copper_ingot", this.conditionsFromItem(Items.COPPER_INGOT))
                        .offerTo(this.exporter);

                this.createShaped(RecipeCategory.COMBAT, GenesisItems.COPPER_CHESTPLATE)
                        .input('X', Items.COPPER_INGOT)
                        .pattern("X X")
                        .pattern("XXX")
                        .pattern("XXX")
                        .criterion("has_copper_ingot", this.conditionsFromItem(Items.COPPER_INGOT))
                        .offerTo(this.exporter);

                this.createShaped(RecipeCategory.COMBAT, GenesisItems.COPPER_LEGGINGS)
                        .input('X', Items.COPPER_INGOT)
                        .pattern("XXX")
                        .pattern("X X")
                        .pattern("X X")
                        .criterion("has_copper_ingot", this.conditionsFromItem(Items.COPPER_INGOT))
                        .offerTo(this.exporter);

                this.createShaped(RecipeCategory.COMBAT, GenesisItems.COPPER_BOOTS)
                        .input('X', Items.COPPER_INGOT)
                        .pattern("X X")
                        .pattern("X X")
                        .criterion("has_copper_ingot", this.conditionsFromItem(Items.COPPER_INGOT))
                        .offerTo(this.exporter);

                CookingRecipeJsonBuilder.createSmelting(
                                Ingredient.ofItems(
                                        GenesisItems.COPPER_SWORD,
                                        GenesisItems.COPPER_SHOVEL,
                                        GenesisItems.COPPER_PICKAXE,
                                        GenesisItems.COPPER_AXE,
                                        GenesisItems.COPPER_HOE,
                                        GenesisItems.COPPER_HELMET,
                                        GenesisItems.COPPER_CHESTPLATE,
                                        GenesisItems.COPPER_LEGGINGS,
                                        GenesisItems.COPPER_BOOTS
                                ),
                                RecipeCategory.MISC,
                                GenesisItems.COPPER_NUGGET,
                                0.1F,
                                200
                        )
                        .criterion("has_copper_sword", this.conditionsFromItem(GenesisItems.COPPER_SWORD))
                        .criterion("has_copper_shovel", this.conditionsFromItem(GenesisItems.COPPER_SHOVEL))
                        .criterion("has_copper_pickaxe", this.conditionsFromItem(GenesisItems.COPPER_PICKAXE))
                        .criterion("has_copper_axe", this.conditionsFromItem(GenesisItems.COPPER_AXE))
                        .criterion("has_copper_hoe", this.conditionsFromItem(GenesisItems.COPPER_HOE))
                        .criterion("has_copper_helmet", this.conditionsFromItem(GenesisItems.COPPER_HELMET))
                        .criterion("has_copper_chestplate", this.conditionsFromItem(GenesisItems.COPPER_CHESTPLATE))
                        .criterion("has_copper_leggings", this.conditionsFromItem(GenesisItems.COPPER_LEGGINGS))
                        .criterion("has_copper_boots", this.conditionsFromItem(GenesisItems.COPPER_BOOTS))
                        .offerTo(this.exporter, getSmeltingItemPath(GenesisItems.COPPER_NUGGET));

                CookingRecipeJsonBuilder.createBlasting(
                                Ingredient.ofItems(
                                        GenesisItems.COPPER_SWORD,
                                        GenesisItems.COPPER_SHOVEL,
                                        GenesisItems.COPPER_PICKAXE,
                                        GenesisItems.COPPER_AXE,
                                        GenesisItems.COPPER_HOE,
                                        GenesisItems.COPPER_HELMET,
                                        GenesisItems.COPPER_CHESTPLATE,
                                        GenesisItems.COPPER_LEGGINGS,
                                        GenesisItems.COPPER_BOOTS
                                ),
                                RecipeCategory.MISC,
                                GenesisItems.COPPER_NUGGET,
                                0.1F,
                                100
                        )
                        .criterion("has_copper_sword", this.conditionsFromItem(GenesisItems.COPPER_SWORD))
                        .criterion("has_copper_shovel", this.conditionsFromItem(GenesisItems.COPPER_SHOVEL))
                        .criterion("has_copper_pickaxe", this.conditionsFromItem(GenesisItems.COPPER_PICKAXE))
                        .criterion("has_copper_axe", this.conditionsFromItem(GenesisItems.COPPER_AXE))
                        .criterion("has_copper_hoe", this.conditionsFromItem(GenesisItems.COPPER_HOE))
                        .criterion("has_copper_helmet", this.conditionsFromItem(GenesisItems.COPPER_HELMET))
                        .criterion("has_copper_chestplate", this.conditionsFromItem(GenesisItems.COPPER_CHESTPLATE))
                        .criterion("has_copper_leggings", this.conditionsFromItem(GenesisItems.COPPER_LEGGINGS))
                        .criterion("has_copper_boots", this.conditionsFromItem(GenesisItems.COPPER_BOOTS))
                        .offerTo(this.exporter, getBlastingItemPath(GenesisItems.COPPER_NUGGET));

                this.createShapeless(RecipeCategory.TOOLS, GenesisItems.FLINTS).input(Items.FLINT).input(Items.FLINT)
                        .criterion("has_flint", this.conditionsFromItem(Items.FLINT)).offerTo(this.exporter);
            }
        };
    }

    @Override
    public String getName() {
        return "Genesis Recipes";
    }
}
