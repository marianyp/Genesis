package dev.mariany.genesis.datagen;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.advancement.criterion.ItemBrokenCriterion;
import dev.mariany.genesis.age.Age;
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
    private static final Identifier WOOD = Genesis.id("wood");
    private static final Identifier STONE = Genesis.id("stone");
    private static final Identifier COPPER = Genesis.id("copper");
    private static final Identifier IRON = Genesis.id("iron");
    private static final Identifier DIAMOND = Genesis.id("diamond");
    private static final Identifier LEATHER = Genesis.id("leather");
    private static final Identifier CLAY = Genesis.id("clay");
    private static final Identifier FURNACE = Genesis.id("furnace");

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
        Age.Builder.create(Age.Category.ARMOR)
                .display(
                        Items.LEATHER_CHESTPLATE,
                        Text.translatable("age.genesis.leather"),
                        Text.empty()
                )
                .build(consumer, LEATHER);

        Age.Builder.create(Age.Category.ARMOR)
                .display(
                        GenesisItems.COPPER_CHESTPLATE,
                        Text.translatable("age.genesis.copper"),
                        Text.translatable("age.genesis.copper.armor_description")
                )
                .criterion("broken_leather", ItemBrokenCriterion.Conditions.create(
                        itemLookup.getOrThrow(GenesisTags.Items.LEATHER_ARMOR))
                )
                .addUnlock(Ingredient.ofTag(itemLookup.getOrThrow(GenesisTags.Items.COPPER_ARMOR)))
                .parent(LEATHER)
                .build(consumer, COPPER);

        Age.Builder.create(Age.Category.ARMOR)
                .display(
                        Items.IRON_CHESTPLATE,
                        Text.translatable("age.genesis.iron"),
                        Text.translatable("age.genesis.iron.armor_description")
                )
                .criterion("broken_copper", ItemBrokenCriterion.Conditions.create(
                        itemLookup.getOrThrow(GenesisTags.Items.COPPER_ARMOR))
                )
                .addUnlock(Ingredient.ofTag(itemLookup.getOrThrow(GenesisTags.Items.IRON_ARMOR)))
                .parent(COPPER)
                .build(consumer, IRON);

        Age.Builder.create(Age.Category.ARMOR)
                .display(
                        Items.DIAMOND_CHESTPLATE,
                        Text.translatable("age.genesis.diamond"),
                        Text.translatable("age.genesis.diamond.armor_description")
                )
                .criterion("broken_iron", ItemBrokenCriterion.Conditions.create(
                        itemLookup.getOrThrow(GenesisTags.Items.IRON_ARMOR))
                )
                .addUnlock(Ingredient.ofTag(itemLookup.getOrThrow(GenesisTags.Items.DIAMOND_ARMOR)))
                .parent(IRON)
                .build(consumer, DIAMOND);
    }

    private void generateToolAges(RegistryWrapper.Impl<Item> itemLookup, Consumer<AgeEntry> consumer) {
        Age.Builder.create(Age.Category.TOOLS)
                .display(
                        Items.WOODEN_PICKAXE,
                        Text.translatable("age.genesis.wood"),
                        Text.empty()
                )
                .build(consumer, WOOD);

        Age.Builder.create(Age.Category.TOOLS)
                .display(
                        Items.STONE_PICKAXE,
                        Text.translatable("age.genesis.stone"),
                        Text.translatable("age.genesis.stone.tools_description")
                )
                .criterion("broken_wood", ItemBrokenCriterion.Conditions.create(
                        itemLookup.getOrThrow(GenesisTags.Items.WOODEN_TOOLS))
                )
                .addUnlock(Ingredient.ofTag(itemLookup.getOrThrow(GenesisTags.Items.STONE_TOOLS)))
                .parent(WOOD)
                .build(consumer, STONE);

        Age.Builder.create(Age.Category.TOOLS)
                .display(
                        GenesisItems.COPPER_PICKAXE,
                        Text.translatable("age.genesis.copper"),
                        Text.translatable("age.genesis.copper.tools_description")
                )
                .criterion("broken_stone", ItemBrokenCriterion.Conditions.create(
                        itemLookup.getOrThrow(GenesisTags.Items.STONE_TOOLS))
                )
                .addUnlock(Ingredient.ofTag(itemLookup.getOrThrow(GenesisTags.Items.COPPER_TOOLS)))
                .parent(STONE)
                .build(consumer, COPPER);

        Age.Builder.create(Age.Category.TOOLS)
                .display(
                        Items.IRON_PICKAXE,
                        Text.translatable("age.genesis.iron"),
                        Text.translatable("age.genesis.iron.tools_description")
                )
                .criterion("broken_copper", ItemBrokenCriterion.Conditions.create(
                        itemLookup.getOrThrow(GenesisTags.Items.COPPER_TOOLS))
                )
                .addUnlock(Ingredient.ofTag(itemLookup.getOrThrow(GenesisTags.Items.IRON_TOOLS)))
                .parent(COPPER)
                .build(consumer, IRON);

        Age.Builder.create(Age.Category.TOOLS)
                .display(
                        Items.DIAMOND_PICKAXE,
                        Text.translatable("age.genesis.diamond"),
                        Text.translatable("age.genesis.diamond.tools_description")
                )
                .criterion("broken_iron", ItemBrokenCriterion.Conditions.create(
                        itemLookup.getOrThrow(GenesisTags.Items.IRON_TOOLS))
                )
                .addUnlock(Ingredient.ofTag(itemLookup.getOrThrow(GenesisTags.Items.DIAMOND_TOOLS)))
                .parent(IRON)
                .build(consumer, DIAMOND);
    }

    private void generateBlockAges(RegistryWrapper.Impl<Item> itemLookup, Consumer<AgeEntry> consumer) {
        Age.Builder.create(Age.Category.BLOCKS)
                .display(
                        GenesisBlocks.CLAY_CAULDRON.asItem(),
                        Text.translatable("age.genesis.clay"),
                        Text.empty()
                )
                .build(consumer, CLAY);

        Age.Builder.create(Age.Category.BLOCKS)
                .display(
                        Items.FURNACE,
                        Text.translatable("age.genesis.furnace"),
                        Text.translatable("age.genesis.furnace.blocks_description")
                )
                .requireAge(Age.Category.ARMOR, IRON)
                .requireAge(Age.Category.TOOLS, IRON)
                .addUnlock(Ingredient.ofTag(itemLookup.getOrThrow(GenesisTags.Items.FURNACES)))
                .parent(CLAY)
                .build(consumer, FURNACE);
    }
}
