package dev.mariany.genesis.datagen;

import dev.mariany.genesis.item.GenesisItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import java.util.concurrent.CompletableFuture;

public class GenesisBlockLootTableOverrideProvider extends FabricBlockLootSubProvider {
    public GenesisBlockLootTableOverrideProvider(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        addOreDrops(GenesisItems.RAW_COAL, Blocks.COAL_ORE, Blocks.DEEPSLATE_COAL_ORE);
        addOreDrops(GenesisItems.RAW_DIAMOND, Blocks.DIAMOND_ORE, Blocks.DEEPSLATE_DIAMOND_ORE);
        addOreDrops(GenesisItems.RAW_EMERALD, Blocks.EMERALD_ORE, Blocks.DEEPSLATE_EMERALD_ORE);

        add(Blocks.LAPIS_ORE, this::createLapisOreDrops);
        add(Blocks.DEEPSLATE_LAPIS_ORE, this::createLapisOreDrops);

        add(Blocks.REDSTONE_ORE, this::createRedstoneOreDrops);
        add(Blocks.DEEPSLATE_REDSTONE_ORE, this::createRedstoneOreDrops);

        add(Blocks.ANCIENT_DEBRIS, block -> this.createOreDrop(block, GenesisItems.RAW_NETHERITE));
    }

    private void addOreDrops(Item item, Block defaultOre, Block deepslateOre) {
        add(defaultOre, block -> this.createOreDrop(block, item));
        add(deepslateOre, block -> this.createOreDrop(block, item));
    }

    public LootTable.Builder createLapisOreDrops(Block drop) {
        HolderLookup.RegistryLookup<Enchantment> enchantmentRegistry = this.registries.lookupOrThrow(Registries.ENCHANTMENT);

        return this.createSilkTouchDispatchTable(
                drop,
                this.applyExplosionDecay(
                        drop,
                        LootItem.lootTableItem(GenesisItems.RAW_LAPIS_LAZULI)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(4F, 9F)))
                                .apply(
                                        ApplyBonusCount.addOreBonusCount(
                                                enchantmentRegistry.getOrThrow(Enchantments.FORTUNE)
                                        )
                                )
                )
        );
    }

    public LootTable.Builder createRedstoneOreDrops(Block drop) {
        HolderLookup.RegistryLookup<Enchantment> enchantmentRegistry = this.registries.lookupOrThrow(Registries.ENCHANTMENT);

        return this.createSilkTouchDispatchTable(
                drop,
                this.applyExplosionDecay(
                        drop,
                        LootItem.lootTableItem(GenesisItems.RAW_REDSTONE)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(4F, 5F)))
                                .apply(
                                        ApplyBonusCount.addUniformBonusCount(
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
