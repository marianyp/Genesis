package dev.mariany.genesis.item;

import dev.mariany.genesis.tag.GenesisTags;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.BlockTags;

public class GenesisToolMaterials {
    public static final ToolMaterial COPPER = new ToolMaterial(
            BlockTags.INCORRECT_FOR_STONE_TOOL,
            191,
            5.5F,
            1.5F,
            12,
            GenesisTags.Items.COPPER_TOOL_MATERIALS
    );
}
