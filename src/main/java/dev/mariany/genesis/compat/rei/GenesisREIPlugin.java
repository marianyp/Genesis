package dev.mariany.genesis.compat.rei;

import dev.mariany.genesis.compat.rei.display.adapter.REIRecipeDisplayAdapters;
import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry;
import me.shedaniel.rei.api.common.plugins.REICommonPlugin;
import me.shedaniel.rei.api.common.registry.display.ServerDisplayRegistry;

public class GenesisREIPlugin implements REICommonPlugin {
    @Override
    public void registerDisplaySerializer(DisplaySerializerRegistry registry) {
        REIRecipeDisplayAdapters.registerSerializers(registry);
    }

    @Override
    public void registerDisplays(ServerDisplayRegistry registry) {
        REIRecipeDisplayAdapters.registerDisplays(registry);
    }
}
