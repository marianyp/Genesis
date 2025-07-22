package dev.mariany.genesis.datagen;

import dev.mariany.genesis.item.GenesisItems;
import dev.mariany.genesis.tag.GenesisTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;

import java.util.concurrent.CompletableFuture;

public class GenesisItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public GenesisItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        valueLookupBuilder(ItemTags.SWORDS).add(GenesisItems.COPPER_SWORD);
        valueLookupBuilder(ItemTags.SHOVELS).add(GenesisItems.COPPER_SHOVEL);
        valueLookupBuilder(ItemTags.PICKAXES).add(GenesisItems.COPPER_PICKAXE);
        valueLookupBuilder(ItemTags.AXES).add(GenesisItems.COPPER_AXE);
        valueLookupBuilder(ItemTags.HOES).add(GenesisItems.COPPER_HOE);

        valueLookupBuilder(ItemTags.HEAD_ARMOR).add(GenesisItems.COPPER_HELMET);
        valueLookupBuilder(ItemTags.CHEST_ARMOR).add(GenesisItems.COPPER_CHESTPLATE);
        valueLookupBuilder(ItemTags.LEG_ARMOR).add(GenesisItems.COPPER_LEGGINGS);
        valueLookupBuilder(ItemTags.FOOT_ARMOR).add(GenesisItems.COPPER_BOOTS);

        valueLookupBuilder(GenesisTags.Items.COPPER_TOOL_MATERIALS).add(Items.COPPER_INGOT);
        valueLookupBuilder(GenesisTags.Items.REPAIRS_COPPER_ARMOR).add(Items.COPPER_INGOT);

        valueLookupBuilder(GenesisTags.Items.LEATHER_ARMOR).add(
                Items.LEATHER_HELMET,
                Items.LEATHER_CHESTPLATE,
                Items.LEATHER_LEGGINGS,
                Items.LEATHER_BOOTS
        );

        valueLookupBuilder(GenesisTags.Items.COPPER_ARMOR).add(
                GenesisItems.COPPER_HELMET,
                GenesisItems.COPPER_CHESTPLATE,
                GenesisItems.COPPER_LEGGINGS,
                GenesisItems.COPPER_BOOTS
        );

        valueLookupBuilder(GenesisTags.Items.GOLDEN_ARMOR).add(
                Items.GOLDEN_HELMET,
                Items.GOLDEN_CHESTPLATE,
                Items.GOLDEN_LEGGINGS,
                Items.GOLDEN_BOOTS
        );

        valueLookupBuilder(GenesisTags.Items.IRON_ARMOR).add(
                Items.IRON_HELMET,
                Items.IRON_CHESTPLATE,
                Items.IRON_LEGGINGS,
                Items.IRON_BOOTS
        );

        valueLookupBuilder(GenesisTags.Items.DIAMOND_ARMOR).add(
                Items.DIAMOND_HELMET,
                Items.DIAMOND_CHESTPLATE,
                Items.DIAMOND_LEGGINGS,
                Items.DIAMOND_BOOTS
        );

        valueLookupBuilder(GenesisTags.Items.WOODEN_TOOLS).add(
                Items.WOODEN_SWORD,
                Items.WOODEN_SHOVEL,
                Items.WOODEN_PICKAXE,
                Items.WOODEN_AXE,
                Items.WOODEN_HOE
        );

        valueLookupBuilder(GenesisTags.Items.STONE_TOOLS).add(
                Items.STONE_SWORD,
                Items.STONE_SHOVEL,
                Items.STONE_PICKAXE,
                Items.STONE_AXE,
                Items.STONE_HOE
        );

        valueLookupBuilder(GenesisTags.Items.COPPER_TOOLS).add(
                GenesisItems.COPPER_SWORD,
                GenesisItems.COPPER_SHOVEL,
                GenesisItems.COPPER_PICKAXE,
                GenesisItems.COPPER_AXE,
                GenesisItems.COPPER_HOE
        );

        valueLookupBuilder(GenesisTags.Items.GOLDEN_TOOLS).add(
                Items.GOLDEN_SWORD,
                Items.GOLDEN_SHOVEL,
                Items.GOLDEN_PICKAXE,
                Items.GOLDEN_AXE,
                Items.GOLDEN_HOE
        );

        valueLookupBuilder(GenesisTags.Items.IRON_TOOLS).add(
                Items.IRON_SWORD,
                Items.IRON_SHOVEL,
                Items.IRON_PICKAXE,
                Items.IRON_AXE,
                Items.IRON_HOE
        );

        valueLookupBuilder(GenesisTags.Items.DIAMOND_TOOLS).add(
                Items.DIAMOND_SWORD,
                Items.DIAMOND_SHOVEL,
                Items.DIAMOND_PICKAXE,
                Items.DIAMOND_AXE,
                Items.DIAMOND_HOE
        );

        valueLookupBuilder(GenesisTags.Items.INSTRUCTIONS_CLAY_TOOL_CASTS).add(
                GenesisItems.SWORD_CLAY_CAST,
                GenesisItems.SHOVEL_CLAY_CAST,
                GenesisItems.PICKAXE_CLAY_CAST,
                GenesisItems.AXE_CLAY_CAST,
                GenesisItems.HOE_CLAY_CAST
        );

        valueLookupBuilder(GenesisTags.Items.INSTRUCTIONS_TOOL_CASTS).add(
                GenesisItems.SWORD_CAST,
                GenesisItems.SHOVEL_CAST,
                GenesisItems.PICKAXE_CAST,
                GenesisItems.AXE_CAST,
                GenesisItems.HOE_CAST
        );

        valueLookupBuilder(GenesisTags.Items.FURNACES).add(Items.FURNACE, Items.SMOKER, Items.BLAST_FURNACE);

        valueLookupBuilder(GenesisTags.Items.HEALTHY_STEW_CONTENTS).add(
                Items.ALLIUM,
                Items.APPLE,
                Items.BEETROOT,
                Items.BLUE_ORCHID,
                Items.CARROT,
                Items.CORNFLOWER,
                Items.DANDELION,
                Items.GLOW_BERRIES,
                Items.MELON_SLICE,
                Items.OXEYE_DAISY,
                Items.POPPY,
                Items.PUMPKIN,
                Items.SWEET_BERRIES,
                Items.CACTUS_FLOWER
        );
    }
}
