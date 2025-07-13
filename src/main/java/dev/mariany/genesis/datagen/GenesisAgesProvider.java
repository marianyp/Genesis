package dev.mariany.genesis.datagen;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.advancement.criterion.CompleteTrialSpawnerCriteria;
import dev.mariany.genesis.advancement.criterion.ItemBrokenCriterion;
import dev.mariany.genesis.age.Age;
import dev.mariany.genesis.age.AgeCategory;
import dev.mariany.genesis.age.AgeEntry;
import dev.mariany.genesis.block.GenesisBlocks;
import dev.mariany.genesis.item.GenesisItems;
import dev.mariany.genesis.tag.GenesisTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.criterion.OnKilledCriterion;
import net.minecraft.advancement.criterion.PlayerGeneratesContainerLootCriterion;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.PlayerPredicate;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;
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

    private static final Identifier STORY_SURVIVAL = of(AgeCategory.STORY, "survival");
    private static final Identifier STORY_NETHER = of(AgeCategory.STORY, "nether");
    private static final Identifier STORY_TRIAL = of(AgeCategory.STORY, "trial");
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
        generateStoryAges(registries, consumer);
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
                        Text.translatable("age.genesis.armor.copper.description")
                )
                .criterion("broken_leather", ItemBrokenCriterion.Conditions.create(
                        itemLookup.getOrThrow(GenesisTags.Items.LEATHER_ARMOR))
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
                .criterion("broken_copper", ItemBrokenCriterion.Conditions.create(
                        itemLookup.getOrThrow(GenesisTags.Items.COPPER_ARMOR))
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
                .criterion("broken_iron", ItemBrokenCriterion.Conditions.create(
                        itemLookup.getOrThrow(GenesisTags.Items.IRON_ARMOR))
                )
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
                .requireAge(TOOLS_IRON)
                .requireAge(ARMOR_IRON)
                .itemUnlocks(Ingredient.ofTag(itemLookup.getOrThrow(GenesisTags.Items.FURNACES)))
                .build(consumer, BLOCKS_FURNACE);
    }

    private void generateStoryAges(RegistryWrapper.WrapperLookup registries, Consumer<AgeEntry> consumer) {
        RegistryWrapper.Impl<EntityType<?>> entityLookup = registries.getOrThrow(RegistryKeys.ENTITY_TYPE);

        Age.Builder.create()
                .display(
                        Items.GRASS_BLOCK,
                        Text.translatable("age.genesis.survival"),
                        Text.empty()
                )
                .build(consumer, STORY_SURVIVAL);

        Age.Builder.create()
                .dimensionUnlocks(World.NETHER)
                .parent(STORY_SURVIVAL)
                .requireAge(TOOLS_DIAMOND)
                .display(
                        Items.NETHERRACK,
                        Text.translatable("age.genesis.nether"),
                        Text.translatable("age.genesis.story.nether.description")
                )
                .build(consumer, STORY_NETHER);

        Age.Builder.create()
                .itemUnlocks(Ingredient.ofItem(GenesisItems.ANVIL_CLAY_CAST))
                .parent(STORY_NETHER)
                .parentOptional()
                .criterion("complete_ominous_trial_spawner", CompleteTrialSpawnerCriteria.Conditions.create(true))
                .display(
                        Items.COPPER_BLOCK,
                        Text.translatable("age.genesis.trial"),
                        Text.translatable("age.genesis.story.trial.description")
                )
                .build(consumer, STORY_TRIAL);

        Age.Builder.create()
                .itemUnlocks(Ingredient.ofItem(Items.TRIDENT))
                .parent(STORY_TRIAL)
                .parentOptional()
                .criterion("killed_three_guardians",
                        OnKilledCriterion.Conditions.createPlayerKilledEntity(
                                EntityPredicate.Builder.create().type(entityLookup, EntityType.ELDER_GUARDIAN),
                                DamageSourcePredicate.Builder.create().sourceEntity(
                                        EntityPredicate.Builder.create().typeSpecific(
                                                PlayerPredicate.Builder.create().stat(
                                                        Stats.KILLED,
                                                        EntityType.ELDER_GUARDIAN.getRegistryEntry(),
                                                        NumberRange.IntRange.atLeast(2)).build()
                                        )
                                )
                        )
                )
                .display(
                        Items.PRISMARINE_BRICKS,
                        Text.translatable("age.genesis.ocean"),
                        Text.translatable("age.genesis.story.ocean.description")
                )
                .build(consumer, STORY_OCEAN);

        Age.Builder.create()
                .itemUnlocks(Ingredient.ofItem(GenesisItems.SHIELD_CLAY_CAST))
                .display(
                        Items.SCULK,
                        Text.translatable("age.genesis.sculk"),
                        Text.translatable("age.genesis.story.sculk.description")
                )
                .parent(STORY_OCEAN)
                .parentOptional()
                .criterion(LOOT_ANCIENT_CITY_REQUIREMENT,
                        PlayerGeneratesContainerLootCriterion.Conditions.create(LootTables.ANCIENT_CITY_CHEST)
                )
                .criterion(LOOT_ANCIENT_CITY_ICE_BOX_REQUIREMENT,
                        PlayerGeneratesContainerLootCriterion.Conditions.create(LootTables.ANCIENT_CITY_ICE_BOX_CHEST)
                )
                .requirements(AdvancementRequirements.anyOf(ANCIENT_CITY_REQUIREMENTS))
                .build(consumer, STORY_SCULK);

        Age.Builder.create()
                .itemUnlocks(Ingredient.ofItem(Items.ENDER_EYE))
                .dimensionUnlocks(World.END)
                .parent(STORY_SCULK)
                .parentOptional()
                .criterion("killed_wither",
                        OnKilledCriterion.Conditions.createPlayerKilledEntity(
                                EntityPredicate.Builder.create().type(entityLookup, EntityType.WITHER)
                        )
                )
                .display(
                        Items.WITHER_SKELETON_SKULL,
                        Text.translatable("age.genesis.wither"),
                        Text.translatable("age.genesis.story.wither.description")
                )
                .build(consumer, STORY_WITHER);

        Age.Builder.create()
                .itemUnlocks(Ingredient.ofItem(Items.ENCHANTED_GOLDEN_APPLE))
                .parent(STORY_WITHER)
                .criterion("killed_ender_dragon",
                        OnKilledCriterion.Conditions.createPlayerKilledEntity(
                                EntityPredicate.Builder.create().type(entityLookup, EntityType.ENDER_DRAGON)
                        )
                )
                .display(
                        Items.DRAGON_EGG,
                        Text.translatable("age.genesis.end"),
                        Text.translatable("age.genesis.story.end.description")
                )
                .build(consumer, STORY_END);
    }
}
