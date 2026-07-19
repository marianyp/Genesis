package dev.mariany.genesis.mixin;

import dev.mariany.genesis.recipe.brew.BrewItemRecipe;
import dev.mariany.genesis.recipe.brew.GenesisBrewingRecipes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionBrewing;

@Mixin(PotionBrewing.class)
public class BrewingRecipeRegistryMixin {
    @Inject(method = "isContainer", at = @At(value = "HEAD"), cancellable = true)
    private void injectIsPotionType(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (GenesisBrewingRecipes.getPotionBypasses().contains(stack.getItem())) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "mix", at = @At(value = "HEAD"), cancellable = true)
    public void injectCraft(ItemStack ingredient, ItemStack input, CallbackInfoReturnable<ItemStack> cir) {
        List<BrewItemRecipe> brewItemRecipes = GenesisBrewingRecipes.getRecipes();

        for (BrewItemRecipe brewItemRecipe : brewItemRecipes) {
            boolean inputMatches = input.is(brewItemRecipe.from());
            boolean ingredientMatches = ingredient.is(brewItemRecipe.ingredient());

            if (inputMatches && ingredientMatches) {
                cir.setReturnValue(brewItemRecipe.to().getDefaultInstance());
                break;
            }
        }
    }
}
