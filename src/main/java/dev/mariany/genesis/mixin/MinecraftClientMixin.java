package dev.mariany.genesis.mixin;

import dev.mariany.genesis.client.instruction.ClientInstructionManager;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/toast/ToastManager;update()V"))
    private void injectRender(boolean tick, CallbackInfo ci) {
        ClientInstructionManager.getInstance().update();
    }
}
