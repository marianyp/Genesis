package dev.mariany.genesis.mixin.accessor;

import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractRecipeBookScreen.class)
public interface AbstractRecipeBookScreenAccessor {
    @Accessor("recipeBookComponent")
    RecipeBookComponent<?> genesis$recipeBookComponent();

    @Invoker("getRecipeBookButtonPosition")
    ScreenPosition genesis$getRecipeBookButtonPosition();

    @Invoker("onRecipeBookButtonClick")
    void genesis$onRecipeBookButtonClick();
}
