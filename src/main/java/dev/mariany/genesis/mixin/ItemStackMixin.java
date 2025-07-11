package dev.mariany.genesis.mixin;

import dev.mariany.genesis.advancement.criterion.GenesisCriteria;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(
            method = "onDurabilityChange",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;shouldBreak()Z")
    )
    private void injectOnDurabilityChange(int damage, @Nullable ServerPlayerEntity player, Consumer<Item> breakCallback, CallbackInfo ci) {
        ItemStack stack = ((ItemStack) (Object) this);

        if(player != null && stack.shouldBreak()) {
            GenesisCriteria.ITEM_BROKEN.trigger(player, stack);
        }
    }
}
