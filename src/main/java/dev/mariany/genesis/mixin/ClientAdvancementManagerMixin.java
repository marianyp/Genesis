package dev.mariany.genesis.mixin;

import dev.mariany.genesis.client.instruction.ClientInstructionManager;
import net.minecraft.client.network.ClientAdvancementManager;
import net.minecraft.network.packet.s2c.play.AdvancementUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientAdvancementManager.class)
public class ClientAdvancementManagerMixin {
    @Inject(method = "onAdvancements", at = @At("TAIL"))
    private void onAdvancementUpdate(AdvancementUpdateS2CPacket packet, CallbackInfo ci) {
        ClientInstructionManager.getInstance().refreshInstructionToasts();
    }
}
