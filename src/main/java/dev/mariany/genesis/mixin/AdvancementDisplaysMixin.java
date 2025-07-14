package dev.mariany.genesis.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mariany.genesis.age.AgeEntry;
import dev.mariany.genesis.age.AgeManager;
import net.minecraft.advancement.AdvancementDisplays;
import net.minecraft.advancement.PlacedAdvancement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;
import java.util.function.Predicate;

@Mixin(AdvancementDisplays.class)
public class AdvancementDisplaysMixin {
    @WrapOperation(
            method = "shouldDisplay(Lnet/minecraft/advancement/PlacedAdvancement;Lit/unimi/dsi/fastutil/Stack;Ljava/util/function/Predicate;Lnet/minecraft/advancement/AdvancementDisplays$ResultConsumer;)Z",
            at = @At(value = "INVOKE", target = "Ljava/util/function/Predicate;test(Ljava/lang/Object;)Z")
    )
    private static boolean wrapShouldDisplay(
            Predicate<PlacedAdvancement> predicate, Object object, Operation<Boolean> original
    ) {
        if (object instanceof PlacedAdvancement placedAdvancement) {
            Optional<AgeEntry> optionalAge = AgeManager.getInstance().find(placedAdvancement.getAdvancementEntry());

            if (optionalAge.isPresent()) {
                return true;
            }
        }

        return original.call(predicate, object);
    }
}
