package dev.mariany.genesis.tag;

import dev.mariany.genesis.Genesis;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.Structure;

public class GenesisTags {
    public static final class Items {
        public static final TagKey<Item> REPAIRS_COPPER_ARMOR = createTag("repairs_copper_armor");
        public static final TagKey<Item> COPPER_TOOL_MATERIALS = createTag("copper_tool_materials");

        public static final TagKey<Item> LEATHER_ARMOR = createTag("leather_armor");
        public static final TagKey<Item> COPPER_ARMOR = createTag("copper_armor");
        public static final TagKey<Item> GOLDEN_ARMOR = createTag("golden_armor");
        public static final TagKey<Item> IRON_ARMOR = createTag("iron_armor");
        public static final TagKey<Item> DIAMOND_ARMOR = createTag("diamond_armor");
        public static final TagKey<Item> NETHERITE_ARMOR = createTag("netherite_armor");

        public static final TagKey<Item> WOODEN_TOOLS = createTag("wooden_tools");
        public static final TagKey<Item> STONE_TOOLS = createTag("stone_tools");
        public static final TagKey<Item> COPPER_TOOLS = createTag("copper_tools");
        public static final TagKey<Item> GOLDEN_TOOLS = createTag("golden_tools");
        public static final TagKey<Item> IRON_TOOLS = createTag("iron_tools");
        public static final TagKey<Item> DIAMOND_TOOLS = createTag("diamond_tools");
        public static final TagKey<Item> NETHERITE_TOOLS = createTag("netherite_tools");

        public static final TagKey<Item> INSTRUCTIONS_CLAY_TOOL_CASTS = createTag("instructions_clay_tool_casts");
        public static final TagKey<Item> INSTRUCTIONS_TOOL_CASTS = createTag("instructions_tool_casts");

        public static final TagKey<Item> FURNACES = createTag("furnaces");

        public static final TagKey<Item> HEALTHY_STEW_CONTENTS = createTag("healthy_stew_contents");

        public static final TagKey<Item> FROM_ASSEMBLY_TABLE = createTag("from_assembly_table");

        public static final TagKey<Item> FROM_SWORD_CAST = createTag("from_sword_cast");
        public static final TagKey<Item> FROM_SHOVEL_CAST = createTag("from_shovel_cast");
        public static final TagKey<Item> FROM_PICKAXE_CAST = createTag("from_pickaxe_cast");
        public static final TagKey<Item> FROM_AXE_CAST = createTag("from_axe_cast");
        public static final TagKey<Item> FROM_HOE_CAST = createTag("from_hoe_cast");
        public static final TagKey<Item> FROM_SHIELD_CAST = createTag("from_shield_cast");
        public static final TagKey<Item> FROM_ANVIL_CAST = createTag("from_anvil_cast");
        public static final TagKey<Item> FROM_TOTEM_CAST = createTag("from_totem_cast");

        private static TagKey<Item> createTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, Genesis.id(name));
        }
    }

    public static final class Blocks {
        public static final TagKey<Block> BOAR_SPAWNABLE_ON = createTag("boar_spawnable_on");

        private static TagKey<Block> createTag(String name) {
            return TagKey.of(RegistryKeys.BLOCK, Genesis.id(name));
        }
    }

    public static final class Structures {
        public static final TagKey<Structure> ON_DEEP_DARK_EXPLORER_MAPS = createTag("on_deep_dark_explorer_maps");

        private static TagKey<Structure> createTag(String name) {
            return TagKey.of(RegistryKeys.STRUCTURE, Genesis.id(name));
        }
    }

    public static final class Biomes {
        public static final TagKey<Biome> ON_PALE_GARDEN_EXPLORER_MAPS = createTag("on_pale_garden_explorer_maps");

        private static TagKey<Biome> createTag(String name) {
            return TagKey.of(RegistryKeys.BIOME, Genesis.id(name));
        }
    }
}
