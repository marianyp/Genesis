package dev.mariany.genesis.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.screen.recipebook.AnimatedResultButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(AnimatedResultButton.class)
public class AnimatedResultButtonMixin {
    @Shadow
    private List<AnimatedResultButton.Result> results;

    @WrapOperation(
            method = "showResultCollection",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/recipebook/AnimatedResultButton;areAllResultsEqual(Ljava/util/List;)Z"
            )
    )
    public boolean injectShowResultCollection(
            List<AnimatedResultButton.Result> results, Operation<Boolean> original
    ) {
        this.results = this.results.stream().filter(result -> !result.displayItems().isEmpty()).toList();
        return original.call(this.results);
    }
}
