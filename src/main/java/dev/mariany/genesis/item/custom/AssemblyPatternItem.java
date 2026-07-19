package dev.mariany.genesis.item.custom;

import dev.mariany.genesis.recipe.CraftingPattern;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class AssemblyPatternItem extends Item {
    private final CraftingPattern craftingPattern;
    private final TagKey<Item> crafts;

    public AssemblyPatternItem(CraftingPattern craftingPattern, TagKey<Item> crafts, Properties settings) {
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
