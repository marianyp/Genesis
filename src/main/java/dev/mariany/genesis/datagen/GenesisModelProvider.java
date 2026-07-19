package dev.mariany.genesis.datagen;

import dev.mariany.genesis.block.GenesisBlocks;
import dev.mariany.genesis.item.GenesisItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.*;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.properties.numeric.UseDuration;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public class GenesisModelProvider extends FabricModelProvider {
    public GenesisModelProvider(FabricPackOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        blockStateModelGenerator.createTrivialCube(GenesisBlocks.RAW_COAL_BLOCK);
        blockStateModelGenerator.createTrivialCube(GenesisBlocks.RAW_DIAMOND_BLOCK);
        blockStateModelGenerator.createTrivialCube(GenesisBlocks.RAW_EMERALD_BLOCK);
        blockStateModelGenerator.createTrivialCube(GenesisBlocks.RAW_LAPIS_LAZULI_BLOCK);
        blockStateModelGenerator.createTrivialCube(GenesisBlocks.RAW_NETHERITE_BLOCK);
        blockStateModelGenerator.createTrivialCube(GenesisBlocks.RAW_REDSTONE_BLOCK);

        blockStateModelGenerator.createCraftingTableLike(
                GenesisBlocks.ASSEMBLY_TABLE,
                Blocks.OAK_PLANKS,
                TextureMapping::craftingTable
        );
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
        itemModelGenerator.generateFlatItem(
                GenesisItems.ENCHANTED_HONEY_BOTTLE,
                Items.HONEY_BOTTLE,
                ModelTemplates.FLAT_ITEM
        );

        itemModelGenerator.generateFlatItem(GenesisItems.BOAR_SPAWN_EGG, ModelTemplates.FLAT_ITEM);

        this.registerRawOres(itemModelGenerator);
        this.registerCasts(itemModelGenerator);
        this.registerFlints(itemModelGenerator, GenesisItems.FLINTS);
    }

    private void registerRawOres(ItemModelGenerators itemModelGenerator) {
        itemModelGenerator.generateFlatItem(GenesisItems.RAW_COAL, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(GenesisItems.RAW_DIAMOND, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(GenesisItems.RAW_REDSTONE, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(GenesisItems.RAW_EMERALD, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(GenesisItems.RAW_LAPIS_LAZULI, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(GenesisItems.RAW_NETHERITE, ModelTemplates.FLAT_ITEM);
    }

    private void registerCasts(ItemModelGenerators itemModelGenerator) {
        itemModelGenerator.generateFlatItem(GenesisItems.BLANK_CLAY_CAST, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(GenesisItems.CLAY_SWORD_CAST, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(GenesisItems.CLAY_SHOVEL_CAST, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(GenesisItems.CLAY_PICKAXE_CAST, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(GenesisItems.CLAY_AXE_CAST, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(GenesisItems.CLAY_HOE_CAST, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(GenesisItems.CLAY_SPEAR_CAST, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(GenesisItems.CLAY_SHIELD_CAST, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(GenesisItems.CLAY_ANVIL_CAST, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(GenesisItems.CLAY_TOTEM_CAST, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(GenesisItems.SWORD_CAST, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(GenesisItems.SHOVEL_CAST, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(GenesisItems.PICKAXE_CAST, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(GenesisItems.AXE_CAST, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(GenesisItems.HOE_CAST, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(GenesisItems.SPEAR_CAST, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(GenesisItems.SHIELD_CAST, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(GenesisItems.ANVIL_CAST, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(GenesisItems.TOTEM_CAST, ModelTemplates.FLAT_ITEM);
    }

    private void registerFlints(ItemModelGenerators itemModelGenerator, Item item) {
        ItemModel.Unbaked idle = ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(item));
        ItemModel.Unbaked apart = ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(item, "_striking_0"));
        ItemModel.Unbaked sparked = ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(item, "_striking_1"));
        ItemModel.Unbaked touching = ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(item, "_striking_2"));

        itemModelGenerator.itemModelOutput
                .accept(
                        item,
                        ItemModelUtils.conditional(
                                ItemModelUtils.isUsingItem(),
                                ItemModelUtils.rangeSelect(
                                        new UseDuration(false), 0.1F, apart, ItemModelUtils.override(sparked, 0.4F), ItemModelUtils.override(touching, 0.9F)
                                ),
                                idle
                        )
                );
    }
}
