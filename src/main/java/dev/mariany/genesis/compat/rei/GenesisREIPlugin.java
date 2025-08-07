package dev.mariany.genesis.compat.rei;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.compat.rei.display.AssemblyDisplay;
import dev.mariany.genesis.recipe.AssemblyRecipe;
import dev.mariany.genesis.recipe.GenesisRecipeTypes;
import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry;
import me.shedaniel.rei.api.common.plugins.REICommonPlugin;
import me.shedaniel.rei.api.common.registry.display.ServerDisplayRegistry;

public class GenesisREIPlugin implements REICommonPlugin {
    @Override
    public void registerDisplaySerializer(DisplaySerializerRegistry registry) {
        registry.register(Genesis.id("crafting/assembly"), AssemblyDisplay.SERIALIZER);
    }

    @Override
    public void registerDisplays(ServerDisplayRegistry registry) {
        registry.beginRecipeFiller(AssemblyRecipe.class)
                .filterType(GenesisRecipeTypes.ASSEMBLY)
                .fill(AssemblyDisplay::new);
    }
}
