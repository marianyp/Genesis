package dev.mariany.genesis.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mariany.genesis.age.AgeManager;
import net.minecraft.block.CrafterBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeCache;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(CrafterBlock.class)
public class CrafterBlockMixin {
    @WrapOperation(
            method = "getCraftingRecipe",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/recipe/RecipeCache;getRecipe(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/recipe/input/CraftingRecipeInput;)Ljava/util/Optional;"
            )
    )
    private static Optional<RecipeEntry<CraftingRecipe>> wrapGetCraftingRecipe(
            RecipeCache instance, ServerWorld world, CraftingRecipeInput input, Operation<Optional<RecipeEntry<CraftingRecipe>>> original
    ) {
        Optional<RecipeEntry<CraftingRecipe>> optionalRecipe = original.call(instance, world, input);

        if (optionalRecipe.isPresent()) {
            RecipeEntry<CraftingRecipe> recipe = optionalRecipe.get();
            ItemStack stack = recipe.value().craft(input, world.getRegistryManager());

            if (AgeManager.getInstance().isAgeGuarded(stack.getItem())) {
                return Optional.empty();
            }
        }

        return original.call(instance, world, input);
    }
}
