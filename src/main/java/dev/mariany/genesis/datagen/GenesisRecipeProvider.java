package dev.mariany.genesis.datagen;

import dev.mariany.genesis.block.GenesisBlocks;
import dev.mariany.genesis.item.GenesisItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class GenesisRecipeProvider extends FabricRecipeProvider {
    public GenesisRecipeProvider(
            FabricPackOutput output,
            CompletableFuture<HolderLookup.Provider> registriesFuture
    ) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(
            HolderLookup.Provider registryLookup,
            RecipeOutput exporter
    ) {
        return new RecipeProvider(registryLookup, exporter) {
            private static String getCampfireItemPath(ItemLike item) {
                return getItemName(item) + "_from_campfire_cooking";
            }

            @Override
            public void buildRecipes() {
                this.shapeless(RecipeCategory.TOOLS, GenesisItems.FLINTS)
                    .requires(Items.FLINT)
                    .requires(Items.FLINT)
                    .unlockedBy(getHasName(Items.FLINT), this.has(Items.FLINT))
                    .save(this.output);

                this.shapeless(RecipeCategory.MISC, GenesisBlocks.ASSEMBLY_TABLE)
                    .requires(Items.CRAFTING_TABLE)
                    .unlockedBy(getHasName(Items.CRAFTING_TABLE), this.has(Items.CRAFTING_TABLE))
                    .save(this.output);

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
                this.nineBlockStorageRecipes(
                        RecipeCategory.MISC,
                        GenesisItems.RAW_COAL,
                        RecipeCategory.BUILDING_BLOCKS,
                        GenesisBlocks.RAW_COAL_BLOCK
                );

                this.nineBlockStorageRecipes(
                        RecipeCategory.MISC,
                        GenesisItems.RAW_DIAMOND,
                        RecipeCategory.BUILDING_BLOCKS,
                        GenesisBlocks.RAW_DIAMOND_BLOCK
                );

                this.nineBlockStorageRecipes(
                        RecipeCategory.MISC,
                        GenesisItems.RAW_EMERALD,
                        RecipeCategory.BUILDING_BLOCKS,
                        GenesisBlocks.RAW_EMERALD_BLOCK
                );

                this.nineBlockStorageRecipes(
                        RecipeCategory.MISC,
                        GenesisItems.RAW_LAPIS_LAZULI,
                        RecipeCategory.BUILDING_BLOCKS,
                        GenesisBlocks.RAW_LAPIS_LAZULI_BLOCK
                );

                this.nineBlockStorageRecipes(
                        RecipeCategory.MISC,
                        GenesisItems.RAW_NETHERITE,
                        RecipeCategory.BUILDING_BLOCKS,
                        GenesisBlocks.RAW_NETHERITE_BLOCK
                );

                this.nineBlockStorageRecipes(
                        RecipeCategory.MISC,
                        GenesisItems.RAW_REDSTONE,
                        RecipeCategory.BUILDING_BLOCKS,
                        GenesisBlocks.RAW_REDSTONE_BLOCK
                );
            }

            private void registerSpecialVanillaRecipes() {
                this.shaped(RecipeCategory.COMBAT, Items.TRIDENT)
                    .pattern(" II")
                    .pattern(" PI")
                    .pattern("P  ")
                    .define('I', Items.IRON_INGOT)
                    .define('P', Items.PRISMARINE_SHARD)
                    .unlockedBy(getHasName(Items.PRISMARINE_SHARD), has(Items.PRISMARINE_SHARD))
                    .save(this.output);

                this.shaped(RecipeCategory.FOOD, Items.ENCHANTED_GOLDEN_APPLE)
                    .pattern("GGG")
                    .pattern("GAG")
                    .pattern("GGG")
                    .define('G', Items.GOLD_BLOCK)
                    .define('A', Items.APPLE)
                    .unlockedBy(getHasName(Items.APPLE), has(Items.APPLE))
                    .save(this.output);
            }

            private void registerCauldronRecipes() {
                // Craft Clay Cauldron
                this.shaped(RecipeCategory.DECORATIONS, GenesisBlocks.CLAY_CAULDRON)
                    .pattern("# #")
                    .pattern("###")
                    .define('#', Items.CLAY_BALL)
                    .unlockedBy(getHasName(Items.CLAY_BALL), has(Items.CLAY_BALL))
                    .save(this.output);

                // Cook Clay Cauldron in Campfire
                SimpleCookingRecipeBuilder
                        .campfireCooking(
                                Ingredient.of(GenesisBlocks.CLAY_CAULDRON),
                                RecipeCategory.MISC,
                                GenesisBlocks.TERRACOTTA_CAULDRON,
                                0.3F,
                                1200
                        )
                        .unlockedBy(getHasName(GenesisBlocks.CLAY_CAULDRON), this.has(GenesisBlocks.CLAY_CAULDRON))
                        .save(this.output, getCampfireItemPath(GenesisBlocks.TERRACOTTA_CAULDRON));

                // Cook Clay Cauldron in Blast Furnace
                SimpleCookingRecipeBuilder
                        .blasting(
                                Ingredient.of(GenesisBlocks.CLAY_CAULDRON),
                                RecipeCategory.MISC,
                                CookingBookCategory.MISC,
                                GenesisBlocks.TERRACOTTA_CAULDRON,
                                0.3F,
                                300
                        )
                        .unlockedBy(getHasName(GenesisBlocks.CLAY_CAULDRON), this.has(GenesisBlocks.CLAY_CAULDRON))
                        .save(this.output, getBlastingRecipeName(GenesisBlocks.TERRACOTTA_CAULDRON));
            }

            private void registerRawOreRecipes() {
                this.offerSmeltingAndBlasting(GenesisItems.RAW_NETHERITE, Items.NETHERITE_SCRAP);
                this.offerSmeltingAndBlasting(GenesisItems.RAW_COAL, Items.COAL);
                this.offerSmeltingAndBlasting(GenesisItems.RAW_EMERALD, Items.EMERALD);
                this.offerSmeltingAndBlasting(GenesisItems.RAW_LAPIS_LAZULI, Items.LAPIS_LAZULI);
                this.offerSmeltingAndBlasting(GenesisItems.RAW_DIAMOND, Items.DIAMOND);
                this.offerSmeltingAndBlasting(GenesisItems.RAW_REDSTONE, Items.REDSTONE);
            }

            private void offerSmeltingAndBlasting(ItemLike input, ItemLike output) {
                Item outputItem = output.asItem();
                Holder.Reference<Item> outputReference = outputItem.builtInRegistryHolder();
                Optional<ResourceKey<Item>> optionalReferenceKey = outputReference.unwrapKey();

                if (optionalReferenceKey.isPresent()) {
                    Identifier outputId = optionalReferenceKey.get().identifier();
                    String group = outputId.getPath();

                    this.oreSmelting(
                            List.of(input),
                            RecipeCategory.MISC,
                            CookingBookCategory.MISC,
                            output,
                            0.7F,
                            200,
                            group
                    );

                    this.oreBlasting(
                            List.of(input),
                            RecipeCategory.MISC,
                            CookingBookCategory.MISC,
                            output,
                            0.7F,
                            100,
                            group
                    );
                }
            }

            private void registerKilnRecipes() {
                // Craft Clay Kiln
                this.shaped(RecipeCategory.DECORATIONS, GenesisBlocks.CLAY_KILN)
                    .pattern("###")
                    .pattern("# #")
                    .pattern("###")
                    .define('#', Items.CLAY_BALL)
                    .unlockedBy(getHasName(Items.CLAY_BALL), has(Items.CLAY_BALL))
                    .save(this.output);

                // Cook Clay Kiln in Campfire
                SimpleCookingRecipeBuilder
                        .campfireCooking(
                                Ingredient.of(GenesisBlocks.CLAY_KILN),
                                RecipeCategory.MISC,
                                GenesisBlocks.KILN,
                                0.3F,
                                1200
                        )
                        .unlockedBy(getHasName(GenesisBlocks.CLAY_KILN), this.has(GenesisBlocks.CLAY_KILN))
                        .save(this.output, getCampfireItemPath(GenesisBlocks.KILN));

                // Cook Clay Kiln in Blast Furnace
                SimpleCookingRecipeBuilder
                        .blasting(
                                Ingredient.of(GenesisBlocks.CLAY_KILN),
                                RecipeCategory.MISC,
                                CookingBookCategory.MISC,
                                GenesisBlocks.KILN,
                                0.3F,
                                300
                        )
                        .unlockedBy(getHasName(GenesisBlocks.CLAY_KILN), this.has(GenesisBlocks.CLAY_KILN))
                        .save(this.output, getBlastingRecipeName(GenesisBlocks.KILN));
            }

            private void registerSpecialCastRecipes() {
                this.shaped(RecipeCategory.MISC, Items.TOTEM_OF_UNDYING)
                    .pattern("GGG")
                    .pattern("EGE")
                    .pattern("GGG")
                    .define('G', Items.GOLD_INGOT)
                    .define('E', Items.EMERALD)
                    .unlockedBy(getHasName(GenesisItems.TOTEM_CAST), has(GenesisItems.TOTEM_CAST))
                    .save(this.output);
            }

            private void registerCasts() {
                registerCast(GenesisItems.CLAY_SWORD_CAST, GenesisItems.SWORD_CAST);
                registerCast(GenesisItems.CLAY_SHOVEL_CAST, GenesisItems.SHOVEL_CAST);
                registerCast(GenesisItems.CLAY_PICKAXE_CAST, GenesisItems.PICKAXE_CAST);
                registerCast(GenesisItems.CLAY_AXE_CAST, GenesisItems.AXE_CAST);
                registerCast(GenesisItems.CLAY_HOE_CAST, GenesisItems.HOE_CAST);
                registerCast(GenesisItems.CLAY_SPEAR_CAST, GenesisItems.SPEAR_CAST);
                registerCast(GenesisItems.CLAY_SHIELD_CAST, GenesisItems.SHIELD_CAST);
                registerCast(GenesisItems.CLAY_ANVIL_CAST, GenesisItems.ANVIL_CAST);
                registerCast(GenesisItems.CLAY_TOTEM_CAST, GenesisItems.TOTEM_CAST);
            }

            private void registerCast(Item input, Item output) {
                SimpleCookingRecipeBuilder
                        .campfireCooking(
                                Ingredient.of(input),
                                RecipeCategory.MISC,
                                output,
                                0.3F,
                                1200
                        )
                        .unlockedBy(getHasName(input), this.has(input))
                        .save(this.output, getCampfireItemPath(output));

                SimpleCookingRecipeBuilder
                        .blasting(
                                Ingredient.of(input),
                                RecipeCategory.MISC,
                                CookingBookCategory.MISC,
                                output,
                                0.3F,
                                300
                        )
                        .unlockedBy(getHasName(input), this.has(input))
                        .save(this.output, getBlastingRecipeName(output));
            }

            private void registerClayCasts() {
                this.shaped(RecipeCategory.MISC, GenesisItems.BLANK_CLAY_CAST)
                    .pattern("###")
                    .pattern("###")
                    .pattern("###")
                    .define('#', Items.CLAY_BALL)
                    .unlockedBy(getHasName(Items.CLAY_BALL), has(Items.CLAY_BALL))
                    .save(this.output);

                this.shaped(RecipeCategory.MISC, GenesisItems.CLAY_SWORD_CAST)
                    .pattern("F")
                    .pattern("F")
                    .pattern("C")
                    .define('F', Items.FLINT)
                    .define('C', GenesisItems.BLANK_CLAY_CAST)
                    .unlockedBy(getHasName(GenesisItems.BLANK_CLAY_CAST), has(GenesisItems.BLANK_CLAY_CAST))
                    .save(this.output);

                this.shaped(RecipeCategory.MISC, GenesisItems.CLAY_SHOVEL_CAST)
                    .pattern("F")
                    .pattern("C")
                    .define('F', Items.FLINT)
                    .define('C', GenesisItems.BLANK_CLAY_CAST)
                    .unlockedBy(getHasName(GenesisItems.BLANK_CLAY_CAST), has(GenesisItems.BLANK_CLAY_CAST))
                    .save(this.output);

                this.shaped(RecipeCategory.MISC, GenesisItems.CLAY_PICKAXE_CAST)
                    .pattern("FFF")
                    .pattern(" C ")
                    .define('F', Items.FLINT)
                    .define('C', GenesisItems.BLANK_CLAY_CAST)
                    .unlockedBy(getHasName(GenesisItems.BLANK_CLAY_CAST), has(GenesisItems.BLANK_CLAY_CAST))
                    .save(this.output);

                this.shaped(RecipeCategory.MISC, GenesisItems.CLAY_AXE_CAST)
                    .pattern("FF")
                    .pattern("CF")
                    .define('F', Items.FLINT)
                    .define('C', GenesisItems.BLANK_CLAY_CAST)
                    .unlockedBy(getHasName(GenesisItems.BLANK_CLAY_CAST), has(GenesisItems.BLANK_CLAY_CAST))
                    .save(this.output);

                this.shaped(RecipeCategory.MISC, GenesisItems.CLAY_HOE_CAST)
                    .pattern("FF")
                    .pattern(" C")
                    .define('F', Items.FLINT)
                    .define('C', GenesisItems.BLANK_CLAY_CAST)
                    .unlockedBy(getHasName(GenesisItems.BLANK_CLAY_CAST), has(GenesisItems.BLANK_CLAY_CAST))
                    .save(this.output);

                this.shaped(RecipeCategory.MISC, GenesisItems.CLAY_SPEAR_CAST)
                    .pattern(" F")
                    .pattern("C ")
                    .define('F', Items.FLINT)
                    .define('C', GenesisItems.BLANK_CLAY_CAST)
                    .unlockedBy(getHasName(GenesisItems.BLANK_CLAY_CAST), has(GenesisItems.BLANK_CLAY_CAST))
                    .save(this.output);

                this.shaped(RecipeCategory.MISC, GenesisItems.CLAY_SHIELD_CAST)
                    .pattern(" FF")
                    .pattern("CFF")
                    .pattern(" FF")
                    .define('F', Items.FLINT)
                    .define('C', GenesisItems.BLANK_CLAY_CAST)
                    .unlockedBy(getHasName(GenesisItems.BLANK_CLAY_CAST), has(GenesisItems.BLANK_CLAY_CAST))
                    .save(this.output);

                this.shaped(RecipeCategory.MISC, GenesisItems.CLAY_ANVIL_CAST)
                    .pattern("FFF")
                    .pattern(" C ")
                    .pattern("FFF")
                    .define('F', Items.FLINT)
                    .define('C', GenesisItems.BLANK_CLAY_CAST)
                    .unlockedBy(getHasName(GenesisItems.BLANK_CLAY_CAST), has(GenesisItems.BLANK_CLAY_CAST))
                    .save(this.output);
            }
        };
    }

    @Override
    public String getName() {
        return "Genesis Recipes";
    }
}
