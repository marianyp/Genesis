package dev.mariany.genesis.datagen;

import dev.mariany.genesis.entity.GenesisEntityTypes;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricEntityLootSubProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SmeltItemFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.concurrent.CompletableFuture;

public class GenesisEntityLootTableGenerator extends FabricEntityLootSubProvider {
    public GenesisEntityLootTableGenerator(
            FabricPackOutput output,
            CompletableFuture<HolderLookup.Provider> registryLookup
    ) {
        super(output, registryLookup);
    }

    @Override
    public void generate() {
        this.add(
                GenesisEntityTypes.BOAR,
                LootTable.lootTable()
                         .withPool(
                                 LootPool.lootPool()
                                         .setRolls(ConstantValue.exactly(1F))
                                         .add(
                                                 LootItem.lootTableItem(Items.LEATHER)
                                                         .apply(
                                                                 SetItemCountFunction.setCount(
                                                                         UniformGenerator.between(1F, 2F)
                                                                 )
                                                         )
                                                         .apply(EnchantedCountIncreaseFunction.lootingMultiplier(
                                                                        this.registries,
                                                                        UniformGenerator.between(0F, 1F)
                                                                )
                                                         )
                                         )
                         )
                         .withPool(
                                 LootPool.lootPool()
                                         .setRolls(ConstantValue.exactly(1.0F))
                                         .add(
                                                 LootItem.lootTableItem(Items.PORKCHOP)
                                                         .apply(SetItemCountFunction.setCount(
                                                                        UniformGenerator.between(0F, 1F)
                                                                )
                                                         )
                                                         .apply(SmeltItemFunction.smelted().when(
                                                                        this.shouldSmeltLoot()
                                                                )
                                                         )
                                                         .apply(EnchantedCountIncreaseFunction.lootingMultiplier(
                                                                        this.registries,
                                                                        UniformGenerator.between(0F, 1F)
                                                                )
                                                         )
                                         )
                         )
        );
    }
}
