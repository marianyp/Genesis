package dev.mariany.genesis.datagen;

import dev.mariany.genesis.block.GenesisBlocks;
import dev.mariany.genesis.item.GenesisItems;
import dev.mariany.genesis.tag.GenesisTags;
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
                this.offerReversibleCompactingRecipesWithCompactingRecipeGroup(
                        RecipeCategory.MISC,
                        GenesisItems.COPPER_NUGGET,
                        RecipeCategory.MISC,
                        Items.COPPER_INGOT,
                        "copper_ingot_from_nuggets",
                        "copper_ingot"
                );

                this.createShapeless(RecipeCategory.TOOLS, GenesisItems.FLINTS).input(Items.FLINT).input(Items.FLINT)
                        .criterion(hasItem(Items.FLINT), this.conditionsFromItem(Items.FLINT)).offerTo(this.exporter);

                this.createShapeless(RecipeCategory.MISC, GenesisBlocks.ASSEMBLY_TABLE).input(Items.CRAFTING_TABLE)
                        .criterion(hasItem(Items.CRAFTING_TABLE), this.conditionsFromItem(Items.CRAFTING_TABLE))
                        .offerTo(this.exporter);

                this.registerCopperTools();
                this.registerCopperArmor();
                this.registerCopperGearCooking();
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

            private void registerCopperGearCooking() {
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
                        .criterion(hasItem(GenesisItems.COPPER_SWORD),
                                this.conditionsFromItem(GenesisItems.COPPER_SWORD))
                        .criterion(hasItem(GenesisItems.COPPER_SHOVEL),
                                this.conditionsFromItem(GenesisItems.COPPER_SHOVEL))
                        .criterion(hasItem(GenesisItems.COPPER_PICKAXE),
                                this.conditionsFromItem(GenesisItems.COPPER_PICKAXE))
                        .criterion(hasItem(GenesisItems.COPPER_AXE), this.conditionsFromItem(GenesisItems.COPPER_AXE))
                        .criterion(hasItem(GenesisItems.COPPER_HOE), this.conditionsFromItem(GenesisItems.COPPER_HOE))
                        .criterion(hasItem(GenesisItems.COPPER_HELMET),
                                this.conditionsFromItem(GenesisItems.COPPER_HELMET))
                        .criterion(hasItem(GenesisItems.COPPER_CHESTPLATE),
                                this.conditionsFromItem(GenesisItems.COPPER_CHESTPLATE))
                        .criterion(hasItem(GenesisItems.COPPER_LEGGINGS),
                                this.conditionsFromItem(GenesisItems.COPPER_LEGGINGS))
                        .criterion(hasItem(GenesisItems.COPPER_BOOTS),
                                this.conditionsFromItem(GenesisItems.COPPER_BOOTS))
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
                        .criterion(hasItem(GenesisItems.COPPER_SWORD),
                                this.conditionsFromItem(GenesisItems.COPPER_SWORD))
                        .criterion(hasItem(GenesisItems.COPPER_SHOVEL),
                                this.conditionsFromItem(GenesisItems.COPPER_SHOVEL))
                        .criterion(hasItem(GenesisItems.COPPER_PICKAXE),
                                this.conditionsFromItem(GenesisItems.COPPER_PICKAXE))
                        .criterion(hasItem(GenesisItems.COPPER_AXE), this.conditionsFromItem(GenesisItems.COPPER_AXE))
                        .criterion(hasItem(GenesisItems.COPPER_HOE), this.conditionsFromItem(GenesisItems.COPPER_HOE))
                        .criterion(hasItem(GenesisItems.COPPER_HELMET),
                                this.conditionsFromItem(GenesisItems.COPPER_HELMET))
                        .criterion(hasItem(GenesisItems.COPPER_CHESTPLATE),
                                this.conditionsFromItem(GenesisItems.COPPER_CHESTPLATE))
                        .criterion(hasItem(GenesisItems.COPPER_LEGGINGS),
                                this.conditionsFromItem(GenesisItems.COPPER_LEGGINGS))
                        .criterion(hasItem(GenesisItems.COPPER_BOOTS),
                                this.conditionsFromItem(GenesisItems.COPPER_BOOTS))
                        .offerTo(this.exporter, getBlastingItemPath(GenesisItems.COPPER_NUGGET));
            }

            private void registerCopperArmor() {
                this.createShaped(RecipeCategory.COMBAT, GenesisItems.COPPER_HELMET)
                        .input('X', Items.COPPER_INGOT)
                        .pattern("XXX")
                        .pattern("X X")
                        .criterion(hasItem(Items.COPPER_INGOT), this.conditionsFromItem(Items.COPPER_INGOT))
                        .offerTo(this.exporter);

                this.createShaped(RecipeCategory.COMBAT, GenesisItems.COPPER_CHESTPLATE)
                        .input('X', Items.COPPER_INGOT)
                        .pattern("X X")
                        .pattern("XXX")
                        .pattern("XXX")
                        .criterion(hasItem(Items.COPPER_INGOT), this.conditionsFromItem(Items.COPPER_INGOT))
                        .offerTo(this.exporter);

                this.createShaped(RecipeCategory.COMBAT, GenesisItems.COPPER_LEGGINGS)
                        .input('X', Items.COPPER_INGOT)
                        .pattern("XXX")
                        .pattern("X X")
                        .pattern("X X")
                        .criterion(hasItem(Items.COPPER_INGOT), this.conditionsFromItem(Items.COPPER_INGOT))
                        .offerTo(this.exporter);

                this.createShaped(RecipeCategory.COMBAT, GenesisItems.COPPER_BOOTS)
                        .input('X', Items.COPPER_INGOT)
                        .pattern("X X")
                        .pattern("X X")
                        .criterion(hasItem(Items.COPPER_INGOT), this.conditionsFromItem(Items.COPPER_INGOT))
                        .offerTo(this.exporter);
            }

            private void registerCopperTools() {
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

                this.createShaped(RecipeCategory.TOOLS, GenesisItems.COPPER_HOE)
                        .input('#', Items.STICK)
                        .input('X', GenesisTags.Items.COPPER_TOOL_MATERIALS)
                        .pattern("XX")
                        .pattern(" #")
                        .pattern(" #")
                        .criterion("has_copper_ingot", this.conditionsFromTag(GenesisTags.Items.COPPER_TOOL_MATERIALS))
                        .offerTo(this.exporter);
            }
        };
    }

    @Override
    public String getName() {
        return "Genesis Recipes";
    }
}
