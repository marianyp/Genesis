package dev.mariany.genesis.datagen;

import dev.mariany.genesis.item.GenesisItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.RegistryKeys;
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

        addDrop(Blocks.LAPIS_ORE, this::lapisOreDrops);
        addDrop(Blocks.DEEPSLATE_LAPIS_ORE, this::lapisOreDrops);

        addDrop(Blocks.REDSTONE_ORE, this::redstoneOreDrops);
        addDrop(Blocks.DEEPSLATE_REDSTONE_ORE, this::redstoneOreDrops);

        addDrop(Blocks.ANCIENT_DEBRIS, block -> this.oreDrops(block, GenesisItems.RAW_NETHERITE));
    }

    private void addOreDrops(Item item, Block defaultOre, Block deepslateOre) {
        addDrop(defaultOre, block -> this.oreDrops(block, item));
        addDrop(deepslateOre, block -> this.oreDrops(block, item));
    }

    public LootTable.Builder lapisOreDrops(Block drop) {
        RegistryWrapper.Impl<Enchantment> enchantmentRegistry = this.registries.getOrThrow(RegistryKeys.ENCHANTMENT);

        return this.dropsWithSilkTouch(
                drop,
                this.applyExplosionDecay(
                        drop,
                        ItemEntry.builder(GenesisItems.RAW_LAPIS_LAZULI)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(4F, 9F)))
                                .apply(
                                        ApplyBonusLootFunction.oreDrops(
                                                enchantmentRegistry.getOrThrow(Enchantments.FORTUNE)
                                        )
                                )
                )
        );
    }

    public LootTable.Builder redstoneOreDrops(Block drop) {
        RegistryWrapper.Impl<Enchantment> enchantmentRegistry = this.registries.getOrThrow(RegistryKeys.ENCHANTMENT);

        return this.dropsWithSilkTouch(
                drop,
                this.applyExplosionDecay(
                        drop,
                        ItemEntry.builder(GenesisItems.RAW_REDSTONE)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(4F, 5F)))
                                .apply(
                                        ApplyBonusLootFunction.uniformBonusCount(
                                                enchantmentRegistry.getOrThrow(Enchantments.FORTUNE)
                                        )
                                )
                )
        );
    }

    @Override
    public String getName() {
        return "Genesis Block Loot Table Overrides";
    }
}
