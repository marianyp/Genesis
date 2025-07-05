package dev.mariany.genesis.tag;

import dev.mariany.genesis.Genesis;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class GenesisTags {
    public static class Items {
        public static TagKey<Item> REPAIRS_COPPER_ARMOR = createTag("repairs_copper_armor");
        public static TagKey<Item> COPPER_TOOL_MATERIALS = createTag("copper_tool_materials");

        private static TagKey<Item> createTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, Genesis.id(name));
        }
    }

    public static class Blocks {
    }
}
