package dev.mariany.genesis.loot;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.item.GenesisItems;
import dev.mariany.genesis.tag.GenesisTags;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableSource;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.entries.TagEntry;
import net.minecraft.world.level.storage.loot.functions.ExplorationMapFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SetNameFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LootTableModifiers {
    private static final Map<ResourceKey<LootTable>, Float> CAST_MAP = new HashMap<>();
    private static final Map<ResourceKey<LootTable>, Float> RARE_CAST_MAP = new HashMap<>();
    private static final List<ResourceKey<LootTable>> SINGLE_RARE_CAST_MAP = new ArrayList<>();
    private static final List<ResourceKey<LootTable>> SINGLE_COMMON_CAST_MAP = new ArrayList<>();
    private static final List<ResourceKey<LootTable>> VILLAGE_HOUSE_LOOT_TABLES = new ArrayList<>();
    private static final Map<ResourceKey<LootTable>, Float> SPAWNS_ENCHANTED_HONEY_BOTTLES = new HashMap<>();

    static {
        CAST_MAP.put(BuiltInLootTables.ANCIENT_CITY, 0.3F);
        CAST_MAP.put(BuiltInLootTables.BURIED_TREASURE, 0.2F);
        CAST_MAP.put(BuiltInLootTables.SIMPLE_DUNGEON, 0.5F);
        CAST_MAP.put(BuiltInLootTables.TRIAL_CHAMBERS_REWARD_COMMON, 0.2F);

        RARE_CAST_MAP.put(BuiltInLootTables.BASTION_BRIDGE, 0.4F);
        RARE_CAST_MAP.put(BuiltInLootTables.BASTION_TREASURE, 1F);
        RARE_CAST_MAP.put(BuiltInLootTables.DESERT_PYRAMID, 0.2F);
        RARE_CAST_MAP.put(BuiltInLootTables.JUNGLE_TEMPLE, 0.7F);
        RARE_CAST_MAP.put(BuiltInLootTables.NETHER_BRIDGE, 0.2F);
        RARE_CAST_MAP.put(BuiltInLootTables.TRIAL_CHAMBERS_REWARD, 0.45F);

        SINGLE_RARE_CAST_MAP.add(BuiltInLootTables.DESERT_PYRAMID_ARCHAEOLOGY);
        SINGLE_RARE_CAST_MAP.add(BuiltInLootTables.MASON_GIFT);
        SINGLE_RARE_CAST_MAP.add(BuiltInLootTables.TOOLSMITH_GIFT);
        SINGLE_RARE_CAST_MAP.add(BuiltInLootTables.WEAPONSMITH_GIFT);
        SINGLE_RARE_CAST_MAP.add(BuiltInLootTables.TRAIL_RUINS_ARCHAEOLOGY_RARE);

        SINGLE_COMMON_CAST_MAP.add(BuiltInLootTables.DESERT_WELL_ARCHAEOLOGY);
        SINGLE_COMMON_CAST_MAP.add(BuiltInLootTables.TRAIL_RUINS_ARCHAEOLOGY_COMMON);

        VILLAGE_HOUSE_LOOT_TABLES.add(BuiltInLootTables.VILLAGE_DESERT_HOUSE);
        VILLAGE_HOUSE_LOOT_TABLES.add(BuiltInLootTables.VILLAGE_SAVANNA_HOUSE);
        VILLAGE_HOUSE_LOOT_TABLES.add(BuiltInLootTables.VILLAGE_SNOWY_HOUSE);
        VILLAGE_HOUSE_LOOT_TABLES.add(BuiltInLootTables.VILLAGE_TAIGA_HOUSE);

        SPAWNS_ENCHANTED_HONEY_BOTTLES.put(BuiltInLootTables.TRIAL_CHAMBERS_REWARD_COMMON, 0.4F);
        SPAWNS_ENCHANTED_HONEY_BOTTLES.put(BuiltInLootTables.RUINED_PORTAL, 0.6F);
    }

    public static void bootstrap() {
        Genesis.bootstrapLog("Loot Table Modifiers");
        LootTableEvents.MODIFY.register(LootTableModifiers::modifyLootTable);
    }

    private static void modifyLootTable(
            ResourceKey<LootTable> key,
            LootTable.Builder tableBuilder,
            LootTableSource source,
            HolderLookup.Provider holder
    ) {
        if (!source.isBuiltin()) {
            return;
        }

        if (addTrialChamberMaps(key, tableBuilder)) {
            return;
        }

        if (addCastLootTables(key, tableBuilder)) {
            return;
        }

        addEnchantedHoneyBottles(key, tableBuilder);
    }

    private static boolean addTrialChamberMaps(ResourceKey<LootTable> key, LootTable.Builder tableBuilder) {
        if (key.equals(BuiltInLootTables.VILLAGE_CARTOGRAPHER)) {
            tableBuilder.withPool(
                    LootPool.lootPool()
                            .setRolls(UniformGenerator.between(0, 1))
                            .add(buildTrialChamberMap())
            );
        } else if (VILLAGE_HOUSE_LOOT_TABLES.contains(key)) {
            tableBuilder.withPool(
                    LootPool.lootPool()
                            .setRolls(UniformGenerator.between(0, 1))
                            .add(EmptyLootItem.emptyItem().setWeight(2))
                            .add(buildTrialChamberMap().setWeight(1))
            );
        } else {
            return false;
        }

        return true;
    }

    private static LootPoolSingletonContainer.Builder<?> buildTrialChamberMap() {
        return LootItem.lootTableItem(Items.MAP)
                       .apply(
                               ExplorationMapFunction.makeExplorationMap()
                                                     .setDestination(StructureTags.ON_TRIAL_CHAMBERS_MAPS)
                                                     .setMapDecoration(MapDecorationTypes.TRIAL_CHAMBERS)
                                                     .setZoom((byte) 2)
                                                     .setSkipKnownStructures(false)
                       )
                       .apply(
                               SetNameFunction.setName(
                                       Component.translatable("filled_map.trial_chambers"),
                                       SetNameFunction.Target.ITEM_NAME
                               )
                       );
    }

    private static boolean addCastLootTables(ResourceKey<LootTable> key, LootTable.Builder tableBuilder) {
        if (CAST_MAP.containsKey(key)) {
            tableBuilder.withPool(
                    LootPool.lootPool()
                            .setRolls(UniformGenerator.between(0, 1))
                            .add(LootItem.lootTableItem(GenesisItems.CLAY_SHIELD_CAST))
                            .add(LootItem.lootTableItem(GenesisItems.CLAY_ANVIL_CAST))
                            .when(LootItemRandomChanceCondition.randomChance(CAST_MAP.get(key)))
            );
        } else if (RARE_CAST_MAP.containsKey(key)) {
            tableBuilder.withPool(
                    LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(GenesisItems.CLAY_TOTEM_CAST).setWeight(3))
                            .add(LootItem.lootTableItem(GenesisItems.CLAY_SHIELD_CAST))
                            .add(LootItem.lootTableItem(GenesisItems.CLAY_ANVIL_CAST))
                            .when(LootItemRandomChanceCondition.randomChance(RARE_CAST_MAP.get(key)))
            );
        } else if (SINGLE_RARE_CAST_MAP.contains(key)) {
            tableBuilder.modifyPools(builder -> builder
                    .add(LootItem.lootTableItem(GenesisItems.CLAY_TOTEM_CAST))
                    .add(LootItem.lootTableItem(GenesisItems.CLAY_SHIELD_CAST))
                    .add(LootItem.lootTableItem(GenesisItems.CLAY_ANVIL_CAST)));
        } else if (SINGLE_COMMON_CAST_MAP.contains(key)) {
            tableBuilder.modifyPools(
                    builder -> builder.add(TagEntry.expandTag(GenesisTags.Items.CLAY_TOOL_CASTS))
            );
        } else {
            return false;
        }

        return true;
    }

    private static void addEnchantedHoneyBottles(ResourceKey<LootTable> key, LootTable.Builder tableBuilder) {
        if (addEnchantedHoneyBottleBarter(key, tableBuilder)) {
            return;
        }

        float chance = SPAWNS_ENCHANTED_HONEY_BOTTLES.getOrDefault(key, 0F);

        if (chance <= 0) {
            return;
        }

        tableBuilder.withPool(
                LootPool
                        .lootPool()
                        .setRolls(UniformGenerator.between(0, 1))
                        .add(
                                LootItem
                                        .lootTableItem(GenesisItems.ENCHANTED_HONEY_BOTTLE)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))
                        )
                        .when(LootItemRandomChanceCondition.randomChance(chance))
        );
    }

    private static boolean addEnchantedHoneyBottleBarter(ResourceKey<LootTable> key, LootTable.Builder tableBuilder) {
        if (!key.equals(BuiltInLootTables.PIGLIN_BARTERING)) {
            return false;
        }

        tableBuilder.modifyPools(builder -> builder.add(
                LootItem.lootTableItem(GenesisItems.ENCHANTED_HONEY_BOTTLE)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))
                        .setWeight(40)
        ));

        return true;
    }
}

