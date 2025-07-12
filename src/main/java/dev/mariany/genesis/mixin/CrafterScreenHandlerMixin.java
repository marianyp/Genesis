package dev.mariany.genesis.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mariany.genesis.age.AgeManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.CrafterScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CrafterScreenHandler.class)
public class CrafterScreenHandlerMixin {
    @Final
    @Shadow
    private PlayerEntity player;

    @WrapOperation(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/CraftingResultInventory;setStack(ILnet/minecraft/item/ItemStack;)V"))
    private void wrapUpdateResult(
            CraftingResultInventory craftingResultInventory,
            int slot,
            ItemStack stack,
            Operation<Void> original
    ) {
        if (this.player instanceof ServerPlayerEntity serverPlayerEntity) {
            if (AgeManager.getInstance().isUnlocked(serverPlayerEntity, stack)) {
                original.call(craftingResultInventory, slot, stack);
            }
        }
    }
}
