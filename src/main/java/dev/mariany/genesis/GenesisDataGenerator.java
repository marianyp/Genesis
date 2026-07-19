package dev.mariany.genesis;

import dev.mariany.genesis.datagen.*;
import dev.mariany.genesis.world.item.trading.GenesisVillagerTrades;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;

public class GenesisDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(GenesisAdvancementsOverrideProvider::new);
        pack.addProvider(GenesisAgeProvider::new);
        pack.addProvider(GenesisBiomeTagProvider::new);
        pack.addProvider(GenesisBlockLootTableOverrideProvider::new);
        pack.addProvider(GenesisBlockLootTableProvider::new);
        pack.addProvider(GenesisBlockTagProvider::new);
        pack.addProvider(GenesisDustingLootTableProvider::new);
        pack.addProvider(GenesisEntityLootTableGenerator::new);
        pack.addProvider(GenesisEntityTypeTagProvider::new);
        pack.addProvider(GenesisInstructionProvider::new);
        pack.addProvider(GenesisItemTagProvider::new);
        pack.addProvider(GenesisModelProvider::new);
        pack.addProvider(GenesisRecipeProvider::new);
        pack.addProvider(GenesisStructureTagProvider::new);
        pack.addProvider(GenesisVillagerTradesProvider::new);
        pack.addProvider(GenesisVillagerTradesTagsProvider::new);
    }

    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        registryBuilder.add(Registries.VILLAGER_TRADE, GenesisVillagerTrades::bootstrap);
    }
}
