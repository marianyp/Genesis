package dev.mariany.genesis.datagen;

import dev.mariany.genesis.item.GenesisItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class GenesisBlockLootTableOverrideProvider extends FabricBlockLootTableProvider {
    public GenesisBlockLootTableOverrideProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        addOreDrops(GenesisItems.RAW_COAL, Blocks.COAL_ORE, Blocks.DEEPSLATE_COAL_ORE);
        addOreDrops(GenesisItems.RAW_DIAMOND, Blocks.DIAMOND_ORE, Blocks.DEEPSLATE_DIAMOND_ORE);
        addOreDrops(GenesisItems.RAW_EMERALD, Blocks.EMERALD_ORE, Blocks.DEEPSLATE_EMERALD_ORE);
        addOreDrops(GenesisItems.RAW_LAPIS_LAZULI, Blocks.LAPIS_ORE, Blocks.DEEPSLATE_LAPIS_ORE);
        addOreDrops(GenesisItems.RAW_REDSTONE, Blocks.REDSTONE_ORE, Blocks.DEEPSLATE_REDSTONE_ORE);

        addDrop(Blocks.ANCIENT_DEBRIS, block -> this.oreDrops(block, GenesisItems.RAW_NETHERITE));
    }

    private void addOreDrops(Item item, Block defaultOre, Block deepslateOre) {
        addDrop(defaultOre, block -> this.oreDrops(block, item));
        addDrop(deepslateOre, block -> this.oreDrops(block, item));
    }

    @Override
    public String getName() {
        return "Genesis Block Loot Table Overrides";
    }
}
