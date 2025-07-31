package dev.mariany.genesis.item.custom;

import dev.mariany.genesis.recipe.CraftingPattern;
import net.minecraft.item.Item;
import net.minecraft.registry.tag.TagKey;

public class AssemblyPatternItem extends Item {
    private final CraftingPattern craftingPattern;
    private final TagKey<Item> crafts;

    public AssemblyPatternItem(CraftingPattern craftingPattern, TagKey<Item> crafts, Settings settings) {
        super(settings);
        this.craftingPattern = craftingPattern;
        this.crafts = crafts;
    }

    public CraftingPattern getCraftingPattern() {
        return craftingPattern;
    }

    public TagKey<Item> getCrafts() {
        return crafts;
    }
}
