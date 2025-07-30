package dev.mariany.genesis.loot;

import dev.mariany.genesis.item.GenesisItems;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapDecorationTypes;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.EmptyEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.function.ExplorationMapLootFunction;
import net.minecraft.loot.function.SetNameLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.StructureTags;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LootTableModifiers {
    private static final Map<RegistryKey<LootTable>, Float> CAST_MAP = new HashMap<>();
    private static final Map<RegistryKey<LootTable>, Float> TOTEM_CAST_MAP = new HashMap<>();
    private static final List<RegistryKey<LootTable>> SINGLE_RARE_CAST_MAP = new ArrayList<>();
    private static final List<RegistryKey<LootTable>> SINGLE_COMMON_CAST_MAP = new ArrayList<>();
    private static final List<RegistryKey<LootTable>> VILLAGE_HOUSE_LOOT_TABLES = new ArrayList<>();

    static {
        CAST_MAP.put(LootTables.SIMPLE_DUNGEON_CHEST, 0.5F);
        CAST_MAP.put(LootTables.ANCIENT_CITY_CHEST, 0.3F);
        CAST_MAP.put(LootTables.BURIED_TREASURE_CHEST, 0.2F);

        TOTEM_CAST_MAP.put(LootTables.JUNGLE_TEMPLE_CHEST, 0.7F);
        TOTEM_CAST_MAP.put(LootTables.DESERT_PYRAMID_CHEST, 0.45F);

        SINGLE_RARE_CAST_MAP.add(LootTables.DESERT_PYRAMID_ARCHAEOLOGY);
        SINGLE_RARE_CAST_MAP.add(LootTables.TRAIL_RUINS_RARE_ARCHAEOLOGY);
        SINGLE_RARE_CAST_MAP.add(LootTables.HERO_OF_THE_VILLAGE_MASON_GIFT_GAMEPLAY);
        SINGLE_RARE_CAST_MAP.add(LootTables.HERO_OF_THE_VILLAGE_WEAPONSMITH_GIFT_GAMEPLAY);
        SINGLE_RARE_CAST_MAP.add(LootTables.HERO_OF_THE_VILLAGE_TOOLSMITH_GIFT_GAMEPLAY);

        SINGLE_COMMON_CAST_MAP.add(LootTables.TRAIL_RUINS_COMMON_ARCHAEOLOGY);
        SINGLE_COMMON_CAST_MAP.add(LootTables.DESERT_WELL_ARCHAEOLOGY);

        VILLAGE_HOUSE_LOOT_TABLES.add(LootTables.VILLAGE_DESERT_HOUSE_CHEST);
        VILLAGE_HOUSE_LOOT_TABLES.add(LootTables.VILLAGE_SAVANNA_HOUSE_CHEST);
        VILLAGE_HOUSE_LOOT_TABLES.add(LootTables.VILLAGE_SNOWY_HOUSE_CHEST);
        VILLAGE_HOUSE_LOOT_TABLES.add(LootTables.VILLAGE_TAIGA_HOUSE_CHEST);
    }

    public static void modifyLootTables() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if (!source.isBuiltin()) {
                return;
            }

            if (key.equals(LootTables.VILLAGE_CARTOGRAPHER_CHEST)) {
                tableBuilder.pool(
                        LootPool.builder()
                                .rolls(UniformLootNumberProvider.create(0, 1))
                                .with(buildTrialChamberMap())
                );
            } else if (VILLAGE_HOUSE_LOOT_TABLES.contains(key)) {
                tableBuilder.pool(
                        LootPool.builder()
                                .rolls(UniformLootNumberProvider.create(0, 1))
                                .with(EmptyEntry.builder().weight(5))
                                .with(buildTrialChamberMap().weight(1))
                );
            } else {
                addCastLootTables(key, tableBuilder);
            }
        });
    }

    private static LeafEntry.Builder<?> buildTrialChamberMap() {
        return ItemEntry.builder(Items.MAP)
                .apply(
                        ExplorationMapLootFunction.builder()
                                .withDestination(StructureTags.ON_TRIAL_CHAMBERS_MAPS)
                                .withDecoration(MapDecorationTypes.TRIAL_CHAMBERS)
                                .withZoom((byte) 2)
                                .withSkipExistingChunks(false)
                )
                .apply(
                        SetNameLootFunction.builder(
                                Text.translatable("filled_map.trial_chambers"), SetNameLootFunction.Target.ITEM_NAME)
                );
    }

    private static void addCastLootTables(RegistryKey<LootTable> key, LootTable.Builder tableBuilder) {
        if (CAST_MAP.containsKey(key)) {
            float castChance = CAST_MAP.get(key);

            tableBuilder.pool(
                    LootPool.builder()
                            .rolls(UniformLootNumberProvider.create(0, 1))
                            .with(ItemEntry.builder(GenesisItems.CLAY_SHIELD_CAST))
                            .with(ItemEntry.builder(GenesisItems.CLAY_ANVIL_CAST))
                            .conditionally(RandomChanceLootCondition.builder(castChance))
            );
        } else if (TOTEM_CAST_MAP.containsKey(key)) {
            float totemCastChance = TOTEM_CAST_MAP.get(key);

            tableBuilder.pool(
                    LootPool.builder()
                            .rolls(ConstantLootNumberProvider.create(1))
                            .with(ItemEntry.builder(GenesisItems.CLAY_TOTEM_CAST))
                            .with(ItemEntry.builder(GenesisItems.CLAY_SHIELD_CAST))
                            .with(ItemEntry.builder(GenesisItems.CLAY_ANVIL_CAST))
                            .conditionally(RandomChanceLootCondition.builder(totemCastChance))
            );
        } else if (SINGLE_RARE_CAST_MAP.contains(key)) {
            tableBuilder.modifyPools(builder -> builder
                    .with(ItemEntry.builder(GenesisItems.CLAY_TOTEM_CAST))
                    .with(ItemEntry.builder(GenesisItems.CLAY_SHIELD_CAST))
                    .with(ItemEntry.builder(GenesisItems.CLAY_ANVIL_CAST)));
        } else if (SINGLE_COMMON_CAST_MAP.contains(key)) {
            tableBuilder.modifyPools(builder -> builder
                    .with(ItemEntry.builder(GenesisItems.CLAY_SWORD_CAST))
                    .with(ItemEntry.builder(GenesisItems.CLAY_SHOVEL_CAST))
                    .with(ItemEntry.builder(GenesisItems.CLAY_PICKAXE_CAST))
                    .with(ItemEntry.builder(GenesisItems.CLAY_AXE_CAST))
                    .with(ItemEntry.builder(GenesisItems.CLAY_HOE_CAST)));
        }
    }
}

