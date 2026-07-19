package dev.mariany.genesis.mixin;

import dev.mariany.genesis.recipe.DynamicAssemblyRecipeProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RecipeManager.class)
public class ServerRecipeManagerMixin {
    @Shadow
    private RecipeMap recipes;

    @Inject(method = "finalizeRecipeLoading", at = @At("HEAD"))
    public void initialize(FeatureFlagSet features, CallbackInfo ci) {
        DynamicAssemblyRecipeProvider dynamicAssemblyRecipeProvider = new DynamicAssemblyRecipeProvider();
        this.recipes = dynamicAssemblyRecipeProvider.provide(recipes.values());
    }
}
