package dev.mariany.genesis.compat.rei.client;

import dev.mariany.genesis.block.GenesisBlocks;
import dev.mariany.genesis.client.gui.screen.ingame.AssemblyScreen;
import dev.mariany.genesis.compat.rei.REICategoryIdentifiers;
import dev.mariany.genesis.compat.rei.client.categories.crafting.AssemblyCategory;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.common.BuiltinPlugin;

public class GenesisClientREIPlugin implements REIClientPlugin {
    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new AssemblyCategory());

        registry.addWorkstations(BuiltinPlugin.SMELTING, EntryStacks.of(GenesisBlocks.KILN));
        registry.addWorkstations(REICategoryIdentifiers.ASSEMBLY, EntryStacks.of(GenesisBlocks.ASSEMBLY_TABLE));
    }

    @Override
    public void registerScreens(ScreenRegistry registry) {
        registry.registerContainerClickArea(
                new Rectangle(111, 32, 28, 23),
                AssemblyScreen.class,
                REICategoryIdentifiers.ASSEMBLY
        );
    }
}
