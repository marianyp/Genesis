package dev.mariany.genesis.mixin;

import dev.mariany.genesis.age.AgeEntry;
import dev.mariany.genesis.config.ConfigHandler;
import dev.mariany.genesis.instruction.InstructionEntry;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.PlacedAdvancement;
import net.minecraft.client.gui.screen.advancement.AdvancementTab;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Optional;

@Mixin(AdvancementsScreen.class)
public abstract class AdvancementsScreenMixin {
    @Shadow
    @Final
    private Map<AdvancementEntry, AdvancementTab> tabs;

    @Shadow
    public abstract void selectTab(@Nullable AdvancementEntry advancement);

    @Inject(method = "init", at = @At(value = "TAIL"))
    public void injectInit(CallbackInfo ci) {
        if (ConfigHandler.getConfig().alwaysStartOnAgesAdvancementScreen) {
            Optional<AdvancementEntry> optionalAgeRoot = this.tabs.keySet()
                    .stream()
                    .filter(advancementEntry -> advancementEntry.id().equals(AgeEntry.ROOT_ADVANCEMENT_ID))
                    .findFirst();

            optionalAgeRoot.ifPresent(this::selectTab);
        }
    }

    @Inject(method = "onRootAdded", at = @At(value = "HEAD"), cancellable = true)
    public void injectOnRootAdded(PlacedAdvancement root, CallbackInfo ci) {
        if (root.getAdvancementEntry().id().equals(InstructionEntry.ROOT_ADVANCEMENT_ID)) {
            ci.cancel();
        }
    }
}
