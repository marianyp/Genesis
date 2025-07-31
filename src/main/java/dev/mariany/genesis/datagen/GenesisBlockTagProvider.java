package dev.mariany.genesis.datagen;

import dev.mariany.genesis.block.GenesisBlocks;
import dev.mariany.genesis.tag.GenesisTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

import java.util.concurrent.CompletableFuture;

public class GenesisBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public GenesisBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        valueLookupBuilder(GenesisTags.Blocks.BOAR_SPAWNABLE_ON)
                .addOptionalTag(BlockTags.ANIMALS_SPAWNABLE_ON)
                .addOptionalTag(BlockTags.SAND)
                .addOptionalTag(BlockTags.SNOW)
                .addOptionalTag(BlockTags.BADLANDS_TERRACOTTA)
                .add(
                        Blocks.DIRT,
                        Blocks.GRASS_BLOCK,
                        Blocks.PODZOL,
                        Blocks.COARSE_DIRT,
                        Blocks.ROOTED_DIRT,
                        Blocks.MOSS_BLOCK,
                        Blocks.PALE_MOSS_BLOCK,
                        Blocks.MUD,
                        Blocks.MUDDY_MANGROVE_ROOTS
                );

        valueLookupBuilder(BlockTags.PICKAXE_MINEABLE).add(
                GenesisBlocks.CLAY_KILN,
                GenesisBlocks.KILN,
                GenesisBlocks.CLAY_CAULDRON,
                GenesisBlocks.TERRACOTTA_CAULDRON,
                GenesisBlocks.DIRT_TERRACOTTA_CAULDRON,
                GenesisBlocks.GRAVEL_TERRACOTTA_CAULDRON,
                GenesisBlocks.RAW_COAL_BLOCK,
                GenesisBlocks.RAW_DIAMOND_BLOCK,
                GenesisBlocks.RAW_EMERALD_BLOCK,
                GenesisBlocks.RAW_LAPIS_LAZULI_BLOCK,
                GenesisBlocks.RAW_NETHERITE_BLOCK,
                GenesisBlocks.RAW_REDSTONE_BLOCK
        );

        valueLookupBuilder(BlockTags.AXE_MINEABLE).add(GenesisBlocks.ASSEMBLY_TABLE);
    }
}
