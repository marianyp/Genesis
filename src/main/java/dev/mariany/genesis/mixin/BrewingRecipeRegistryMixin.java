package dev.mariany.genesis.mixin;

import dev.mariany.genesis.recipe.brew.BrewItemRecipe;
import dev.mariany.genesis.recipe.brew.GenesisBrewingRecipes;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.BrewingRecipeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(BrewingRecipeRegistry.class)
public class BrewingRecipeRegistryMixin {
    @Inject(method = "isPotionType", at = @At(value = "HEAD"), cancellable = true)
    private void injectIsPotionType(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (GenesisBrewingRecipes.getPotionBypasses().contains(stack.getItem())) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "craft", at = @At(value = "HEAD"), cancellable = true)
    public void injectCraft(ItemStack ingredient, ItemStack input, CallbackInfoReturnable<ItemStack> cir) {
        List<BrewItemRecipe> brewItemRecipes = GenesisBrewingRecipes.getBrewItemRecipes();

        for (BrewItemRecipe brewItemRecipe : brewItemRecipes) {
            boolean inputMatches = input.isOf(brewItemRecipe.from());
            boolean ingredientMatches = ingredient.isOf(brewItemRecipe.ingredient());

            if (inputMatches && ingredientMatches) {
                cir.setReturnValue(brewItemRecipe.to().getDefaultStack());
                break;
            }
        }
    }
}
