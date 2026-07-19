package dev.mariany.genesis.datagen;

import dev.mariany.genesis.block.GenesisBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.minecraft.core.HolderLookup;
import java.util.concurrent.CompletableFuture;

public class GenesisBlockLootTableProvider extends FabricBlockLootSubProvider {
    public GenesisBlockLootTableProvider(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        dropSelf(GenesisBlocks.CLAY_KILN);
        add(GenesisBlocks.KILN, this::createNameableBlockEntityTable);

        dropSelf(GenesisBlocks.CLAY_CAULDRON);
        dropSelf(GenesisBlocks.TERRACOTTA_CAULDRON);
        dropSelf(GenesisBlocks.DIRT_TERRACOTTA_CAULDRON);
        dropSelf(GenesisBlocks.GRAVEL_TERRACOTTA_CAULDRON);
        dropSelf(GenesisBlocks.SOUL_SAND_TERRACOTTA_CAULDRON);
        dropSelf(GenesisBlocks.SOUL_SOIL_TERRACOTTA_CAULDRON);

        dropSelf(GenesisBlocks.RAW_COAL_BLOCK);
        dropSelf(GenesisBlocks.RAW_DIAMOND_BLOCK);
        dropSelf(GenesisBlocks.RAW_EMERALD_BLOCK);
        dropSelf(GenesisBlocks.RAW_LAPIS_LAZULI_BLOCK);
        dropSelf(GenesisBlocks.RAW_NETHERITE_BLOCK);
        dropSelf(GenesisBlocks.RAW_REDSTONE_BLOCK);

        dropSelf(GenesisBlocks.ASSEMBLY_TABLE);
    }
}
