package dev.mariany.genesis.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mariany.genesis.client.age.ClientAgeManager;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeDisplayEntry;
import net.minecraft.recipe.display.SlotDisplay;
import net.minecraft.util.context.ContextParameterMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(RecipeDisplayEntry.class)
public class RecipeDisplayEntryMixin {
    @WrapOperation(
            method = "getStacks",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/recipe/display/SlotDisplay;getStacks(Lnet/minecraft/util/context/ContextParameterMap;)Ljava/util/List;"
            )
    )
    public List<ItemStack> getStacks(SlotDisplay slotDisplay, ContextParameterMap parameters, Operation<List<ItemStack>> original) {
        return original.call(slotDisplay, parameters).stream().filter(stack -> ClientAgeManager.getInstance().isUnlocked(stack)).toList();
    }
}
