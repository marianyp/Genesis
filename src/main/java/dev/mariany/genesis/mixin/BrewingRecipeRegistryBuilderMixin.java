package dev.mariany.genesis.mixin;

import dev.mariany.genesis.recipe.brew.GenesisBrewingRecipes;
import net.minecraft.item.Item;
import net.minecraft.recipe.BrewingRecipeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BrewingRecipeRegistry.Builder.class)
public class BrewingRecipeRegistryBuilderMixin {
    @Inject(method = "assertPotion", at = @At(value = "HEAD"), cancellable = true)
    private static void injectAssertPotion(Item potionType, CallbackInfo ci) {
        if (GenesisBrewingRecipes.getPotionBypasses().contains(potionType)) {
            ci.cancel();
        }
    }
}
