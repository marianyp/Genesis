package dev.mariany.genesis.compat.rei.display.adapter;

import dev.mariany.genesis.compat.rei.display.REIAssemblyDisplay;
import dev.mariany.genesis.compat.rei.display.REISiftingDisplay;
import dev.mariany.genesis.recipe.AssemblyRecipe;
import dev.mariany.genesis.recipe.GenesisRecipeTypes;
import dev.mariany.genesis.recipe.display.type.RecipeDisplayTypes;
import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry;
import me.shedaniel.rei.api.common.registry.display.ServerDisplayRegistry;
import me.shedaniel.rei.plugin.common.displays.crafting.CraftingDisplay;

import java.util.ArrayList;
import java.util.List;

public final class REIRecipeDisplayAdapters {
    private static final List<REIRecipeDisplayAdapter<? extends CraftingDisplay>> ALL = new ArrayList<>();

    public static final REIRecipeDisplayAdapter<REIAssemblyDisplay> ASSEMBLY = register(
            new REIRecipeDisplayAdapter<>(
                    RecipeDisplayTypes.ASSEMBLY,
                    REIAssemblyDisplay.SERIALIZER,
                    REIAssemblyDisplay.class,
                    REIAssemblyDisplay::getWidth,
                    REIAssemblyDisplay::getContents,
                    AssemblyRecipe.class,
                    GenesisRecipeTypes.ASSEMBLY,
                    REIAssemblyDisplay::new
            )
    );

    public static final REIRecipeDisplayAdapter<REISiftingDisplay> SIFTING = register(
            new REIRecipeDisplayAdapter<>(
                    RecipeDisplayTypes.SIFTING,
                    REISiftingDisplay.SERIALIZER,
                    REISiftingDisplay.class,
                    REISiftingDisplay::getWidth,
                    REISiftingDisplay::getContents
            )
    );

    private REIRecipeDisplayAdapters() {
    }

    private static <D extends CraftingDisplay> REIRecipeDisplayAdapter<D> register(
            REIRecipeDisplayAdapter<D> adapter
    ) {
        ALL.add(adapter);
        return adapter;
    }

    public static List<REIRecipeDisplayAdapter<? extends CraftingDisplay>> all() {
        return List.copyOf(ALL);
    }

    public static void registerSerializers(DisplaySerializerRegistry registry) {
        ALL.forEach(adapter -> adapter.registerSerializer(registry));
    }

    public static void registerDisplays(ServerDisplayRegistry registry) {
        ALL.forEach(adapter -> adapter.registerDisplays(registry));
    }

}
