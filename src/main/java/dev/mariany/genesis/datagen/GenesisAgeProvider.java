package dev.mariany.genesis.datagen;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.advancement.criterion.GenesisCriteria;
import dev.mariany.genesis.item.GenesisItems;
import dev.mariany.genesis.tag.GenesisTags;
import dev.mariany.genesisframework.age.Age;
import dev.mariany.genesisframework.age.AgeEntry;
import dev.mariany.genesisframework.age.AgeItemTraits;
import dev.mariany.genesisframework.datagen.AgeProvider;
import dev.mariany.genesisframework.item.trait.ItemTrait;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.predicates.MinMaxBounds;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.advancements.triggers.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class GenesisAgeProvider extends AgeProvider {
    private static final Identifier ARMOR_LEATHER = of(AgeCategory.ARMOR, "leather");
    private static final Identifier ARMOR_COPPER = of(AgeCategory.ARMOR, "copper");
    private static final Identifier ARMOR_IRON = of(AgeCategory.ARMOR, "iron");
    private static final Identifier ARMOR_DIAMOND = of(AgeCategory.ARMOR, "diamond");

    private static final Identifier TOOLS_WOOD = of(AgeCategory.TOOLS, "wood");
    private static final Identifier TOOLS_STONE = of(AgeCategory.TOOLS, "stone");
    private static final Identifier TOOLS_COPPER = of(AgeCategory.TOOLS, "copper");
    private static final Identifier TOOLS_IRON = of(AgeCategory.TOOLS, "iron");
    private static final Identifier TOOLS_DIAMOND = of(AgeCategory.TOOLS, "diamond");

    private static final Identifier PROFICIENCY_ADVENTURE = of(AgeCategory.PROFICIENCY, "adventure");
    private static final Identifier PROFICIENCY_SMELTING = of(AgeCategory.PROFICIENCY, "smelting");
    private static final Identifier PROFICIENCY_COMBAT = of(AgeCategory.PROFICIENCY, "combat");
    private static final Identifier PROFICIENCY_ARCANE = of(AgeCategory.PROFICIENCY, "arcane");

    private static final Identifier STORY_SURVIVAL = of(AgeCategory.STORY, "survival");
    private static final Identifier STORY_WAR = of(AgeCategory.STORY, "war");
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
        return Genesis.id(category + "/" + key);
    }

    public GenesisAgeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateAges(HolderLookup.Provider registries, Consumer<AgeEntry> consumer) {
        HolderLookup.RegistryLookup<Item> itemLookup = registries.lookupOrThrow(Registries.ITEM);
        HolderLookup.RegistryLookup<EntityType<?>> entityLookup = registries.lookupOrThrow(Registries.ENTITY_TYPE);

        generateArmorAges(itemLookup, consumer);
        generateToolAges(itemLookup, consumer);
        generateProficiencyAges(itemLookup, consumer);
        generateStoryAges(entityLookup, consumer);
    }

    private static void generateArmorAges(
            HolderLookup.RegistryLookup<Item> itemLookup,
            Consumer<AgeEntry> consumer
    ) {
        Age.Builder.create()
                   .display(Items.LEATHER_CHESTPLATE)
                   .build(consumer, ARMOR_LEATHER);

        Age.Builder.create()
                   .display(
                           Items.COPPER_CHESTPLATE,
                           Component.translatable("age.genesis.armor.copper.description")
                   )
                   .requireLevel(MinMaxBounds.Ints.atLeast(25))
                   .itemUnlock(itemLookup.getOrThrow(GenesisTags.Items.COPPER_ARMOR))
                   .parent(ARMOR_LEATHER)
                   .build(consumer, ARMOR_COPPER);

        Age.Builder.create()
                   .display(
                           Items.IRON_CHESTPLATE,
                           Component.translatable("age.genesis.armor.iron.description")
                   )
                   .requireTrialWearing(
                           itemLookup,
                           false,
                           Items.COPPER_HELMET,
                           Items.COPPER_CHESTPLATE,
                           Items.COPPER_LEGGINGS,
                           Items.COPPER_BOOTS
                   )
                   .itemUnlock(itemLookup.getOrThrow(GenesisTags.Items.IRON_ARMOR))
                   .itemUnlock(itemLookup.getOrThrow(GenesisTags.Items.GOLDEN_ARMOR))
                   .parent(ARMOR_COPPER)
                   .build(consumer, ARMOR_IRON);

        Age.Builder.create()
                   .display(
                           Items.DIAMOND_CHESTPLATE,
                           Component.translatable("age.genesis.armor.diamond.description")
                   )
                   .requireTrialWearing(
                           itemLookup,
                           true,
                           Items.IRON_HELMET,
                           Items.IRON_CHESTPLATE,
                           Items.IRON_LEGGINGS,
                           Items.IRON_BOOTS
                   )
                   .itemUnlock(itemLookup.getOrThrow(GenesisTags.Items.DIAMOND_ARMOR))
                   .parent(ARMOR_IRON)
                   .build(consumer, ARMOR_DIAMOND);
    }

    private static void generateToolAges(HolderLookup.RegistryLookup<Item> itemLookup, Consumer<AgeEntry> consumer) {
        Age.Builder.create()
                   .display(Items.WOODEN_PICKAXE)
                   .build(consumer, TOOLS_WOOD);

        Age.Builder.create()
                   .display(
                           Items.STONE_PICKAXE,
                           Component.translatable("age.genesis.tools.stone.description")
                   )
                   .requireBreak(itemLookup.getOrThrow(GenesisTags.Items.WOODEN_TOOLS))
                   .itemUnlock(itemLookup.getOrThrow(GenesisTags.Items.STONE_TOOLS))
                   .parent(TOOLS_WOOD)
                   .build(consumer, TOOLS_STONE);

        Age.Builder.create()
                   .display(
                           Items.COPPER_PICKAXE,
                           Component.translatable("age.genesis.tools.copper.description")
                   )
                   .requireBreak(itemLookup.getOrThrow(GenesisTags.Items.STONE_TOOLS))
                   .itemUnlock(itemLookup.getOrThrow(GenesisTags.Items.COPPER_TOOLS))
                   .parent(TOOLS_STONE)
                   .build(consumer, TOOLS_COPPER);

        Age.Builder.create()
                   .display(
                           Items.IRON_PICKAXE,
                           Component.translatable("age.genesis.tools.iron.description")
                   )
                   .requireBreak(itemLookup.getOrThrow(GenesisTags.Items.COPPER_TOOLS))
                   .itemUnlock(itemLookup.getOrThrow(GenesisTags.Items.IRON_TOOLS))
                   .itemUnlock(itemLookup.getOrThrow(GenesisTags.Items.GOLDEN_TOOLS))
                   .parent(TOOLS_COPPER)
                   .build(consumer, TOOLS_IRON);

        Age.Builder.create()
                   .display(
                           Items.DIAMOND_PICKAXE,
                           Component.translatable("age.genesis.tools.diamond.description")
                   )
                   .requireBreak(itemLookup.getOrThrow(GenesisTags.Items.IRON_TOOLS))
                   .itemUnlock(itemLookup.getOrThrow(GenesisTags.Items.DIAMOND_TOOLS))
                   .parent(TOOLS_IRON)
                   .build(consumer, TOOLS_DIAMOND);
    }

    private static void generateProficiencyAges(
            HolderLookup.RegistryLookup<Item> itemLookup,
            Consumer<AgeEntry> consumer
    ) {
        Age.Builder.create()
                   .display(Items.COMPASS)
                   .build(consumer, PROFICIENCY_ADVENTURE);

        generateProficiencyCombatAge(itemLookup, consumer);

        Age.Builder.create()
                   .display(
                           Items.FURNACE,
                           Component.translatable("age.genesis.proficiency.smelting.description")
                   )
                   .parent(PROFICIENCY_COMBAT)
                   .parentOptional()
                   .requireLevel(MinMaxBounds.Ints.atLeast(20))
                   .itemUnlock(itemLookup.getOrThrow(GenesisTags.Items.FURNACES))
                   .build(consumer, PROFICIENCY_SMELTING);

        Age.Builder.create()
                   .display(
                           Items.ENCHANTING_TABLE,
                           Component.translatable("age.genesis.proficiency.arcane.description")
                   )
                   .parent(PROFICIENCY_SMELTING)
                   .parentOptional()
                   .criterion("has_enchanted", EnchantedItemTrigger.TriggerInstance.enchantedItem())
                   .dimensionUnlock(Level.NETHER)
                   .build(consumer, PROFICIENCY_ARCANE);
    }

    private static void generateProficiencyCombatAge(
            HolderLookup.RegistryLookup<Item> itemLookup,
            Consumer<AgeEntry> consumer
    ) {
        AttributeModifier attackDamageAttributeModifier = new AttributeModifier(
                Genesis.id("weapon_age_attack_damage"),
                -0.9,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );

        ItemAttributeModifiers itemAttributeModifiers = ItemAttributeModifiers
                .builder()
                .add(Attributes.ATTACK_DAMAGE, attackDamageAttributeModifier, EquipmentSlotGroup.ANY)
                .build();

        HolderSet.Named<Item> weaponAgeRestrictedWeapons = itemLookup.getOrThrow(
                GenesisTags.Items.WEAPON_AGE_RESTRICTED_WEAPONS
        );

        ItemTrait itemTrait = new ItemTrait(weaponAgeRestrictedWeapons, itemAttributeModifiers);

        AgeItemTraits weaponItemTraits = AgeItemTraits.Builder.create().beforeDone(itemTrait).build();

        Age.Builder.create()
                   .display(
                           Items.IRON_SWORD,
                           Component.translatable("age.genesis.proficiency.combat.description")
                   )
                   .parent(PROFICIENCY_ADVENTURE)
                   .parentOptional()
                   .requireKillHostiles(25)
                   .itemTraits(weaponItemTraits)
                   .build(consumer, PROFICIENCY_COMBAT);
    }

    private static void generateStoryAges(
            HolderLookup.RegistryLookup<EntityType<?>> entityLookup,
            Consumer<AgeEntry> consumer
    ) {

        Age.Builder.create()
                   .display(Items.CREEPER_SPAWN_EGG)
                   .build(consumer, STORY_SURVIVAL);

        Age.Builder.create()
                   .itemUnlock(GenesisItems.CLAY_SHIELD_CAST)
                   .parent(STORY_SURVIVAL)
                   .parentOptional()
                   .criterion(
                           "completed_raid",
                           CriteriaTriggers.RAID_WIN.createCriterion(
                                   new PlayerTrigger.TriggerInstance(Optional.empty())
                           )
                   )
                   .display(
                           Items.RAVAGER_SPAWN_EGG,
                           Component.translatable("age.genesis.story.war.description")
                   )
                   .build(consumer, STORY_WAR);

        Age.Builder.create()
                   .itemUnlock(Items.TRIDENT)
                   .parent(STORY_WAR)
                   .parentOptional()
                   .criterion(
                           "complete_monument",
                           GenesisCriteria.COMPLETE_MONUMENT.createCriterion(
                                   new PlayerTrigger.TriggerInstance(Optional.empty())
                           )
                   )
                   .display(
                           Items.ELDER_GUARDIAN_SPAWN_EGG,
                           Component.translatable("age.genesis.story.ocean.description")
                   )
                   .build(consumer, STORY_OCEAN);

        Age.Builder.create()
                   .itemUnlock(GenesisItems.CLAY_ANVIL_CAST)
                   .parent(STORY_OCEAN)
                   .parentOptional()
                   .criterion(
                           LOOT_ANCIENT_CITY_REQUIREMENT,
                           LootTableTrigger.TriggerInstance.lootTableUsed(BuiltInLootTables.ANCIENT_CITY)
                   )
                   .criterion(
                           LOOT_ANCIENT_CITY_ICE_BOX_REQUIREMENT,
                           LootTableTrigger.TriggerInstance.lootTableUsed(BuiltInLootTables.ANCIENT_CITY_ICE_BOX)
                   )
                   .requirements(AdvancementRequirements.anyOf(ANCIENT_CITY_REQUIREMENTS))
                   .display(
                           Items.WARDEN_SPAWN_EGG,
                           Component.translatable("age.genesis.story.sculk.description")
                   )
                   .build(consumer, STORY_SCULK);

        Age.Builder.create()
                   .parent(STORY_SCULK)
                   .parentOptional()
                   .itemUnlock(Items.ENCHANTED_GOLDEN_APPLE)
                   .criterion(
                           "killed_wither",
                           KilledTrigger.TriggerInstance.playerKilledEntity(
                                   EntityPredicate.Builder.entity().of(entityLookup, EntityTypes.WITHER)
                           )
                   )
                   .display(
                           Items.WITHER_SPAWN_EGG,
                           Component.translatable("age.genesis.story.wither.description")
                   )
                   .build(consumer, STORY_WITHER);

        Age.Builder.create()
                   .parent(STORY_WITHER)
                   .dimensionUnlock(Level.END)
                   .itemUnlock(Items.ENDER_EYE)
                   .requireAge(STORY_WAR)
                   .requireAge(STORY_OCEAN)
                   .requireAge(STORY_SCULK)
                   .requireAge(STORY_WITHER)
                   .display(
                           Items.ENDER_DRAGON_SPAWN_EGG,
                           Component.translatable("age.genesis.story.end.description")
                   )
                   .build(consumer, STORY_END);
    }

    @Override
    public String getName() {
        return "Genesis Ages";
    }

    enum AgeCategory {
        ARMOR("armor"),
        PROFICIENCY("proficiency"),
        TOOLS("tools"),
        STORY("story");

        private final String name;

        AgeCategory(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}
