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
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.criterion.*;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;
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
    private static final Identifier BLOCKS_BED = of(AgeCategory.BLOCKS, "bed");

    private static final Identifier STORY_SURVIVAL = of(AgeCategory.STORY, "survival");
    private static final Identifier STORY_GARDEN = of(AgeCategory.STORY, "garden");
    private static final Identifier STORY_NETHER = of(AgeCategory.STORY, "nether");
    private static final Identifier STORY_OCEAN = of(AgeCategory.STORY, "ocean");
    private static final Identifier STORY_SCULK = of(AgeCategory.STORY, "sculk");
    private static final Identifier STORY_WITHER = of(AgeCategory.STORY, "wither");
    private static final Identifier STORY_END = of(AgeCategory.STORY, "end");

    private static final String LOOT_ANCIENT_CITY_REQUIREMENT = "loot_ancient_city";
    private static final String LOOT_ANCIENT_CITY_ICE_BOX_REQUIREMENT = "loot_ancient_city_ice_box";
    private static final List<String> ANCIENT_CITY_REQUIREMENTS = List.of(
            LOOT_ANCIENT_CITY_REQUIREMENT,
            LOOT_ANCIENT_CITY_ICE_BOX_REQUIREMENT
    );

    private static final int TICKS_PER_SECOND = 20;
    private static final int SECONDS_PER_MINUTE = 60;
    private static final int MINUTES_PER_DAY = 20;

    private static final int TICKS_PER_DAY = TICKS_PER_SECOND * SECONDS_PER_MINUTE * MINUTES_PER_DAY;

    private static final int BED_AGE_SURVIVE_DAYS = 5;
    private static final int BED_AGE_SURVIVE_TICKS = BED_AGE_SURVIVE_DAYS * TICKS_PER_DAY;


    private static Identifier of(AgeCategory category, String key) {
        return Genesis.id(category.asString() + "/" + key);
    }

    public GenesisAgesProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateAges(RegistryWrapper.WrapperLookup registries, Consumer<AgeEntry> consumer) {
        RegistryWrapper.Impl<Item> itemLookup = registries.getOrThrow(RegistryKeys.ITEM);
        RegistryWrapper.Impl<EntityType<?>> entityLookup = registries.getOrThrow(RegistryKeys.ENTITY_TYPE);

        generateArmorAges(itemLookup, consumer);
        generateToolAges(itemLookup, consumer);
        generateBlockAges(itemLookup, consumer);
        generateStoryAges(entityLookup, consumer);
    }

    private void generateArmorAges(
            RegistryWrapper.Impl<Item> itemLookup,
            Consumer<AgeEntry> consumer
    ) {
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
                        Text.translatable("age.genesis.armor.copper.description")
                )
                .requireTrialWearing(
                        itemLookup,
                        false,
                        Items.LEATHER_HELMET,
                        Items.LEATHER_CHESTPLATE,
                        Items.LEATHER_LEGGINGS,
                        Items.LEATHER_BOOTS
                )
                .itemUnlocks(Ingredient.ofTag(itemLookup.getOrThrow(GenesisTags.Items.COPPER_ARMOR)))
                .parent(ARMOR_LEATHER)
                .build(consumer, ARMOR_COPPER);

        Age.Builder.create()
                .display(
                        Items.IRON_CHESTPLATE,
                        Text.translatable("age.genesis.iron"),
                        Text.translatable("age.genesis.armor.iron.description")
                )
                .requireTrialWearing(
                        itemLookup,
                        true,
                        GenesisItems.COPPER_HELMET,
                        GenesisItems.COPPER_CHESTPLATE,
                        GenesisItems.COPPER_LEGGINGS,
                        GenesisItems.COPPER_BOOTS
                )
                .itemUnlocks(Ingredient.ofTag(itemLookup.getOrThrow(GenesisTags.Items.IRON_ARMOR)))
                .itemUnlocks(Ingredient.ofTag(itemLookup.getOrThrow(GenesisTags.Items.GOLDEN_ARMOR)))
                .parent(ARMOR_COPPER)
                .build(consumer, ARMOR_IRON);

        Age.Builder.create()
                .display(
                        Items.DIAMOND_CHESTPLATE,
                        Text.translatable("age.genesis.diamond"),
                        Text.translatable("age.genesis.armor.diamond.description")
                )
                .criterion("completed_raid", Criteria.HERO_OF_THE_VILLAGE.create(new TickCriterion.Conditions(
                        Optional.empty()
                )))
                .itemUnlocks(Ingredient.ofTag(itemLookup.getOrThrow(GenesisTags.Items.DIAMOND_ARMOR)))
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
                        Text.translatable("age.genesis.tools.stone.description")
                )
                .criterion("broken_wood", ItemBrokenCriterion.Conditions.create(
                        itemLookup.getOrThrow(GenesisTags.Items.WOODEN_TOOLS))
                )
                .itemUnlocks(Ingredient.ofTag(itemLookup.getOrThrow(GenesisTags.Items.STONE_TOOLS)))
                .parent(TOOLS_WOOD)
                .build(consumer, TOOLS_STONE);

        Age.Builder.create()
                .display(
                        GenesisItems.COPPER_PICKAXE,
                        Text.translatable("age.genesis.copper"),
                        Text.translatable("age.genesis.tools.copper.description")
                )
                .criterion("broken_stone", ItemBrokenCriterion.Conditions.create(
                        itemLookup.getOrThrow(GenesisTags.Items.STONE_TOOLS))
                )
                .itemUnlocks(Ingredient.ofTag(itemLookup.getOrThrow(GenesisTags.Items.COPPER_TOOLS)))
                .parent(TOOLS_STONE)
                .build(consumer, TOOLS_COPPER);

        Age.Builder.create()
                .display(
                        Items.IRON_PICKAXE,
                        Text.translatable("age.genesis.iron"),
                        Text.translatable("age.genesis.tools.iron.description")
                )
                .criterion("broken_copper", ItemBrokenCriterion.Conditions.create(
                        itemLookup.getOrThrow(GenesisTags.Items.COPPER_TOOLS))
                )
                .itemUnlocks(Ingredient.ofTag(itemLookup.getOrThrow(GenesisTags.Items.IRON_TOOLS)))
                .itemUnlocks(Ingredient.ofTag(itemLookup.getOrThrow(GenesisTags.Items.GOLDEN_TOOLS)))
                .parent(TOOLS_COPPER)
                .build(consumer, TOOLS_IRON);

        Age.Builder.create()
                .display(
                        Items.DIAMOND_PICKAXE,
                        Text.translatable("age.genesis.diamond"),
                        Text.translatable("age.genesis.tools.diamond.description")
                )
                .criterion("broken_iron", ItemBrokenCriterion.Conditions.create(
                        itemLookup.getOrThrow(GenesisTags.Items.IRON_TOOLS))
                )
                .itemUnlocks(Ingredient.ofTag(itemLookup.getOrThrow(GenesisTags.Items.DIAMOND_TOOLS)))
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
                        Text.translatable("age.genesis.blocks.furnace.description")
                )
                .parent(BLOCKS_CLAY)
                .parentOptional()
                .requireAge(TOOLS_IRON)
                .requireAge(ARMOR_IRON)
                .itemUnlocks(Ingredient.ofTag(itemLookup.getOrThrow(GenesisTags.Items.FURNACES)))
                .build(consumer, BLOCKS_FURNACE);

        Age.Builder.create()
                .display(
                        Items.RED_BED,
                        Text.translatable("age.genesis.bed"),
                        Text.translatable("age.genesis.blocks.bed.description")
                )
                .parent(BLOCKS_FURNACE)
                .parentOptional()
                .requireTimePlayed(BED_AGE_SURVIVE_TICKS)
                .itemUnlocks(Ingredient.ofTag(itemLookup.getOrThrow(ItemTags.BEDS)))
                .build(consumer, BLOCKS_BED);
    }

    private void generateStoryAges(
            RegistryWrapper.Impl<EntityType<?>> entityLookup,
            Consumer<AgeEntry> consumer
    ) {

        Age.Builder.create()
                .display(
                        Items.CREEPER_SPAWN_EGG,
                        Text.translatable("age.genesis.survival"),
                        Text.empty()
                )
                .build(consumer, STORY_SURVIVAL);

        Age.Builder.create()
                .itemUnlocks(Ingredient.ofItem(GenesisItems.CLAY_SHIELD_CAST))
                .parent(STORY_SURVIVAL)
                .parentOptional()
                .criterion("killed_creaking",
                        OnKilledCriterion.Conditions.createPlayerKilledEntity(
                                EntityPredicate.Builder.create().type(entityLookup, EntityType.CREAKING)
                        )
                )
                .display(
                        Items.CREAKING_SPAWN_EGG,
                        Text.translatable("age.genesis.garden"),
                        Text.translatable("age.genesis.story.garden.description")
                )
                .build(consumer, STORY_GARDEN);

        Age.Builder.create()
                .dimensionUnlocks(World.NETHER)
                .parent(STORY_GARDEN)
                .parentOptional()
                .criterion("has_enchanted", EnchantedItemCriterion.Conditions.any())
                .display(
                        Items.ZOMBIFIED_PIGLIN_SPAWN_EGG,
                        Text.translatable("age.genesis.nether"),
                        Text.translatable("age.genesis.story.nether.description")
                )
                .build(consumer, STORY_NETHER);

        Age.Builder.create()
                .itemUnlocks(Ingredient.ofItem(Items.TRIDENT))
                .parent(STORY_NETHER)
                .parentOptional()
                .requireKill(entityLookup, EntityType.ELDER_GUARDIAN, 3)
                .display(
                        Items.ELDER_GUARDIAN_SPAWN_EGG,
                        Text.translatable("age.genesis.ocean"),
                        Text.translatable("age.genesis.story.ocean.description")
                )
                .build(consumer, STORY_OCEAN);

        Age.Builder.create()
                .itemUnlocks(Ingredient.ofItem(GenesisItems.CLAY_ANVIL_CAST))
                .parent(STORY_OCEAN)
                .parentOptional()
                .criterion(LOOT_ANCIENT_CITY_REQUIREMENT,
                        PlayerGeneratesContainerLootCriterion.Conditions.create(LootTables.ANCIENT_CITY_CHEST)
                )
                .criterion(LOOT_ANCIENT_CITY_ICE_BOX_REQUIREMENT,
                        PlayerGeneratesContainerLootCriterion.Conditions.create(LootTables.ANCIENT_CITY_ICE_BOX_CHEST)
                )
                .requirements(AdvancementRequirements.anyOf(ANCIENT_CITY_REQUIREMENTS))
                .display(
                        Items.WARDEN_SPAWN_EGG,
                        Text.translatable("age.genesis.sculk"),
                        Text.translatable("age.genesis.story.sculk.description")
                )
                .build(consumer, STORY_SCULK);

        Age.Builder.create()
                .parent(STORY_SCULK)
                .parentOptional()
                .itemUnlocks(Ingredient.ofItem(Items.ENCHANTED_GOLDEN_APPLE))
                .criterion("killed_wither",
                        OnKilledCriterion.Conditions.createPlayerKilledEntity(
                                EntityPredicate.Builder.create().type(entityLookup, EntityType.WITHER)
                        )
                )
                .display(
                        Items.WITHER_SPAWN_EGG,
                        Text.translatable("age.genesis.wither"),
                        Text.translatable("age.genesis.story.wither.description")
                )
                .build(consumer, STORY_WITHER);

        Age.Builder.create()
                .parent(STORY_WITHER)
                .dimensionUnlocks(World.END)
                .itemUnlocks(Ingredient.ofItem(Items.ENDER_EYE))
                .requireAge(STORY_NETHER)
                .requireAge(STORY_GARDEN)
                .requireAge(STORY_OCEAN)
                .requireAge(STORY_SCULK)
                .requireAge(STORY_WITHER)
                .display(
                        Items.ENDER_DRAGON_SPAWN_EGG,
                        Text.translatable("age.genesis.end"),
                        Text.translatable("age.genesis.story.end.description")
                )
                .build(consumer, STORY_END);
    }

    @Override
    public String getName() {
        return "Genesis Ages";
    }
}
