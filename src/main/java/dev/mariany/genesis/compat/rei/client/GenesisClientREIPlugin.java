package dev.mariany.genesis.compat.rei.client;

import dev.mariany.genesis.block.GenesisBlocks;
import dev.mariany.genesis.compat.rei.client.category.REIRecipeDisplayOrdering;
import dev.mariany.genesis.compat.rei.display.adapter.REIRecipeDisplayAdapters;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandlerRegistry;
import me.shedaniel.rei.api.common.plugins.PluginManager;
import me.shedaniel.rei.api.common.registry.ReloadStage;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.common.BuiltinPlugin;

public class GenesisClientREIPlugin implements REIClientPlugin {
    private final REIRecipeDisplayOrdering recipeDisplayOrdering = new REIRecipeDisplayOrdering();

    @Override
    public void postStage(PluginManager<REIClientPlugin> manager, ReloadStage stage) {
        this.applyRecipeDisplayOrdering(manager, stage);
    }

    public void applyRecipeDisplayOrdering(PluginManager<REIClientPlugin> manager, ReloadStage stage) {
        if (stage != ReloadStage.END) {
            return;
        }

        CategoryRegistry registry = manager.get(CategoryRegistry.class);
        this.recipeDisplayOrdering.apply(registry);
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.addWorkstations(BuiltinPlugin.SMELTING, EntryStacks.of(GenesisBlocks.KILN));
        REIRecipeDisplays.registerCategories(registry);
    }

    @Override
    public void registerScreens(ScreenRegistry registry) {
        REIRecipeDisplays.registerScreens(registry);
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerDisplayGenerator(
                REIRecipeDisplayAdapters.SIFTING.categoryIdentifier(),
                new REISiftingDisplayGenerator()
        );
    }

    @Override
    public void registerTransferHandlers(TransferHandlerRegistry registry) {
        REIRecipeDisplays.registerTransferHandlers(registry);
    }
}
