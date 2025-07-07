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
    }
}
