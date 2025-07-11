package dev.mariany.genesis.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mariany.genesis.age.AgeManager;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.ServerRecipeManager;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerRecipeBook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @Shadow
    public ServerPlayerEntity player;

    @WrapOperation(method = "onCraftRequest", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerRecipeBook;isUnlocked(Lnet/minecraft/registry/RegistryKey;)Z"))
    public boolean wrapOnCraftRequest(ServerRecipeBook recipeBook, RegistryKey<Recipe<?>> recipeKey, Operation<Boolean> original) {
        MinecraftServer server = player.getServer();

        if (server != null) {
            ServerRecipeManager recipeManager = server.getRecipeManager();
            Optional<RecipeEntry<?>> recipeEntry = recipeManager.get(recipeKey);

            if (recipeEntry.isPresent()) {
                if (recipeEntry.get().value() instanceof CraftingRecipe craftingRecipe) {
                    ItemStack stack = craftingRecipe.craft(CraftingRecipeInput.EMPTY, server.getRegistryManager());

                    if (!AgeManager.getInstance().isUnlocked(player, stack)) {
                        return false;
                    }
                }
            }
        }

        return original.call(recipeBook, recipeKey);
    }
}
