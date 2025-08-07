package dev.mariany.genesis.datagen;

import dev.mariany.genesis.item.GenesisItems;
import dev.mariany.genesis.tag.GenesisTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagBuilder;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.List;
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

        valueLookupBuilder(GenesisTags.Items.NETHERITE_ARMOR).add(
                Items.NETHERITE_HELMET,
                Items.NETHERITE_CHESTPLATE,
                Items.NETHERITE_LEGGINGS,
                Items.NETHERITE_BOOTS
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

        supportExternalMod(GenesisTags.Items.GOLDEN_TOOLS, "farmersdelight:golden_knife");

        valueLookupBuilder(GenesisTags.Items.IRON_TOOLS).add(
                Items.IRON_SWORD,
                Items.IRON_SHOVEL,
                Items.IRON_PICKAXE,
                Items.IRON_AXE,
                Items.IRON_HOE
        );

        supportExternalMod(GenesisTags.Items.IRON_TOOLS, "farmersdelight:iron_knife");

        valueLookupBuilder(GenesisTags.Items.DIAMOND_TOOLS).add(
                Items.DIAMOND_SWORD,
                Items.DIAMOND_SHOVEL,
                Items.DIAMOND_PICKAXE,
                Items.DIAMOND_AXE,
                Items.DIAMOND_HOE
        );

        supportExternalMod(GenesisTags.Items.DIAMOND_TOOLS, "farmersdelight:diamond_knife");

        valueLookupBuilder(GenesisTags.Items.NETHERITE_TOOLS).add(
                Items.NETHERITE_SWORD,
                Items.NETHERITE_SHOVEL,
                Items.NETHERITE_PICKAXE,
                Items.NETHERITE_AXE,
                Items.NETHERITE_HOE
        );

        supportExternalMod(GenesisTags.Items.NETHERITE_TOOLS, "farmersdelight:netherite_knife");

        valueLookupBuilder(GenesisTags.Items.INSTRUCTIONS_CLAY_TOOL_CASTS).add(
                GenesisItems.CLAY_SWORD_CAST,
                GenesisItems.CLAY_SHOVEL_CAST,
                GenesisItems.CLAY_PICKAXE_CAST,
                GenesisItems.CLAY_AXE_CAST,
                GenesisItems.CLAY_HOE_CAST
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
                Items.CACTUS_FLOWER,
                Items.CARROT,
                Items.CORNFLOWER,
                Items.DANDELION,
                Items.GLOW_BERRIES,
                Items.MELON_SLICE,
                Items.OXEYE_DAISY,
                Items.POPPY,
                Items.PUMPKIN,
                Items.SWEET_BERRIES
        );

        supportExternalMod(GenesisTags.Items.HEALTHY_STEW_CONTENTS, "biomesoplenty", List.of(
                        "orange_cosmos",
                        "pink_hibiscus",
                        "rose",
                        "violet"
                )
        );

        supportExternalMod(GenesisTags.Items.HEALTHY_STEW_CONTENTS, "farmersdelight", List.of(
                        "cabbage",
                        "onion",
                        "tomato"
                )
        );

        valueLookupBuilder(GenesisTags.Items.FROM_SWORD_CAST).addOptionalTag(ItemTags.SWORDS);
        valueLookupBuilder(GenesisTags.Items.FROM_SHOVEL_CAST).addOptionalTag(ItemTags.SHOVELS);
        valueLookupBuilder(GenesisTags.Items.FROM_PICKAXE_CAST).addOptionalTag(ItemTags.PICKAXES);
        valueLookupBuilder(GenesisTags.Items.FROM_AXE_CAST).addOptionalTag(ItemTags.AXES);
        valueLookupBuilder(GenesisTags.Items.FROM_HOE_CAST).addOptionalTag(ItemTags.HOES);
        valueLookupBuilder(GenesisTags.Items.FROM_SHIELD_CAST).add(Items.SHIELD);
        valueLookupBuilder(GenesisTags.Items.FROM_ANVIL_CAST).addOptionalTag(ItemTags.ANVIL);
        valueLookupBuilder(GenesisTags.Items.FROM_TOTEM_CAST).add(Items.TOTEM_OF_UNDYING);
    }

    private void supportExternalMod(TagKey<Item> tag, String item) {
        Identifier id = Identifier.of(item);
        supportExternalMod(tag, id.getNamespace(), List.of(id.getPath()));
    }

    private void supportExternalMod(TagKey<Item> tag, String modName, List<String> items) {
        TagBuilder tagBuilder = getTagBuilder(tag);

        for (String item : items) {
            tagBuilder.addOptional(Identifier.of(modName, item));
        }
    }
}
