package dev.mariany.genesis.datagen;

import dev.mariany.genesis.loot.GenesisLootTables;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableSubProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class GenesisDustingLootTableProvider extends SimpleFabricLootTableSubProvider {
    private final CompletableFuture<HolderLookup.Provider> registryLookupFuture;

    public GenesisDustingLootTableProvider(
            FabricPackOutput dataOutput,
            CompletableFuture<HolderLookup.Provider> registryLookup
    ) {
        super(dataOutput, registryLookup, LootContextParamSets.ARCHAEOLOGY);
        this.registryLookupFuture = registryLookup;
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> lootTableBiConsumer) {
        addDustingDrop(lootTableBiConsumer, GenesisLootTables.DIRT_DUSTING, Items.CLAY_BALL);
        addDustingDrop(lootTableBiConsumer, GenesisLootTables.GRAVEL_DUSTING, Items.FLINT);
    }

    private void addDustingDrop(
            BiConsumer<ResourceKey<LootTable>, LootTable.Builder> lootTableBiConsumer,
            ResourceKey<LootTable> lootTable,
            Item drop
    ) {
        this.registryLookupFuture.thenAccept(registries -> {
            HolderLookup.RegistryLookup<Item> itemRegistry = registries.lookupOrThrow(Registries.ITEM);

            lootTableBiConsumer.accept(
                    lootTable,
                    LootTable.lootTable().withPool(createBrushDependentDropPool(itemRegistry, drop))
            );
        });
    }

    private static LootPool.Builder createBrushDependentDropPool(
            HolderLookup.RegistryLookup<Item> itemRegistry,
            Item drop
    ) {
        return LootPool
                .lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(
                        LootItem
                                .lootTableItem(drop)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
                                .when(
                                        MatchTool.toolMatches(
                                                ItemPredicate.Builder
                                                        .item()
                                                        .of(itemRegistry, ConventionalItemTags.BRUSH_TOOLS)
                                        )
                                )
                                .otherwise(
                                        LootItem
                                                .lootTableItem(drop)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0F, 2F)))
                                )
                );
    }
}
