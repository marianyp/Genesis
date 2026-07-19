package dev.mariany.genesis.datagen;

import dev.mariany.genesis.item.GenesisItems;
import dev.mariany.genesis.tag.GenesisTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GenesisItemTagProvider extends FabricTagsProvider.ItemTagsProvider {
    public GenesisItemTagProvider(
            FabricPackOutput output,
            CompletableFuture<HolderLookup.Provider> registriesFuture
    ) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        tag(GenesisTags.Items.LEATHER_ARMOR)
                .add(key(Items.LEATHER_HELMET))
                .add(key(Items.LEATHER_CHESTPLATE))
                .add(key(Items.LEATHER_LEGGINGS))
                .add(key(Items.LEATHER_BOOTS));

        tag(GenesisTags.Items.COPPER_ARMOR)
                .add(key(Items.COPPER_HELMET))
                .add(key(Items.COPPER_CHESTPLATE))
                .add(key(Items.COPPER_LEGGINGS))
                .add(key(Items.COPPER_BOOTS));

        tag(GenesisTags.Items.GOLDEN_ARMOR)
                .add(key(Items.GOLDEN_HELMET))
                .add(key(Items.GOLDEN_CHESTPLATE))
                .add(key(Items.GOLDEN_LEGGINGS))
                .add(key(Items.GOLDEN_BOOTS));

        tag(GenesisTags.Items.IRON_ARMOR)
                .add(key(Items.IRON_HELMET))
                .add(key(Items.IRON_CHESTPLATE))
                .add(key(Items.IRON_LEGGINGS))
                .add(key(Items.IRON_BOOTS));

        tag(GenesisTags.Items.DIAMOND_ARMOR)
                .add(key(Items.DIAMOND_HELMET))
                .add(key(Items.DIAMOND_CHESTPLATE))
                .add(key(Items.DIAMOND_LEGGINGS))
                .add(key(Items.DIAMOND_BOOTS));

        tag(GenesisTags.Items.NETHERITE_ARMOR)
                .add(key(Items.NETHERITE_HELMET))
                .add(key(Items.NETHERITE_CHESTPLATE))
                .add(key(Items.NETHERITE_LEGGINGS))
                .add(key(Items.NETHERITE_BOOTS));

        tag(GenesisTags.Items.WOODEN_TOOLS)
                .add(key(Items.WOODEN_SWORD))
                .add(key(Items.WOODEN_SHOVEL))
                .add(key(Items.WOODEN_PICKAXE))
                .add(key(Items.WOODEN_AXE))
                .add(key(Items.WOODEN_HOE))
                .add(key(Items.WOODEN_SPEAR));

        tag(GenesisTags.Items.STONE_TOOLS)
                .add(key(Items.STONE_SWORD))
                .add(key(Items.STONE_SHOVEL))
                .add(key(Items.STONE_PICKAXE))
                .add(key(Items.STONE_AXE))
                .add(key(Items.STONE_HOE))
                .add(key(Items.STONE_SPEAR));

        tag(GenesisTags.Items.COPPER_TOOLS)
                .add(key(Items.COPPER_SWORD))
                .add(key(Items.COPPER_SHOVEL))
                .add(key(Items.COPPER_PICKAXE))
                .add(key(Items.COPPER_AXE))
                .add(key(Items.COPPER_HOE))
                .add(key(Items.COPPER_SPEAR));

        supportExternalMod(GenesisTags.Items.COPPER_TOOLS, "farmersdelight:copper_knife");

        tag(GenesisTags.Items.GOLDEN_TOOLS)
                .add(key(Items.GOLDEN_SWORD))
                .add(key(Items.GOLDEN_SHOVEL))
                .add(key(Items.GOLDEN_PICKAXE))
                .add(key(Items.GOLDEN_AXE))
                .add(key(Items.GOLDEN_HOE))
                .add(key(Items.GOLDEN_SPEAR));

        supportExternalMod(GenesisTags.Items.GOLDEN_TOOLS, "farmersdelight:golden_knife");

        tag(GenesisTags.Items.IRON_TOOLS)
                .add(key(Items.IRON_SWORD))
                .add(key(Items.IRON_SHOVEL))
                .add(key(Items.IRON_PICKAXE))
                .add(key(Items.IRON_AXE))
                .add(key(Items.IRON_HOE))
                .add(key(Items.IRON_SPEAR));

        supportExternalMod(GenesisTags.Items.IRON_TOOLS, "farmersdelight:iron_knife");

        tag(GenesisTags.Items.DIAMOND_TOOLS)
                .add(key(Items.DIAMOND_SWORD))
                .add(key(Items.DIAMOND_SHOVEL))
                .add(key(Items.DIAMOND_PICKAXE))
                .add(key(Items.DIAMOND_AXE))
                .add(key(Items.DIAMOND_HOE))
                .add(key(Items.DIAMOND_SPEAR));

        supportExternalMod(GenesisTags.Items.DIAMOND_TOOLS, "farmersdelight:diamond_knife");

        tag(GenesisTags.Items.NETHERITE_TOOLS)
                .add(key(Items.NETHERITE_SWORD))
                .add(key(Items.NETHERITE_SHOVEL))
                .add(key(Items.NETHERITE_PICKAXE))
                .add(key(Items.NETHERITE_AXE))
                .add(key(Items.NETHERITE_HOE))
                .add(key(Items.NETHERITE_SPEAR));

        supportExternalMod(GenesisTags.Items.NETHERITE_TOOLS, "farmersdelight:netherite_knife");

        tag(GenesisTags.Items.CLAY_TOOL_CASTS)
                .add(key(GenesisItems.CLAY_SWORD_CAST))
                .add(key(GenesisItems.CLAY_SHOVEL_CAST))
                .add(key(GenesisItems.CLAY_PICKAXE_CAST))
                .add(key(GenesisItems.CLAY_AXE_CAST))
                .add(key(GenesisItems.CLAY_HOE_CAST))
                .add(key(GenesisItems.CLAY_SPEAR_CAST));

        tag(GenesisTags.Items.TOOL_CASTS)
                .add(key(GenesisItems.SWORD_CAST))
                .add(key(GenesisItems.SHOVEL_CAST))
                .add(key(GenesisItems.PICKAXE_CAST))
                .add(key(GenesisItems.AXE_CAST))
                .add(key(GenesisItems.HOE_CAST))
                .add(key(GenesisItems.SPEAR_CAST));

        tag(GenesisTags.Items.FURNACES).add(key(Items.FURNACE)).add(key(Items.SMOKER)).add(key(Items.BLAST_FURNACE));

        tag(GenesisTags.Items.FROM_SWORD_CAST).addOptionalTag(ItemTags.SWORDS);
        tag(GenesisTags.Items.FROM_SHOVEL_CAST).addOptionalTag(ItemTags.SHOVELS);
        tag(GenesisTags.Items.FROM_PICKAXE_CAST).addOptionalTag(ItemTags.PICKAXES);
        tag(GenesisTags.Items.FROM_AXE_CAST).addOptionalTag(ItemTags.AXES);
        tag(GenesisTags.Items.FROM_HOE_CAST).addOptionalTag(ItemTags.HOES);
        tag(GenesisTags.Items.FROM_SPEAR_CAST).addOptionalTag(ItemTags.SPEARS);
        tag(GenesisTags.Items.FROM_SHIELD_CAST).add(key(Items.SHIELD));
        tag(GenesisTags.Items.FROM_ANVIL_CAST).addOptionalTag(ItemTags.ANVIL);
        tag(GenesisTags.Items.FROM_TOTEM_CAST).add(key(Items.TOTEM_OF_UNDYING));

        tag(GenesisTags.Items.WEAPON_AGE_RESTRICTED_WEAPONS)
                .addOptionalTag(ItemTags.AXES)
                .addOptionalTag(ItemTags.SWORDS);

        tag(GenesisTags.Items.CAUSES_HUNGER)
                .add(key(Items.BEEF))
                .add(key(Items.PORKCHOP))
                .add(key(Items.MUTTON))
                .add(key(Items.CHICKEN))
                .add(key(Items.RABBIT));

        supportExternalMod(GenesisTags.Items.CAUSES_HUNGER, "farmersdelight:bacon");
        supportExternalMod(GenesisTags.Items.CAUSES_HUNGER, "farmersdelight:minced_beef");
        supportExternalMod(GenesisTags.Items.CAUSES_HUNGER, "farmersdelight:chicken_cuts");
        supportExternalMod(GenesisTags.Items.CAUSES_HUNGER, "farmersdelight:wheat_dough");
    }

    private static ResourceKey<Item> key(Item item) {
        return item.builtInRegistryHolder().key();
    }

    private void supportExternalMod(TagKey<Item> tag, String item) {
        Identifier id = Identifier.parse(item);
        supportExternalMod(tag, id.getNamespace(), List.of(id.getPath()));
    }

    private void supportExternalMod(TagKey<Item> tag, String modName, List<String> items) {
        TagBuilder tagBuilder = getOrCreateRawBuilder(tag);

        for (String item : items) {
            tagBuilder.addOptionalElement(Identifier.fromNamespaceAndPath(modName, item));
        }
    }
}
