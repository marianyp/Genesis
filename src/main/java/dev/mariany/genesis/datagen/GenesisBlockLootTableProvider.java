package dev.mariany.genesis.datagen;

import dev.mariany.genesis.block.GenesisBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class GenesisBlockLootTableProvider extends FabricBlockLootTableProvider {
    public GenesisBlockLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        addDrop(GenesisBlocks.CLAY_KILN);
        addDrop(GenesisBlocks.KILN, this::nameableContainerDrops);
    }
}
