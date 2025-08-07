package dev.mariany.genesis.compat.rei.client;

import dev.mariany.genesis.block.GenesisBlocks;
import dev.mariany.genesis.compat.rei.REICategoryIdentifiers;
import dev.mariany.genesis.compat.rei.client.categories.crafting.AssemblyCategory;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.common.BuiltinPlugin;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;

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
                new Rectangle(88, 32, 28, 23),
                CraftingScreen.class,
                REICategoryIdentifiers.ASSEMBLY
        );

        registry.registerContainerClickArea(
                new Rectangle(137, 29, 10, 13),
                InventoryScreen.class,
                REICategoryIdentifiers.ASSEMBLY
        );
    }
}
