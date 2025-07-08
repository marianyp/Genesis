package dev.mariany.genesis.datagen;

import dev.mariany.genesis.loot.GenesisLootTables;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class GenesisArchaeologyLootTableProvider extends SimpleFabricLootTableProvider {
    private final CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture;

    public GenesisArchaeologyLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup, LootContextTypes.ARCHAEOLOGY);
        this.registryLookupFuture = registryLookup;
    }

    @Override
    public void accept(BiConsumer<RegistryKey<LootTable>, LootTable.Builder> lootTableBiConsumer) {
        addSiftingDrop(lootTableBiConsumer, GenesisLootTables.CLAY_SIFTING, Items.CLAY_BALL);
        addSiftingDrop(lootTableBiConsumer, GenesisLootTables.FLINT_SIFTING, Items.FLINT);
    }

    private void addSiftingDrop(BiConsumer<RegistryKey<LootTable>, LootTable.Builder> lootTableBiConsumer, RegistryKey<LootTable> lootTable, Item drop) {
        this.registryLookupFuture.thenAccept(registries -> {
            RegistryWrapper.Impl<Item> itemRegistry = registries.getOrThrow(RegistryKeys.ITEM);

            lootTableBiConsumer.accept(lootTable, LootTable.builder()
                    .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1F))
                            .with(ItemEntry.builder(drop)
                                    .conditionally(
                                            MatchToolLootCondition.builder(
                                                    ItemPredicate.Builder.create().tag(itemRegistry, ConventionalItemTags.BRUSH_TOOLS)
                                            )
                                    )
                                    .alternatively(
                                            ItemEntry.builder(drop).apply(
                                                    SetCountLootFunction.builder(UniformLootNumberProvider.create(0F, 1F))
                                            )
                                    )
                            )
                    )
            );
        });
    }
}
