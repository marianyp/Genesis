package dev.mariany.genesis.mixin;

import dev.mariany.genesis.event.server.advancement.ServerAdvancementEvents;
import net.minecraft.advancement.Advancement;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ServerAdvancementLoader.class)
public class ServerAdvancementLoaderMixin {
    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V", at = @At("HEAD"))
    protected void apply(Map<Identifier, Advancement> map, ResourceManager resourceManager, Profiler profiler, CallbackInfo ci) {
        ServerAdvancementEvents.BEFORE_ADVANCEMENTS_LOAD.invoker().onAdvancementsLoaded(map);
    }
}
