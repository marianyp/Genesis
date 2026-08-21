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
import net.minecraft.world.level.storage.loot.entries.EntryGroup;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class GenesisSiftingLootTableProvider extends SimpleFabricLootTableSubProvider {
    private final CompletableFuture<HolderLookup.Provider> registryLookupFuture;

    public GenesisSiftingLootTableProvider(
            FabricPackOutput dataOutput,
            CompletableFuture<HolderLookup.Provider> registryLookup
    ) {
        super(dataOutput, registryLookup, LootContextParamSets.ARCHAEOLOGY);
        this.registryLookupFuture = registryLookup;
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> lootTableBiConsumer) {
        this.registerTable(lootTableBiConsumer, GenesisLootTables.SIFTING_DIRT, Items.CLAY_BALL);
        this.registerTable(lootTableBiConsumer, GenesisLootTables.SIFTING_GRAVEL, Items.FLINT);
        this.registerSoulTable(lootTableBiConsumer, GenesisLootTables.SIFTING_SOUL);
    }

    private void registerTable(
            BiConsumer<ResourceKey<LootTable>, LootTable.Builder> lootTableBiConsumer,
            ResourceKey<LootTable> lootTable,
            Item drop
    ) {
        this.registerTable(
                lootTableBiConsumer,
                lootTable,
                itemRegistryLookup -> buildBrushPool(itemRegistryLookup, drop)
        );
    }

    private static LootPool.Builder buildBrushPool(
            HolderLookup.RegistryLookup<Item> itemRegistry,
            Item drop
    ) {
        return LootPool
                .lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(
                        LootItem.lootTableItem(drop)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
                                .when(
                                        MatchTool.toolMatches(
                                                ItemPredicate.Builder
                                                        .item()
                                                        .of(itemRegistry, ConventionalItemTags.BRUSH_TOOLS)
                                        )
                                )
                                .otherwise(
                                        LootItem.lootTableItem(drop)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 2)))
                                )
                );
    }

    private void registerSoulTable(
            BiConsumer<ResourceKey<LootTable>, LootTable.Builder> lootTableBiConsumer,
            ResourceKey<LootTable> lootTable
    ) {
        this.registerTable(lootTableBiConsumer, lootTable, GenesisSiftingLootTableProvider::buildSoulPool);
    }

    private static LootPool.Builder buildSoulPool(HolderLookup.RegistryLookup<Item> itemRegistryLookup) {
        return LootPool.lootPool()
                       .setRolls(ConstantValue.exactly(1))
                       .add(
                               new EntryGroup.Builder(
                                       LootItem.lootTableItem(Items.BONE)
                                               .setWeight(50)
                                               .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 1))),
                                       LootItem.lootTableItem(Items.GOLD_NUGGET).setWeight(40),
                                       LootItem.lootTableItem(Items.SKELETON_SKULL).setWeight(6),
                                       LootItem.lootTableItem(Items.WITHER_SKELETON_SKULL).setWeight(5)
                               ).when(
                                       MatchTool.toolMatches(
                                               ItemPredicate.Builder
                                                       .item()
                                                       .of(itemRegistryLookup, ConventionalItemTags.BRUSH_TOOLS)
                                       )
                               )
                       )
                       .add(
                               new EntryGroup.Builder(
                                       LootItem.lootTableItem(Items.BONE)
                                               .setWeight(80)
                                               .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 1))),
                                       LootItem.lootTableItem(Items.GOLD_NUGGET).setWeight(7),
                                       LootItem.lootTableItem(Items.SKELETON_SKULL).setWeight(2),
                                       LootItem.lootTableItem(Items.WITHER_SKELETON_SKULL).setWeight(1)
                               )
                       );
    }

    private void registerTable(
            BiConsumer<ResourceKey<LootTable>, LootTable.Builder> lootTableBiConsumer,
            ResourceKey<LootTable> lootTable,
            LootPoolFactory lootPoolFactory
    ) {
        this.registryLookupFuture.thenAccept(registries -> {
            HolderLookup.RegistryLookup<Item> itemRegistry = registries.lookupOrThrow(Registries.ITEM);

            lootTableBiConsumer.accept(
                    lootTable,
                    LootTable.lootTable().withPool(lootPoolFactory.apply(itemRegistry))
            );
        });
    }

    @FunctionalInterface
    interface LootPoolFactory {
        LootPool.Builder apply(HolderLookup.RegistryLookup<Item> itemRegistryLookup);
    }
}
