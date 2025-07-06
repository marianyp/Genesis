package dev.mariany.genesis.datagen;

import dev.mariany.genesis.item.GenesisItems;
import dev.mariany.genesis.item.equipment.GenesisEquipmentAssets;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.*;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.property.numeric.UseDurationProperty;
import net.minecraft.item.Item;

public class GenesisModelProvider extends FabricModelProvider {
    public GenesisModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(GenesisItems.COPPER_NUGGET, Models.GENERATED);

        this.registerCopperTools(itemModelGenerator);
        this.registerCopperArmor(itemModelGenerator);
        this.registerRawOres(itemModelGenerator);
        this.registerCasts(itemModelGenerator);
        this.registerFlints(itemModelGenerator, GenesisItems.FLINTS);
    }

    private void registerCopperTools(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(GenesisItems.COPPER_SWORD, Models.HANDHELD);
        itemModelGenerator.register(GenesisItems.COPPER_SHOVEL, Models.HANDHELD);
        itemModelGenerator.register(GenesisItems.COPPER_PICKAXE, Models.HANDHELD);
        itemModelGenerator.register(GenesisItems.COPPER_AXE, Models.HANDHELD);
        itemModelGenerator.register(GenesisItems.COPPER_HOE, Models.HANDHELD);
    }

    private void registerCopperArmor(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.registerArmor(GenesisItems.COPPER_HELMET, GenesisEquipmentAssets.COPPER, ItemModelGenerator.HELMET_TRIM_ID_PREFIX, false);
        itemModelGenerator.registerArmor(GenesisItems.COPPER_CHESTPLATE, GenesisEquipmentAssets.COPPER, ItemModelGenerator.CHESTPLATE_TRIM_ID_PREFIX, false);
        itemModelGenerator.registerArmor(GenesisItems.COPPER_LEGGINGS, GenesisEquipmentAssets.COPPER, ItemModelGenerator.LEGGINGS_TRIM_ID_PREFIX, false);
        itemModelGenerator.registerArmor(GenesisItems.COPPER_BOOTS, GenesisEquipmentAssets.COPPER, ItemModelGenerator.BOOTS_TRIM_ID_PREFIX, false);
    }

    private void registerRawOres(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(GenesisItems.RAW_COAL, Models.GENERATED);
        itemModelGenerator.register(GenesisItems.RAW_DIAMOND, Models.GENERATED);
        itemModelGenerator.register(GenesisItems.RAW_REDSTONE, Models.GENERATED);
        itemModelGenerator.register(GenesisItems.RAW_EMERALD, Models.GENERATED);
        itemModelGenerator.register(GenesisItems.RAW_LAPIS_LAZULI, Models.GENERATED);
        itemModelGenerator.register(GenesisItems.RAW_NETHERITE, Models.GENERATED);
    }

    private void registerCasts(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(GenesisItems.BLANK_CLAY_CAST, Models.GENERATED);
        itemModelGenerator.register(GenesisItems.SWORD_CLAY_CAST, Models.GENERATED);
        itemModelGenerator.register(GenesisItems.SHOVEL_CLAY_CAST, Models.GENERATED);
        itemModelGenerator.register(GenesisItems.PICKAXE_CLAY_CAST, Models.GENERATED);
        itemModelGenerator.register(GenesisItems.AXE_CLAY_CAST, Models.GENERATED);
        itemModelGenerator.register(GenesisItems.HOE_CLAY_CAST, Models.GENERATED);
        itemModelGenerator.register(GenesisItems.SHIELD_CLAY_CAST, Models.GENERATED);
        itemModelGenerator.register(GenesisItems.ANVIL_CLAY_CAST, Models.GENERATED);
        itemModelGenerator.register(GenesisItems.TOTEM_CLAY_CAST, Models.GENERATED);
        itemModelGenerator.register(GenesisItems.SWORD_CAST, Models.GENERATED);
        itemModelGenerator.register(GenesisItems.SHOVEL_CAST, Models.GENERATED);
        itemModelGenerator.register(GenesisItems.PICKAXE_CAST, Models.GENERATED);
        itemModelGenerator.register(GenesisItems.AXE_CAST, Models.GENERATED);
        itemModelGenerator.register(GenesisItems.HOE_CAST, Models.GENERATED);
        itemModelGenerator.register(GenesisItems.SHIELD_CAST, Models.GENERATED);
        itemModelGenerator.register(GenesisItems.ANVIL_CAST, Models.GENERATED);
        itemModelGenerator.register(GenesisItems.TOTEM_CAST, Models.GENERATED);
    }

    private void registerFlints(ItemModelGenerator itemModelGenerator, Item item) {
        ItemModel.Unbaked idle = ItemModels.basic(ModelIds.getItemModelId(item));
        ItemModel.Unbaked apart = ItemModels.basic(ModelIds.getItemSubModelId(item, "_striking_0"));
        ItemModel.Unbaked sparked = ItemModels.basic(ModelIds.getItemSubModelId(item, "_striking_1"));
        ItemModel.Unbaked touching = ItemModels.basic(ModelIds.getItemSubModelId(item, "_striking_2"));

        itemModelGenerator.output
                .accept(
                        item,
                        ItemModels.condition(
                                ItemModels.usingItemProperty(),
                                ItemModels.rangeDispatch(
                                        new UseDurationProperty(false), 0.1F, apart, ItemModels.rangeDispatchEntry(sparked, 0.4F), ItemModels.rangeDispatchEntry(touching, 0.9F)
                                ),
                                idle
                        )
                );
    }
}
