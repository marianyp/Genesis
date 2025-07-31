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

        addDrop(GenesisBlocks.CLAY_CAULDRON);
        addDrop(GenesisBlocks.TERRACOTTA_CAULDRON);
        addDrop(GenesisBlocks.DIRT_TERRACOTTA_CAULDRON);
        addDrop(GenesisBlocks.GRAVEL_TERRACOTTA_CAULDRON);
        addDrop(GenesisBlocks.SOUL_SAND_TERRACOTTA_CAULDRON);
        addDrop(GenesisBlocks.SOUL_SOIL_TERRACOTTA_CAULDRON);

        addDrop(GenesisBlocks.RAW_COAL_BLOCK);
        addDrop(GenesisBlocks.RAW_DIAMOND_BLOCK);
        addDrop(GenesisBlocks.RAW_EMERALD_BLOCK);
        addDrop(GenesisBlocks.RAW_LAPIS_LAZULI_BLOCK);
        addDrop(GenesisBlocks.RAW_NETHERITE_BLOCK);
        addDrop(GenesisBlocks.RAW_REDSTONE_BLOCK);

        addDrop(GenesisBlocks.ASSEMBLY_TABLE);
    }
}
