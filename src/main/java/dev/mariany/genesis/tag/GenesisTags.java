package dev.mariany.genesis.tag;

import dev.mariany.genesis.Genesis;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class GenesisTags {
    public static class Items {
        public static TagKey<Item> REPAIRS_COPPER_ARMOR = createTag("repairs_copper_armor");
        public static TagKey<Item> COPPER_TOOL_MATERIALS = createTag("copper_tool_materials");

        public static TagKey<Item> LEATHER_ARMOR = createTag("leather_armor");
        public static TagKey<Item> COPPER_ARMOR = createTag("copper_armor");
        public static TagKey<Item> GOLDEN_ARMOR = createTag("golden_armor");
        public static TagKey<Item> IRON_ARMOR = createTag("iron_armor");
        public static TagKey<Item> DIAMOND_ARMOR = createTag("diamond_armor");

        public static TagKey<Item> WOODEN_TOOLS = createTag("wooden_tools");
        public static TagKey<Item> STONE_TOOLS = createTag("stone_tools");
        public static TagKey<Item> COPPER_TOOLS = createTag("copper_tools");
        public static TagKey<Item> GOLDEN_TOOLS = createTag("golden_tools");
        public static TagKey<Item> IRON_TOOLS = createTag("iron_tools");
        public static TagKey<Item> DIAMOND_TOOLS = createTag("diamond_tools");

        public static TagKey<Item> FURNACES = createTag("furnaces");

        public static TagKey<Item> HEALTHY_STEW_CONTENTS = createTag("healthy_stew_contents");

        private static TagKey<Item> createTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, Genesis.id(name));
        }
    }

    public static class Blocks {
    }
}
