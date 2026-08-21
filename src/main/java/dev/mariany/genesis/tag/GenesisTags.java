package dev.mariany.genesis.tag;

import dev.mariany.genesis.Genesis;
import net.fabricmc.fabric.api.tag.convention.v2.TagUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.Structure;

public class GenesisTags {
    private GenesisTags() {
    }

    public static final class Items {
        public static final TagKey<Item> LEATHER_ARMOR = createTag("leather_armor");
        public static final TagKey<Item> COPPER_ARMOR = createTag("copper_armor");
        public static final TagKey<Item> GOLDEN_ARMOR = createTag("golden_armor");
        public static final TagKey<Item> IRON_ARMOR = createTag("iron_armor");
        public static final TagKey<Item> DIAMOND_ARMOR = createTag("diamond_armor");

        public static final TagKey<Item> WOODEN_TOOLS = createTag("wooden_tools");
        public static final TagKey<Item> STONE_TOOLS = createTag("stone_tools");
        public static final TagKey<Item> COPPER_TOOLS = createTag("copper_tools");
        public static final TagKey<Item> GOLDEN_TOOLS = createTag("golden_tools");
        public static final TagKey<Item> IRON_TOOLS = createTag("iron_tools");
        public static final TagKey<Item> DIAMOND_TOOLS = createTag("diamond_tools");

        public static final TagKey<Item> WOODEN_PICKAXES = createTag("wooden_pickaxes");
        public static final TagKey<Item> WOODEN_SPEARS = createTag("wooden_spears");
        public static final TagKey<Item> CAMPFIRE_FUEL = createTag("campfire_fuel");

        public static final TagKey<Item> CLAY_TOOL_CASTS = createTag("clay_tool_casts");
        public static final TagKey<Item> TOOL_CASTS = createTag("tool_casts");

        public static final TagKey<Item> FURNACES = createTag("furnaces");

        public static final TagKey<Item> FROM_SWORD_CAST = createTag("from_sword_cast");
        public static final TagKey<Item> FROM_SHOVEL_CAST = createTag("from_shovel_cast");
        public static final TagKey<Item> FROM_PICKAXE_CAST = createTag("from_pickaxe_cast");
        public static final TagKey<Item> FROM_AXE_CAST = createTag("from_axe_cast");
        public static final TagKey<Item> FROM_HOE_CAST = createTag("from_hoe_cast");
        public static final TagKey<Item> FROM_SPEAR_CAST = createTag("from_spear_cast");
        public static final TagKey<Item> FROM_SHIELD_CAST = createTag("from_shield_cast");
        public static final TagKey<Item> FROM_ANVIL_CAST = createTag("from_anvil_cast");
        public static final TagKey<Item> FROM_TOTEM_CAST = createTag("from_totem_cast");

        public static final TagKey<Item> CAUSES_HUNGER = createTag("causes_hunger");

        public static final TagKey<Item> WEAPON_AGE_RESTRICTED_WEAPONS = createTag(
                "weapon_age_restricted_weapons"
        );

        private Items() {
        }

        private static TagKey<Item> createTag(String name) {
            return TagKey.create(Registries.ITEM, Genesis.id(name));
        }
    }

    public static final class Blocks {
        public static final TagKey<Block> BOAR_SPAWNABLE_ON = createTag("boar_spawnable_on");

        private Blocks() {
        }

        private static TagKey<Block> createTag(String name) {
            return TagKey.create(Registries.BLOCK, Genesis.id(name));
        }
    }

    public static final class Structures {
        public static final TagKey<Structure> ON_DEEP_DARK_EXPLORER_MAPS = createTag("on_deep_dark_explorer_maps");

        private Structures() {
        }

        private static TagKey<Structure> createTag(String name) {
            return TagKey.create(Registries.STRUCTURE, Genesis.id(name));
        }
    }

    public static final class Conventional {
        private Conventional() {
        }

        public static final class Items {
            public static final TagKey<Item> SMOKERS = createTag("smokers");

            private Items() {
            }

            private static TagKey<Item> createTag(String name) {
                return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(TagUtil.C_TAG_NAMESPACE, name));
            }
        }
    }
}
