package dev.mariany.genesis.mixin;

import dev.mariany.genesis.recipe.brew.GenesisBrewingRecipes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BrewingStandBlockEntity.class)
public class BrewingStandBlockEntityMixin {
    @Inject(method = "canPlaceItem", at = @At(value = "HEAD"), cancellable = true)
    public void injectIsValid(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        BrewingStandBlockEntity brewingStandBlockEntity = ((BrewingStandBlockEntity) (Object) this);

        if (slot != 3 && slot != 4 && brewingStandBlockEntity.getItem(slot).isEmpty()) {
            if (GenesisBrewingRecipes.getPotionBypasses().contains(stack.getItem())) {
                cir.setReturnValue(true);
            }
        }
    }
}
