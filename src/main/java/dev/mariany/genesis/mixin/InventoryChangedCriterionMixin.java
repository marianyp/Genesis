package dev.mariany.genesis.mixin;

import dev.mariany.genesis.recipe.DynamicHealthyStewRecipeProvider;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(InventoryChangedCriterion.class)
public class InventoryChangedCriterionMixin {
    @Inject(
            method = "trigger(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/item/ItemStack;)V",
            at = @At("TAIL")
    )
    public void trigger(ServerPlayerEntity player, PlayerInventory inventory, ItemStack stack, CallbackInfo ci) {
        List<RegistryKey<Recipe<?>>> recipesInvolving = DynamicHealthyStewRecipeProvider.getRecipesInvolving(stack.getItem())
                .stream()
                .map(id -> RegistryKey.of(RegistryKeys.RECIPE, id))
                .toList();

        if (!recipesInvolving.isEmpty()) {
            player.unlockRecipes(recipesInvolving);
        }
    }
}
