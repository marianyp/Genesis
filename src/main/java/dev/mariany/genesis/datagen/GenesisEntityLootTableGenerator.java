package dev.mariany.genesis.datagen;

import dev.mariany.genesis.entity.GenesisEntities;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricEntityLootTableProvider;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.EnchantedCountIncreaseLootFunction;
import net.minecraft.loot.function.FurnaceSmeltLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.RegistryWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class GenesisEntityLootTableGenerator extends FabricEntityLootTableProvider {
    public GenesisEntityLootTableGenerator(
            FabricDataOutput output,
            @NotNull CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup
    ) {
        super(output, registryLookup);
    }

    @Override
    public void generate() {
        this.register(
                GenesisEntities.BOAR,
                LootTable.builder()
                        .pool(
                                LootPool.builder()
                                        .rolls(ConstantLootNumberProvider.create(1F))
                                        .with(
                                                ItemEntry.builder(Items.LEATHER)
                                                        .apply(
                                                                SetCountLootFunction.builder(
                                                                        UniformLootNumberProvider.create(1F, 2F)
                                                                )
                                                        )
                                                        .apply(EnchantedCountIncreaseLootFunction.builder(
                                                                        this.registries,
                                                                        UniformLootNumberProvider.create(0F, 1F)
                                                                )
                                                        )
                                        )
                        )
                        .pool(
                                LootPool.builder()
                                        .rolls(ConstantLootNumberProvider.create(1.0F))
                                        .with(
                                                ItemEntry.builder(Items.PORKCHOP)
                                                        .apply(SetCountLootFunction.builder(
                                                                        UniformLootNumberProvider.create(0F, 1F)
                                                                )
                                                        )
                                                        .apply(FurnaceSmeltLootFunction.builder().conditionally(
                                                                        this.createSmeltLootCondition()
                                                                )
                                                        )
                                                        .apply(EnchantedCountIncreaseLootFunction.builder(
                                                                        this.registries,
                                                                        UniformLootNumberProvider.create(0F, 1F)
                                                                )
                                                        )
                                        )
                        )
        );
    }
}
