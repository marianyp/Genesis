package dev.mariany.genesis.loot;

import dev.mariany.genesis.item.GenesisItems;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.RegistryKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LootTableModifiers {
    private static final Map<RegistryKey<LootTable>, Float> CAST_MAP = new HashMap<>();
    private static final Map<RegistryKey<LootTable>, Float> TOTEM_CAST_MAP = new HashMap<>();
    private static final List<RegistryKey<LootTable>> SINGLE_RARE_CAST_MAP = new ArrayList<>();
    private static final List<RegistryKey<LootTable>> SINGLE_COMMON_CAST_MAP = new ArrayList<>();

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
    }

    public static void modifyLootTables() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if (!source.isBuiltin()) {
                return;
            }

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
        });
    }
}

