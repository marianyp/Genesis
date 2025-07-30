package dev.mariany.genesis;

import dev.mariany.genesis.datagen.*;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class GenesisDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(GenesisItemTagProvider::new);
        pack.addProvider(GenesisBlockTagProvider::new);
        pack.addProvider(GenesisModelProvider::new);
        pack.addProvider(GenesisRecipeProvider::new);
        pack.addProvider(GenesisRecipeOverrideProvider::new);
        pack.addProvider(GenesisBlockLootTableProvider::new);
        pack.addProvider(GenesisBlockLootTableOverrideProvider::new);
        pack.addProvider(GenesisEntityLootTableGenerator::new);
        pack.addProvider(GenesisAdvancementsOverrideProvider::new);
        pack.addProvider(GenesisDustingLootTableProvider::new);
        pack.addProvider(GenesisAgesProvider::new);
        pack.addProvider(GenesisInstructionsProvider::new);
        pack.addProvider(GenesisStructureTagProvider::new);
        pack.addProvider(GenesisBiomeTagProvider::new);

        FabricDataGenerator.Pack secondaryPack = fabricDataGenerator.createPack();
        secondaryPack.addProvider((FabricDataGenerator.Pack.Factory<GenesisEquipmentAssetProvider>) GenesisEquipmentAssetProvider::new);
    }
}
