package dev.mariany.genesis.mixin.compat.jei;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.mariany.genesis.compat.jei.event.JEIEvents;
import mezz.jei.api.recipe.category.IRecipeCategory;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Pseudo
@Mixin(targets = "mezz.jei.gui.recipes.RecipeSortUtil", remap = false)
public abstract class RecipeSortUtilMixin {
    @ModifyReturnValue(
            method = "sortRecipeCategories(Ljava/util/List;Lmezz/jei/api/recipe/transfer/IRecipeTransferManager;)Ljava/util/List;",
            at = @At("RETURN"),
            require = 0,
            remap = false
    )
    private static List<IRecipeCategory<?>> modifySortRecipeCategories(
            @Nullable List<IRecipeCategory<?>> sortedCategories
    ) {
        return sortedCategories == null
                ? null
                : JEIEvents.ORDER_RECIPE_CATEGORIES.invoker().order(sortedCategories);
    }
}
