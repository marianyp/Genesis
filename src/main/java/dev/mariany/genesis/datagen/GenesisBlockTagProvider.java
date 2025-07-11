package dev.mariany.genesis.datagen;

import dev.mariany.genesis.block.GenesisBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

import java.util.concurrent.CompletableFuture;

public class GenesisBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public GenesisBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        valueLookupBuilder(BlockTags.PICKAXE_MINEABLE).add(
                GenesisBlocks.CLAY_KILN,
                GenesisBlocks.KILN,
                GenesisBlocks.CLAY_CAULDRON,
                GenesisBlocks.TERRACOTTA_CAULDRON,
                GenesisBlocks.DIRT_TERRACOTTA_CAULDRON,
                GenesisBlocks.GRAVEL_TERRACOTTA_CAULDRON
        );
    }
}
