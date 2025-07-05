package dev.mariany.genesis;

import dev.mariany.genesis.datagen.GenesisEquipmentAssetProvider;
import dev.mariany.genesis.datagen.GenesisItemTagProvider;
import dev.mariany.genesis.datagen.GenesisModelProvider;
import dev.mariany.genesis.datagen.GenesisRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class GenesisDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(GenesisItemTagProvider::new);
        pack.addProvider(GenesisModelProvider::new);
        pack.addProvider(GenesisRecipeProvider::new);

        FabricDataGenerator.Pack secondaryPack = fabricDataGenerator.createPack();
        secondaryPack.addProvider((FabricDataGenerator.Pack.Factory<GenesisEquipmentAssetProvider>) GenesisEquipmentAssetProvider::new);
    }
}
