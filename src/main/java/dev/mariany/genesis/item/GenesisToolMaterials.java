package dev.mariany.genesis.item;

import dev.mariany.genesis.tag.GenesisTags;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.BlockTags;

public class GenesisToolMaterials {
    public static final ToolMaterial COPPER = new ToolMaterial(
            BlockTags.INCORRECT_FOR_STONE_TOOL,
            190,
            5.0F,
            1.0F,
            13,
            GenesisTags.Items.COPPER_TOOL_MATERIALS
    );
}
