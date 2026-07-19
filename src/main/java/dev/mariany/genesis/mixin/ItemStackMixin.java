package dev.mariany.genesis.mixin;

import dev.mariany.genesis.advancement.criterion.GenesisCriteria;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(
            method = "applyDamage",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isBroken()Z")
    )
    private void injectOnDurabilityChange(
            int damage,
            @Nullable ServerPlayer player,
            Consumer<Item> breakCallback,
            CallbackInfo ci
    ) {
        ItemStack stack = ((ItemStack) (Object) this);

        if (player != null && stack.isBroken()) {
            GenesisCriteria.ITEM_BROKEN.trigger(player, stack);
        }
    }
}
