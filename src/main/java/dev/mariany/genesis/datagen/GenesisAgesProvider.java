package dev.mariany.genesis.datagen;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.advancement.criterion.ItemBrokenCriterion;
import dev.mariany.genesis.age.Age;
import dev.mariany.genesis.age.AgeCategory;
import dev.mariany.genesis.age.AgeEntry;
import dev.mariany.genesis.block.GenesisBlocks;
import dev.mariany.genesis.item.GenesisItems;
import dev.mariany.genesis.tag.GenesisTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class GenesisAgesProvider extends AgesProvider {
    private static final Identifier ARMOR_LEATHER = of(AgeCategory.ARMOR, "leather");
    private static final Identifier ARMOR_COPPER = of(AgeCategory.ARMOR, "copper");
    private static final Identifier ARMOR_IRON = of(AgeCategory.ARMOR, "iron");
    private static final Identifier ARMOR_DIAMOND = of(AgeCategory.ARMOR, "diamond");

    private static final Identifier TOOLS_WOOD = of(AgeCategory.TOOLS, "wood");
    private static final Identifier TOOLS_STONE = of(AgeCategory.TOOLS, "stone");
    private static final Identifier TOOLS_COPPER = of(AgeCategory.TOOLS, "copper");
    private static final Identifier TOOLS_IRON = of(AgeCategory.TOOLS, "iron");
    private static final Identifier TOOLS_DIAMOND = of(AgeCategory.TOOLS, "diamond");

    private static final Identifier BLOCKS_CLAY = of(AgeCategory.BLOCKS, "clay");
    private static final Identifier BLOCKS_FURNACE = of(AgeCategory.BLOCKS, "furnace");

    private static Identifier of(AgeCategory category, String key) {
        return Genesis.id(category.asString() + "/" + key);
    }

    public GenesisAgesProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateAges(RegistryWrapper.WrapperLookup registries, Consumer<AgeEntry> consumer) {
        RegistryWrapper.Impl<Item> itemLookup = registries.getOrThrow(RegistryKeys.ITEM);

        generateArmorAges(itemLookup, consumer);
        generateToolAges(itemLookup, consumer);
        generateBlockAges(itemLookup, consumer);
    }

    private void generateArmorAges(RegistryWrapper.Impl<Item> itemLookup, Consumer<AgeEntry> consumer) {
        Age.Builder.create()
                .display(
                        Items.LEATHER_CHESTPLATE,
                        Text.translatable("age.genesis.leather"),
                        Text.empty()
                )
                .build(consumer, ARMOR_LEATHER);

        Age.Builder.create()
                .display(
                        GenesisItems.COPPER_CHESTPLATE,
                        Text.translatable("age.genesis.copper"),
                        Text.translatable("age.genesis.copper.armor_description")
                )
                .criterion("broken_leather", ItemBrokenCriterion.Conditions.create(
                        itemLookup.getOrThrow(GenesisTags.Items.LEATHER_ARMOR))
                )
                .addUnlock(Ingredient.ofTag(itemLookup.getOrThrow(GenesisTags.Items.COPPER_ARMOR)))
                .parent(ARMOR_LEATHER)
                .build(consumer, ARMOR_COPPER);

        Age.Builder.create()
                .display(
                        Items.IRON_CHESTPLATE,
                        Text.translatable("age.genesis.iron"),
                        Text.translatable("age.genesis.iron.armor_description")
                )
                .criterion("broken_copper", ItemBrokenCriterion.Conditions.create(
                        itemLookup.getOrThrow(GenesisTags.Items.COPPER_ARMOR))
                )
                .addUnlock(Ingredient.ofTag(itemLookup.getOrThrow(GenesisTags.Items.IRON_ARMOR)))
                .addUnlock(Ingredient.ofTag(itemLookup.getOrThrow(GenesisTags.Items.GOLDEN_ARMOR)))
                .parent(ARMOR_COPPER)
                .build(consumer, ARMOR_IRON);

        Age.Builder.create()
                .display(
                        Items.DIAMOND_CHESTPLATE,
                        Text.translatable("age.genesis.diamond"),
                        Text.translatable("age.genesis.diamond.armor_description")
                )
                .criterion("broken_iron", ItemBrokenCriterion.Conditions.create(
                        itemLookup.getOrThrow(GenesisTags.Items.IRON_ARMOR))
                )
                .addUnlock(Ingredient.ofTag(itemLookup.getOrThrow(GenesisTags.Items.DIAMOND_ARMOR)))
                .parent(ARMOR_IRON)
                .build(consumer, ARMOR_DIAMOND);
    }

    private void generateToolAges(RegistryWrapper.Impl<Item> itemLookup, Consumer<AgeEntry> consumer) {
        Age.Builder.create()
                .display(
                        Items.WOODEN_PICKAXE,
                        Text.translatable("age.genesis.wood"),
                        Text.empty()
                )
                .build(consumer, TOOLS_WOOD);

        Age.Builder.create()
                .display(
                        Items.STONE_PICKAXE,
                        Text.translatable("age.genesis.stone"),
                        Text.translatable("age.genesis.stone.tools_description")
                )
                .criterion("broken_wood", ItemBrokenCriterion.Conditions.create(
                        itemLookup.getOrThrow(GenesisTags.Items.WOODEN_TOOLS))
                )
                .addUnlock(Ingredient.ofTag(itemLookup.getOrThrow(GenesisTags.Items.STONE_TOOLS)))
                .parent(TOOLS_WOOD)
                .build(consumer, TOOLS_STONE);

        Age.Builder.create()
                .display(
                        GenesisItems.COPPER_PICKAXE,
                        Text.translatable("age.genesis.copper"),
                        Text.translatable("age.genesis.copper.tools_description")
                )
                .criterion("broken_stone", ItemBrokenCriterion.Conditions.create(
                        itemLookup.getOrThrow(GenesisTags.Items.STONE_TOOLS))
                )
                .addUnlock(Ingredient.ofTag(itemLookup.getOrThrow(GenesisTags.Items.COPPER_TOOLS)))
                .parent(TOOLS_STONE)
                .build(consumer, TOOLS_COPPER);

        Age.Builder.create()
                .display(
                        Items.IRON_PICKAXE,
                        Text.translatable("age.genesis.iron"),
                        Text.translatable("age.genesis.iron.tools_description")
                )
                .criterion("broken_copper", ItemBrokenCriterion.Conditions.create(
                        itemLookup.getOrThrow(GenesisTags.Items.COPPER_TOOLS))
                )
                .addUnlock(Ingredient.ofTag(itemLookup.getOrThrow(GenesisTags.Items.IRON_TOOLS)))
                .addUnlock(Ingredient.ofTag(itemLookup.getOrThrow(GenesisTags.Items.GOLDEN_TOOLS)))
                .parent(TOOLS_COPPER)
                .build(consumer, TOOLS_IRON);

        Age.Builder.create()
                .display(
                        Items.DIAMOND_PICKAXE,
                        Text.translatable("age.genesis.diamond"),
                        Text.translatable("age.genesis.diamond.tools_description")
                )
                .criterion("broken_iron", ItemBrokenCriterion.Conditions.create(
                        itemLookup.getOrThrow(GenesisTags.Items.IRON_TOOLS))
                )
                .addUnlock(Ingredient.ofTag(itemLookup.getOrThrow(GenesisTags.Items.DIAMOND_TOOLS)))
                .parent(TOOLS_IRON)
                .build(consumer, TOOLS_DIAMOND);
    }

    private void generateBlockAges(RegistryWrapper.Impl<Item> itemLookup, Consumer<AgeEntry> consumer) {
        Age.Builder.create()
                .display(
                        GenesisBlocks.KILN.asItem(),
                        Text.translatable("age.genesis.clay"),
                        Text.empty()
                )
                .build(consumer, BLOCKS_CLAY);

        Age.Builder.create()
                .display(
                        Items.FURNACE,
                        Text.translatable("age.genesis.furnace"),
                        Text.translatable("age.genesis.furnace.blocks_description")
                )
                .requireAge(TOOLS_IRON)
                .requireAge(ARMOR_IRON)
                .addUnlock(Ingredient.ofTag(itemLookup.getOrThrow(GenesisTags.Items.FURNACES)))
                .parent(BLOCKS_CLAY)
                .build(consumer, BLOCKS_FURNACE);
    }
}
