package dev.mariany.genesis.mixin;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.recipe.override.ToolRecipeOverrider;
import net.minecraft.recipe.PreparedRecipes;
import net.minecraft.recipe.ServerRecipeManager;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.featuretoggle.FeatureSet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerRecipeManager.class)
public class ServerRecipeManagerMixin {
    @Shadow
    private PreparedRecipes preparedRecipes;

    @Shadow
    @Final
    private RegistryWrapper.WrapperLookup registries;

    @Inject(method = "initialize", at = @At("HEAD"))
    public void initialize(FeatureSet features, CallbackInfo ci) {
        ToolRecipeOverrider toolRecipeOverrider = new ToolRecipeOverrider(this.registries);
        this.preparedRecipes = toolRecipeOverrider.override(preparedRecipes.recipes());
        Genesis.LOGGER.info("Recipe override completed successfully!");
    }
}
