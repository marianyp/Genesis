package dev.mariany.genesis.world.item.trading;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.tag.GenesisTags;
import dev.mariany.genesis.world.level.storage.loot.functions.BiomeMapFunction;
import net.minecraft.advancements.predicates.DataComponentMatchers;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.TradeCost;
import net.minecraft.world.item.trading.VillagerTrade;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
import net.minecraft.world.level.storage.loot.functions.*;

import java.util.List;
import java.util.Optional;

public class GenesisVillagerTrades {
    public static final ResourceKey<VillagerTrade> CARTOGRAPHER_2_EMERALD_AND_COMPASS_PALE_GARDEN = resourceKey(
            "cartographer/2/emerald_and_compass_pale_garden"
    );

    public static final ResourceKey<VillagerTrade> CARTOGRAPHER_3_EMERALD_AND_COMPASS_DEEP_DARK = resourceKey(
            "cartographer/3/emerald_and_compass_deep_dark"
    );

    public static void bootstrap(BootstrapContext<VillagerTrade> context) {
        HolderGetter<Item> itemGetter = context.lookup(Registries.ITEM);

        register(
                context,
                CARTOGRAPHER_2_EMERALD_AND_COMPASS_PALE_GARDEN,
                createBiomeMapTrade(
                        itemGetter,
                        "pale_garden",
                        8,
                        5,
                        GenesisTags.Biomes.ON_PALE_GARDEN_EXPLORER_MAPS
                )
        );

        register(
                context,
                CARTOGRAPHER_3_EMERALD_AND_COMPASS_DEEP_DARK,
                createStructureMapTrade(
                        itemGetter,
                        "deep_dark",
                        13,
                        10,
                        GenesisTags.Structures.ON_DEEP_DARK_EXPLORER_MAPS,
                        MapDecorationTypes.RED_X
                )
        );
    }

    private static ResourceKey<VillagerTrade> resourceKey(final String path) {
        return ResourceKey.create(Registries.VILLAGER_TRADE, Genesis.id(path));
    }

    private static void register(
            BootstrapContext<VillagerTrade> context,
            ResourceKey<VillagerTrade> resourceKey,
            VillagerTrade villagerTrade
    ) {
        context.register(resourceKey, villagerTrade);
    }

    private static VillagerTrade createBiomeMapTrade(
            HolderGetter<Item> itemGetter,
            String translationKey,
            int emeraldCost,
            int xp,
            TagKey<Biome> biomeTagKey
    ) {
        return createMapTrade(
                itemGetter,
                translationKey,
                emeraldCost,
                xp,
                BiomeMapFunction
                        .makeBiomeMap()
                        .setDestination(biomeTagKey)
                        .build()
        );
    }

    private static VillagerTrade createStructureMapTrade(
            HolderGetter<Item> itemGetter,
            String translationKey,
            int emeraldCost,
            int xp,
            TagKey<Structure> structureTagKey,
            Holder<MapDecorationType> mapDecoration
    ) {
        return createMapTrade(
                itemGetter,
                translationKey,
                emeraldCost,
                xp,
                ExplorationMapFunction
                        .makeExplorationMap()
                        .setDestination(structureTagKey)
                        .setMapDecoration(mapDecoration)
                        .setSearchRadius(100)
                        .setSkipKnownStructures(true)
                        .build()
        );
    }

    private static VillagerTrade createMapTrade(
            HolderGetter<Item> items,
            String id,
            int emeraldCost,
            int xp,
            LootItemFunction lootItemConditionalFunction
    ) {
        return new VillagerTrade(
                new TradeCost(Items.EMERALD, emeraldCost),
                Optional.of(new TradeCost(Items.COMPASS, 1)),
                new ItemStackTemplate(Items.MAP),
                12,
                xp,
                0.2F,
                Optional.empty(),
                List.of(
                        lootItemConditionalFunction,
                        SetNameFunction.setName(
                                Component.translatable("filled_map." + Genesis.id(id).toLanguageKey()),
                                SetNameFunction.Target.ITEM_NAME
                        ).build(),
                        FilteredFunction
                                .filtered(
                                        new ItemPredicate.Builder()
                                                .of(items, Items.FILLED_MAP)
                                                .withComponents(
                                                        DataComponentMatchers.Builder
                                                                .components()
                                                                .any(DataComponents.MAP_ID)
                                                                .build()
                                                )
                                                .build()
                                )
                                .onFail(Optional.of(DiscardItem.discardItem().build()))
                                .build()
                )
        );
    }
}
