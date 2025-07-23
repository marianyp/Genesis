package dev.mariany.genesis.mixin;

import dev.mariany.genesis.advancement.criterion.GenesisCriteria;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.network.packet.c2s.play.AdvancementTabC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayerNetworkHandlerMixin {
    @Shadow
    public ServerPlayerEntity player;

    @Inject(method = "onAdvancementTab", at = @At(value = "TAIL"))
    public void injectOnAdvancementTab(AdvancementTabC2SPacket packet, CallbackInfo ci) {
        MinecraftServer server = player.getServer();

        if (server != null) {
            if (packet.getAction() == AdvancementTabC2SPacket.Action.OPENED_TAB) {
                Identifier advancementId = Objects.requireNonNull(packet.getTabToOpen());
                AdvancementEntry advancementEntry = server.getAdvancementLoader().get(advancementId);

                if (advancementEntry != null) {
                    GenesisCriteria.OPEN_ADVANCEMENT_TAB.trigger(player, advancementId);
                }
            }
        }
    }
}
