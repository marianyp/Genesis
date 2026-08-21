package dev.mariany.genesis.datagen;

import dev.mariany.genesis.item.GenesisItems;
import dev.mariany.genesis.tag.GenesisTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.properties.WoodType;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class GenesisItemTagProvider extends FabricTagsProvider.ItemTagsProvider {
    private static final String MORE_TOOL_VARIANTS_MOD_ID = "mstv-mtoolv";
    private static final String MORE_WEAPON_VARIANTS_MOD_ID = "mstv-mweaponv";
    private static final String MORE_STICK_VARIANTS_MOD_ID = "mstv-base";

    private static final List<String> TOOL_TYPES = List.of("shovel", "pickaxe", "axe", "hoe");
    private static final List<String> WEAPON_TYPES = List.of("sword", "spear");

    public GenesisItemTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        this.tag(GenesisTags.Items.LEATHER_ARMOR)
            .add(key(Items.LEATHER_HELMET))
            .add(key(Items.LEATHER_CHESTPLATE))
            .add(key(Items.LEATHER_LEGGINGS))
            .add(key(Items.LEATHER_BOOTS));

        this.tag(GenesisTags.Items.COPPER_ARMOR)
            .add(key(Items.COPPER_HELMET))
            .add(key(Items.COPPER_CHESTPLATE))
            .add(key(Items.COPPER_LEGGINGS))
            .add(key(Items.COPPER_BOOTS));

        this.tag(GenesisTags.Items.GOLDEN_ARMOR)
            .add(key(Items.GOLDEN_HELMET))
            .add(key(Items.GOLDEN_CHESTPLATE))
            .add(key(Items.GOLDEN_LEGGINGS))
            .add(key(Items.GOLDEN_BOOTS));

        this.tag(GenesisTags.Items.IRON_ARMOR)
            .add(key(Items.IRON_HELMET))
            .add(key(Items.IRON_CHESTPLATE))
            .add(key(Items.IRON_LEGGINGS))
            .add(key(Items.IRON_BOOTS));

        this.tag(GenesisTags.Items.DIAMOND_ARMOR)
            .add(key(Items.DIAMOND_HELMET))
            .add(key(Items.DIAMOND_CHESTPLATE))
            .add(key(Items.DIAMOND_LEGGINGS))
            .add(key(Items.DIAMOND_BOOTS));

        this.tag(GenesisTags.Items.WOODEN_TOOLS)
            .add(key(Items.WOODEN_SWORD))
            .add(key(Items.WOODEN_SHOVEL))
            .add(key(Items.WOODEN_PICKAXE))
            .add(key(Items.WOODEN_AXE))
            .add(key(Items.WOODEN_HOE))
            .add(key(Items.WOODEN_SPEAR));

        this.supportTieredMoreVariantsItems(GenesisTags.Items.WOODEN_TOOLS, "wooden");

        this.tag(GenesisTags.Items.STONE_TOOLS)
            .add(key(Items.STONE_SWORD))
            .add(key(Items.STONE_SHOVEL))
            .add(key(Items.STONE_PICKAXE))
            .add(key(Items.STONE_AXE))
            .add(key(Items.STONE_HOE))
            .add(key(Items.STONE_SPEAR));

        this.supportTieredMoreVariantsItems(GenesisTags.Items.STONE_TOOLS, "stone");
        this.supportTieredMoreVariantsItems(GenesisTags.Items.STONE_TOOLS, "deepslate", true);
        this.supportTieredMoreVariantsItems(GenesisTags.Items.STONE_TOOLS, "blackstone", true);

        this.tag(GenesisTags.Items.COPPER_TOOLS)
            .add(key(Items.COPPER_SWORD))
            .add(key(Items.COPPER_SHOVEL))
            .add(key(Items.COPPER_PICKAXE))
            .add(key(Items.COPPER_AXE))
            .add(key(Items.COPPER_HOE))
            .add(key(Items.COPPER_SPEAR));

        this.supportExternalMod(GenesisTags.Items.COPPER_TOOLS, "farmersdelight:copper_knife");

        this.supportTieredMoreVariantsItems(GenesisTags.Items.COPPER_TOOLS, "copper");

        this.tag(GenesisTags.Items.GOLDEN_TOOLS)
            .add(key(Items.GOLDEN_SWORD))
            .add(key(Items.GOLDEN_SHOVEL))
            .add(key(Items.GOLDEN_PICKAXE))
            .add(key(Items.GOLDEN_AXE))
            .add(key(Items.GOLDEN_HOE))
            .add(key(Items.GOLDEN_SPEAR));

        this.supportExternalMod(GenesisTags.Items.GOLDEN_TOOLS, "farmersdelight:golden_knife");

        this.supportTieredMoreVariantsItems(GenesisTags.Items.GOLDEN_TOOLS, "golden");

        this.tag(GenesisTags.Items.IRON_TOOLS)
            .add(key(Items.IRON_SWORD))
            .add(key(Items.IRON_SHOVEL))
            .add(key(Items.IRON_PICKAXE))
            .add(key(Items.IRON_AXE))
            .add(key(Items.IRON_HOE))
            .add(key(Items.IRON_SPEAR));

        this.supportExternalMod(GenesisTags.Items.IRON_TOOLS, "farmersdelight:iron_knife");

        this.supportTieredMoreVariantsItems(GenesisTags.Items.IRON_TOOLS, "iron");

        this.tag(GenesisTags.Items.DIAMOND_TOOLS)
            .add(key(Items.DIAMOND_SWORD))
            .add(key(Items.DIAMOND_SHOVEL))
            .add(key(Items.DIAMOND_PICKAXE))
            .add(key(Items.DIAMOND_AXE))
            .add(key(Items.DIAMOND_HOE))
            .add(key(Items.DIAMOND_SPEAR));

        this.supportExternalMod(GenesisTags.Items.DIAMOND_TOOLS, "farmersdelight:diamond_knife");

        this.supportTieredMoreVariantsItems(GenesisTags.Items.DIAMOND_TOOLS, "diamond");

        this.tag(GenesisTags.Items.WOODEN_PICKAXES).add(key(Items.WOODEN_PICKAXE));

        this.supportMoreVariants(
                GenesisTags.Items.WOODEN_PICKAXES,
                MORE_TOOL_VARIANTS_MOD_ID,
                "wooden",
                List.of("pickaxe")
        );

        this.tag(GenesisTags.Items.WOODEN_SPEARS).add(key(Items.WOODEN_SPEAR));

        this.supportMoreVariants(
                GenesisTags.Items.WOODEN_SPEARS,
                MORE_WEAPON_VARIANTS_MOD_ID,
                "wooden",
                List.of("spear")
        );

        this.tag(GenesisTags.Items.CAMPFIRE_FUEL)
            .add(key(Items.STICK))
            .addOptionalTag(tagKey(MORE_STICK_VARIANTS_MOD_ID, "stick_variants"));

        this.tag(GenesisTags.Items.CLAY_TOOL_CASTS)
            .add(key(GenesisItems.CLAY_SWORD_CAST))
            .add(key(GenesisItems.CLAY_SHOVEL_CAST))
            .add(key(GenesisItems.CLAY_PICKAXE_CAST))
            .add(key(GenesisItems.CLAY_AXE_CAST))
            .add(key(GenesisItems.CLAY_HOE_CAST))
            .add(key(GenesisItems.CLAY_SPEAR_CAST));

        this.tag(GenesisTags.Items.TOOL_CASTS)
            .add(key(GenesisItems.SWORD_CAST))
            .add(key(GenesisItems.SHOVEL_CAST))
            .add(key(GenesisItems.PICKAXE_CAST))
            .add(key(GenesisItems.AXE_CAST))
            .add(key(GenesisItems.HOE_CAST))
            .add(key(GenesisItems.SPEAR_CAST));

        this.tag(GenesisTags.Items.FURNACES)
            .addOptionalTag(ConventionalItemTags.PLAYER_WORKSTATIONS_FURNACES)
            .addOptionalTag(GenesisTags.Conventional.Items.SMOKERS)
            .add(key(Items.BLAST_FURNACE));

        this.tag(GenesisTags.Items.FROM_SWORD_CAST).addOptionalTag(ItemTags.SWORDS);
        this.tag(GenesisTags.Items.FROM_SHOVEL_CAST).addOptionalTag(ItemTags.SHOVELS);
        this.tag(GenesisTags.Items.FROM_PICKAXE_CAST).addOptionalTag(ItemTags.PICKAXES);
        this.tag(GenesisTags.Items.FROM_AXE_CAST).addOptionalTag(ItemTags.AXES);
        this.tag(GenesisTags.Items.FROM_HOE_CAST).addOptionalTag(ItemTags.HOES);
        this.tag(GenesisTags.Items.FROM_SPEAR_CAST).addOptionalTag(ItemTags.SPEARS);
        this.tag(GenesisTags.Items.FROM_SHIELD_CAST).addOptionalTag(ConventionalItemTags.SHIELD_TOOLS);
        this.tag(GenesisTags.Items.FROM_ANVIL_CAST).addOptionalTag(ItemTags.ANVIL);
        this.tag(GenesisTags.Items.FROM_TOTEM_CAST).add(key(Items.TOTEM_OF_UNDYING));

        this.tag(GenesisTags.Items.WEAPON_AGE_RESTRICTED_WEAPONS)
            .addOptionalTag(ItemTags.AXES)
            .addOptionalTag(ItemTags.SWORDS);

        this.tag(GenesisTags.Items.CAUSES_HUNGER)
            .add(key(Items.BEEF))
            .add(key(Items.PORKCHOP))
            .add(key(Items.MUTTON))
            .add(key(Items.CHICKEN))
            .add(key(Items.RABBIT));

        this.supportExternalMod(GenesisTags.Items.CAUSES_HUNGER, "farmersdelight:bacon");
        this.supportExternalMod(GenesisTags.Items.CAUSES_HUNGER, "farmersdelight:minced_beef");
        this.supportExternalMod(GenesisTags.Items.CAUSES_HUNGER, "farmersdelight:chicken_cuts");
        this.supportExternalMod(GenesisTags.Items.CAUSES_HUNGER, "farmersdelight:wheat_dough");
    }

    private static ResourceKey<Item> key(Item item) {
        return item.builtInRegistryHolder().key();
    }

    private static TagKey<Item> tagKey(String namespace, String path) {
        return TagKey.create(
                Registries.ITEM,
                Identifier.fromNamespaceAndPath(namespace, path)
        );
    }

    private void supportExternalMod(TagKey<Item> tag, String item) {
        Identifier id = Identifier.parse(item);
        this.supportExternalMod(tag, id.getNamespace(), List.of(id.getPath()));
    }

    private void supportExternalMod(TagKey<Item> tag, String modName, List<String> items) {
        TagBuilder tagBuilder = this.getOrCreateRawBuilder(tag);

        for (String item : items) {
            tagBuilder.addOptionalElement(Identifier.fromNamespaceAndPath(modName, item));
        }
    }

    private void supportTieredMoreVariantsItems(TagKey<Item> tag, String material) {
        this.supportTieredMoreVariantsItems(tag, material, false);
    }

    private void supportTieredMoreVariantsItems(TagKey<Item> tag, String material, boolean includeOak) {
        this.supportMoreVariants(tag, MORE_TOOL_VARIANTS_MOD_ID, material, TOOL_TYPES, includeOak);
        this.supportMoreVariants(tag, MORE_WEAPON_VARIANTS_MOD_ID, material, WEAPON_TYPES, includeOak);
    }

    private void supportMoreVariants(
            TagKey<Item> tag,
            String namespace,
            String material,
            List<String> itemTypes
    ) {
        this.supportMoreVariants(tag, namespace, material, itemTypes, false);
    }

    private void supportMoreVariants(
            TagKey<Item> tag,
            String namespace,
            String material,
            List<String> itemTypes,
            boolean includeOak
    ) {
        TagBuilder tagBuilder = this.getOrCreateRawBuilder(tag);

        WoodType.values()
                .filter(woodType -> includeOak || !Objects.equals(WoodType.OAK, woodType))
                .forEach(woodType -> {
                    for (String itemType : itemTypes) {
                        Identifier id = Identifier.fromNamespaceAndPath(
                                namespace,
                                woodType.name() + "_" + material + "_" + itemType
                        );

                        tagBuilder.addOptionalElement(id);
                    }
                });
    }
}
