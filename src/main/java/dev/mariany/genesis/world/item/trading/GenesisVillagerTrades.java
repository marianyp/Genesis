package dev.mariany.genesis.world.item.trading;

import dev.mariany.genesis.Genesis;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.TradeCost;
import net.minecraft.world.item.trading.VillagerTrade;
import net.minecraft.world.item.trading.VillagerTrades;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
import net.minecraft.world.level.storage.loot.functions.DiscardItem;
import net.minecraft.world.level.storage.loot.functions.ExplorationMapFunction;
import net.minecraft.world.level.storage.loot.functions.FilteredFunction;

public final class GenesisVillagerTrades {
    public static final ResourceKey<VillagerTrade> CARTOGRAPHER_3_EMERALD_AND_COMPASS_DEEP_DARK = ResourceKey.create(
            Registries.VILLAGER_TRADE,
            Genesis.id("cartographer/3/emerald_and_compass_deep_dark")
    );

    private GenesisVillagerTrades() {
    }

    public static void bootstrap(BootstrapContext<VillagerTrade> context) {
        HolderGetter<Structure> structures = context.lookup(Registries.STRUCTURE);
        context.register(CARTOGRAPHER_3_EMERALD_AND_COMPASS_DEEP_DARK, createAncientCityMapTrade(structures));
    }

    private static VillagerTrade createAncientCityMapTrade(HolderGetter<Structure> structures) {
        HolderSet.Named<Structure> destination = structures.getOrThrow(StructureTags.ON_ANCIENT_CITY_MAPS);

        ExplorationMapFunction.Builder explorationMapFunctionBuilder = ExplorationMapFunction
                .makeExplorationMap(destination)
                .setMapDecoration(MapDecorationTypes.ANCIENT_CITY)
                .setSkipKnownStructures(true);

        FilteredFunction.Builder filteredFunctionBuilder = FilteredFunction
                .filtered(VillagerTrades.anyValidMap().build())
                .onFail(DiscardItem.discardItem().build());

        return VillagerTrade
                .builder(
                        new TradeCost(Items.EMERALD, 13),
                        new TradeCost(Items.COMPASS, 1),
                        new ItemStackTemplate(Items.BURIED_ANCIENT_CITY_MAP),
                        12,
                        10,
                        0.2F
                )
                .addModifier(explorationMapFunctionBuilder)
                .addModifier(filteredFunctionBuilder)
                .build();
    }
}
