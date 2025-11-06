package dev.mariany.genesis.datagen;

import dev.mariany.genesis.block.GenesisBlocks;
import dev.mariany.genesis.item.GenesisItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipe.CookingRecipeJsonBuilder;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class GenesisRecipeProvider extends FabricRecipeProvider {
    public GenesisRecipeProvider(FabricDataOutput output,
                                 CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup registryLookup,
                                                 RecipeExporter exporter) {
        return new RecipeGenerator(registryLookup, exporter) {
            private static String getCampfireItemPath(ItemConvertible item) {
                return getItemPath(item) + "_from_campfire_cooking";
            }

            @Override
            public void generate() {
                this.createShapeless(RecipeCategory.TOOLS, GenesisItems.FLINTS).input(Items.FLINT).input(Items.FLINT)
                        .criterion(hasItem(Items.FLINT), this.conditionsFromItem(Items.FLINT)).offerTo(this.exporter);

                this.createShapeless(RecipeCategory.MISC, GenesisBlocks.ASSEMBLY_TABLE).input(Items.CRAFTING_TABLE)
                        .criterion(hasItem(Items.CRAFTING_TABLE), this.conditionsFromItem(Items.CRAFTING_TABLE))
                        .offerTo(this.exporter);

                this.registerClayCasts();
                this.registerCasts();
                this.registerSpecialCastRecipes();
                this.registerKilnRecipes();
                this.registerRawOreRecipes();
                this.registerCauldronRecipes();
                this.registerSpecialVanillaRecipes();
                this.registerRawBlockRecipes();
            }

            private void registerRawBlockRecipes() {
                this.offerReversibleCompactingRecipes(
                        RecipeCategory.MISC,
                        GenesisItems.RAW_COAL,
                        RecipeCategory.BUILDING_BLOCKS,
                        GenesisBlocks.RAW_COAL_BLOCK
                );

                this.offerReversibleCompactingRecipes(
                        RecipeCategory.MISC,
                        GenesisItems.RAW_DIAMOND,
                        RecipeCategory.BUILDING_BLOCKS,
                        GenesisBlocks.RAW_DIAMOND_BLOCK
                );

                this.offerReversibleCompactingRecipes(
                        RecipeCategory.MISC,
                        GenesisItems.RAW_EMERALD,
                        RecipeCategory.BUILDING_BLOCKS,
                        GenesisBlocks.RAW_EMERALD_BLOCK
                );

                this.offerReversibleCompactingRecipes(
                        RecipeCategory.MISC,
                        GenesisItems.RAW_LAPIS_LAZULI,
                        RecipeCategory.BUILDING_BLOCKS,
                        GenesisBlocks.RAW_LAPIS_LAZULI_BLOCK
                );

                this.offerReversibleCompactingRecipes(
                        RecipeCategory.MISC,
                        GenesisItems.RAW_NETHERITE,
                        RecipeCategory.BUILDING_BLOCKS,
                        GenesisBlocks.RAW_NETHERITE_BLOCK
                );

                this.offerReversibleCompactingRecipes(
                        RecipeCategory.MISC,
                        GenesisItems.RAW_REDSTONE,
                        RecipeCategory.BUILDING_BLOCKS,
                        GenesisBlocks.RAW_REDSTONE_BLOCK
                );
            }

            private void registerSpecialVanillaRecipes() {
                this.createShaped(RecipeCategory.COMBAT, Items.TRIDENT)
                        .pattern(" II")
                        .pattern(" PI")
                        .pattern("P  ")
                        .input('I', Items.IRON_INGOT)
                        .input('P', Items.PRISMARINE_SHARD)
                        .criterion(hasItem(Items.PRISMARINE_SHARD), conditionsFromItem(Items.PRISMARINE_SHARD))
                        .offerTo(this.exporter);

                this.createShaped(RecipeCategory.FOOD, Items.ENCHANTED_GOLDEN_APPLE)
                        .pattern("GGG")
                        .pattern("GAG")
                        .pattern("GGG")
                        .input('G', Items.GOLD_BLOCK)
                        .input('A', Items.APPLE)
                        .criterion(hasItem(Items.APPLE), conditionsFromItem(Items.APPLE))
                        .offerTo(this.exporter);
            }

            private void registerCauldronRecipes() {
                // Craft Clay Cauldron
                this.createShaped(RecipeCategory.DECORATIONS, GenesisBlocks.CLAY_CAULDRON)
                        .pattern("# #")
                        .pattern("###")
                        .input('#', Items.CLAY_BALL)
                        .criterion(hasItem(Items.CLAY_BALL), conditionsFromItem(Items.CLAY_BALL))
                        .offerTo(this.exporter);

                // Cook Clay Cauldron in Campfire
                CookingRecipeJsonBuilder.createCampfireCooking(
                                Ingredient.ofItem(GenesisBlocks.CLAY_CAULDRON),
                                RecipeCategory.MISC,
                                GenesisBlocks.TERRACOTTA_CAULDRON,
                                0.3F,
                                1200
                        )
                        .criterion(hasItem(GenesisBlocks.CLAY_CAULDRON),
                                this.conditionsFromItem(GenesisBlocks.CLAY_CAULDRON))
                        .offerTo(this.exporter, getCampfireItemPath(GenesisBlocks.TERRACOTTA_CAULDRON));

                // Cook Clay Cauldron in Blast Furnace
                CookingRecipeJsonBuilder.createBlasting(
                                Ingredient.ofItem(GenesisBlocks.CLAY_CAULDRON),
                                RecipeCategory.MISC,
                                GenesisBlocks.TERRACOTTA_CAULDRON,
                                0.3F,
                                300
                        )
                        .criterion(hasItem(GenesisBlocks.CLAY_CAULDRON),
                                this.conditionsFromItem(GenesisBlocks.CLAY_CAULDRON))
                        .offerTo(this.exporter, getBlastingItemPath(GenesisBlocks.TERRACOTTA_CAULDRON));
            }

            private void registerRawOreRecipes() {
                this.offerSmeltingAndBlasting(GenesisItems.RAW_NETHERITE, Items.NETHERITE_SCRAP);
                this.offerSmeltingAndBlasting(GenesisItems.RAW_COAL, Items.COAL);
                this.offerSmeltingAndBlasting(GenesisItems.RAW_EMERALD, Items.EMERALD);
                this.offerSmeltingAndBlasting(GenesisItems.RAW_LAPIS_LAZULI, Items.LAPIS_LAZULI);
                this.offerSmeltingAndBlasting(GenesisItems.RAW_DIAMOND, Items.DIAMOND);
                this.offerSmeltingAndBlasting(GenesisItems.RAW_REDSTONE, Items.REDSTONE);
            }

            private void offerSmeltingAndBlasting(ItemConvertible input, ItemConvertible output) {
                Item outputItem = output.asItem();
                RegistryEntry.Reference<Item> outputReference = outputItem.getRegistryEntry();
                Optional<RegistryKey<Item>> optionalReferenceKey = outputReference.getKey();

                if (optionalReferenceKey.isPresent()) {
                    Identifier outputId = optionalReferenceKey.get().getValue();
                    String group = outputId.getPath();
                    this.offerSmelting(List.of(input), RecipeCategory.MISC, output, 0.7F, 200, group);
                    this.offerBlasting(List.of(input), RecipeCategory.MISC, output, 0.7F, 100, group);
                }
            }

            private void registerKilnRecipes() {
                // Craft Clay Kiln
                this.createShaped(RecipeCategory.DECORATIONS, GenesisBlocks.CLAY_KILN)
                        .pattern("###")
                        .pattern("# #")
                        .pattern("###")
                        .input('#', Items.CLAY_BALL)
                        .criterion(hasItem(Items.CLAY_BALL), conditionsFromItem(Items.CLAY_BALL))
                        .offerTo(this.exporter);

                // Cook Clay Kiln in Campfire
                CookingRecipeJsonBuilder.createCampfireCooking(
                                Ingredient.ofItem(GenesisBlocks.CLAY_KILN),
                                RecipeCategory.MISC,
                                GenesisBlocks.KILN,
                                0.3F,
                                1200
                        )
                        .criterion(hasItem(GenesisBlocks.CLAY_KILN), this.conditionsFromItem(GenesisBlocks.CLAY_KILN))
                        .offerTo(this.exporter, getCampfireItemPath(GenesisBlocks.KILN));

                // Cook Clay Kiln in Blast Furnace
                CookingRecipeJsonBuilder.createBlasting(
                                Ingredient.ofItem(GenesisBlocks.CLAY_KILN),
                                RecipeCategory.MISC,
                                GenesisBlocks.KILN,
                                0.3F,
                                300
                        )
                        .criterion(hasItem(GenesisBlocks.CLAY_KILN), this.conditionsFromItem(GenesisBlocks.CLAY_KILN))
                        .offerTo(this.exporter, getBlastingItemPath(GenesisBlocks.KILN));
            }

            private void registerSpecialCastRecipes() {
                this.createShaped(RecipeCategory.MISC, Items.TOTEM_OF_UNDYING)
                        .pattern("GGG")
                        .pattern("EGE")
                        .pattern("GGG")
                        .input('G', Items.GOLD_INGOT)
                        .input('E', Items.EMERALD)
                        .criterion(hasItem(GenesisItems.TOTEM_CAST), conditionsFromItem(GenesisItems.TOTEM_CAST))
                        .offerTo(this.exporter);
            }

            private void registerCasts() {
                registerCast(GenesisItems.CLAY_SWORD_CAST, GenesisItems.SWORD_CAST);
                registerCast(GenesisItems.CLAY_SHOVEL_CAST, GenesisItems.SHOVEL_CAST);
                registerCast(GenesisItems.CLAY_PICKAXE_CAST, GenesisItems.PICKAXE_CAST);
                registerCast(GenesisItems.CLAY_AXE_CAST, GenesisItems.AXE_CAST);
                registerCast(GenesisItems.CLAY_HOE_CAST, GenesisItems.HOE_CAST);
                registerCast(GenesisItems.CLAY_SHIELD_CAST, GenesisItems.SHIELD_CAST);
                registerCast(GenesisItems.CLAY_ANVIL_CAST, GenesisItems.ANVIL_CAST);
                registerCast(GenesisItems.CLAY_TOTEM_CAST, GenesisItems.TOTEM_CAST);
            }

            private void registerCast(Item input, Item output) {
                CookingRecipeJsonBuilder.createCampfireCooking(
                                Ingredient.ofItem(input),
                                RecipeCategory.MISC,
                                output,
                                0.3F,
                                1200
                        )
                        .criterion(hasItem(input), this.conditionsFromItem(input))
                        .offerTo(this.exporter, getCampfireItemPath(output));

                CookingRecipeJsonBuilder.createBlasting(
                                Ingredient.ofItem(input),
                                RecipeCategory.MISC,
                                output,
                                0.3F,
                                300
                        )
                        .criterion(hasItem(input), this.conditionsFromItem(input))
                        .offerTo(this.exporter, getBlastingItemPath(output));
            }

            private void registerClayCasts() {
                this.createShaped(RecipeCategory.MISC, GenesisItems.BLANK_CLAY_CAST)
                        .pattern("###")
                        .pattern("###")
                        .pattern("###")
                        .input('#', Items.CLAY_BALL)
                        .criterion(hasItem(Items.CLAY_BALL), conditionsFromItem(Items.CLAY_BALL))
                        .offerTo(this.exporter);

                this.createShaped(RecipeCategory.MISC, GenesisItems.CLAY_SWORD_CAST)
                        .pattern("F")
                        .pattern("F")
                        .pattern("C")
                        .input('F', Items.FLINT)
                        .input('C', GenesisItems.BLANK_CLAY_CAST)
                        .criterion(hasItem(GenesisItems.BLANK_CLAY_CAST),
                                conditionsFromItem(GenesisItems.BLANK_CLAY_CAST))
                        .offerTo(this.exporter);

                this.createShaped(RecipeCategory.MISC, GenesisItems.CLAY_SHOVEL_CAST)
                        .pattern("F")
                        .pattern("C")
                        .input('F', Items.FLINT)
                        .input('C', GenesisItems.BLANK_CLAY_CAST)
                        .criterion(hasItem(GenesisItems.BLANK_CLAY_CAST),
                                conditionsFromItem(GenesisItems.BLANK_CLAY_CAST))
                        .offerTo(this.exporter);

                this.createShaped(RecipeCategory.MISC, GenesisItems.CLAY_PICKAXE_CAST)
                        .pattern("FFF")
                        .pattern(" C ")
                        .input('F', Items.FLINT)
                        .input('C', GenesisItems.BLANK_CLAY_CAST)
                        .criterion(hasItem(GenesisItems.BLANK_CLAY_CAST),
                                conditionsFromItem(GenesisItems.BLANK_CLAY_CAST))
                        .offerTo(this.exporter);

                this.createShaped(RecipeCategory.MISC, GenesisItems.CLAY_AXE_CAST)
                        .pattern("FF")
                        .pattern("CF")
                        .input('F', Items.FLINT)
                        .input('C', GenesisItems.BLANK_CLAY_CAST)
                        .criterion(hasItem(GenesisItems.BLANK_CLAY_CAST),
                                conditionsFromItem(GenesisItems.BLANK_CLAY_CAST))
                        .offerTo(this.exporter);

                this.createShaped(RecipeCategory.MISC, GenesisItems.CLAY_HOE_CAST)
                        .pattern("FF")
                        .pattern(" C")
                        .input('F', Items.FLINT)
                        .input('C', GenesisItems.BLANK_CLAY_CAST)
                        .criterion(hasItem(GenesisItems.BLANK_CLAY_CAST),
                                conditionsFromItem(GenesisItems.BLANK_CLAY_CAST))
                        .offerTo(this.exporter);

                this.createShaped(RecipeCategory.MISC, GenesisItems.CLAY_SHIELD_CAST)
                        .pattern(" FF")
                        .pattern("CFF")
                        .pattern(" FF")
                        .input('F', Items.FLINT)
                        .input('C', GenesisItems.BLANK_CLAY_CAST)
                        .criterion(hasItem(GenesisItems.BLANK_CLAY_CAST),
                                conditionsFromItem(GenesisItems.BLANK_CLAY_CAST))
                        .offerTo(this.exporter);

                this.createShaped(RecipeCategory.MISC, GenesisItems.CLAY_ANVIL_CAST)
                        .pattern("FFF")
                        .pattern(" C ")
                        .pattern("FFF")
                        .input('F', Items.FLINT)
                        .input('C', GenesisItems.BLANK_CLAY_CAST)
                        .criterion(hasItem(GenesisItems.BLANK_CLAY_CAST),
                                conditionsFromItem(GenesisItems.BLANK_CLAY_CAST))
                        .offerTo(this.exporter);
            }
        };
    }

    @Override
    public String getName() {
        return "Genesis Recipes";
    }
}
