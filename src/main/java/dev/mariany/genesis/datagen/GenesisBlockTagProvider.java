package dev.mariany.genesis.datagen;

import dev.mariany.genesis.block.GenesisBlocks;
import dev.mariany.genesis.tag.GenesisTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;

public class GenesisBlockTagProvider extends FabricTagsProvider.BlockTagsProvider {
    public GenesisBlockTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        tag(GenesisTags.Blocks.BOAR_SPAWNABLE_ON)
                .addOptionalTag(BlockTags.ANIMALS_SPAWNABLE_ON)
                .addOptionalTag(BlockTags.SAND)
                .addOptionalTag(BlockTags.SNOW)
                .addOptionalTag(BlockTags.BADLANDS_TERRACOTTA)
                .add(key(Blocks.DIRT))
                .add(key(Blocks.GRASS_BLOCK))
                .add(key(Blocks.PODZOL))
                .add(key(Blocks.COARSE_DIRT))
                .add(key(Blocks.ROOTED_DIRT))
                .add(key(Blocks.MOSS_BLOCK))
                .add(key(Blocks.PALE_MOSS_BLOCK))
                .add(key(Blocks.MUD))
                .add(key(Blocks.MUDDY_MANGROVE_ROOTS));

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(key(GenesisBlocks.CLAY_KILN))
                .add(key(GenesisBlocks.KILN))
                .add(key(GenesisBlocks.CLAY_CAULDRON))
                .add(key(GenesisBlocks.TERRACOTTA_CAULDRON))
                .add(key(GenesisBlocks.DIRT_TERRACOTTA_CAULDRON))
                .add(key(GenesisBlocks.GRAVEL_TERRACOTTA_CAULDRON))
                .add(key(GenesisBlocks.RAW_COAL_BLOCK))
                .add(key(GenesisBlocks.RAW_DIAMOND_BLOCK))
                .add(key(GenesisBlocks.RAW_EMERALD_BLOCK))
                .add(key(GenesisBlocks.RAW_LAPIS_LAZULI_BLOCK))
                .add(key(GenesisBlocks.RAW_NETHERITE_BLOCK))
                .add(key(GenesisBlocks.RAW_REDSTONE_BLOCK));

        tag(BlockTags.MINEABLE_WITH_AXE).add(key(GenesisBlocks.ASSEMBLY_TABLE));

        supportExternalMod("visualworkbench:unaltered_workbenches", GenesisBlocks.ASSEMBLY_TABLE);
    }

    private void supportExternalMod(String tag, Block block) {
        getOrCreateRawBuilder(TagKey.create(Registries.BLOCK, Identifier.parse(tag)))
                .addElement(key(block).identifier());
    }

    private static ResourceKey<Block> key(Block block) {
        return block.builtInRegistryHolder().key();
    }
}
