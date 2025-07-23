package dev.mariany.genesis.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mariany.genesis.config.ConfigHandler;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(GameOptions.class)
public class GameOptionsMixin {
    @Unique
    private final SimpleOption<Double> lockedGamma = new SimpleOption<>(
            "options.gamma",
            SimpleOption.emptyTooltip(),
            (optionText, value) -> GameOptions.getGenericValueText(
                    optionText,
                    Text.translatable("options.gamma.min")
            ),
            SimpleOption.DoubleSliderCallbacks.INSTANCE,
            0D,
            value -> this.resetGamma()
    );

    @Inject(method = "getGamma", at = @At(value = "HEAD"), cancellable = true)
    public void injectGetGamma(CallbackInfoReturnable<SimpleOption<Double>> cir) {
        if (ConfigHandler.getConfig().enforceMoodyBrightness) {
            cir.setReturnValue(lockedGamma);
        }
    }

    @Unique
    private void resetGamma() {
        lockedGamma.setValue(0D);
    }

    @WrapOperation(
            method = "acceptProfiledOptions",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/option/GameOptions$OptionVisitor;accept(Ljava/lang/String;Lnet/minecraft/client/option/SimpleOption;)V"
            )
    )
    private <T> void wrapAcceptProfiledOptions(
            GameOptions.OptionVisitor instance,
            String key,
            SimpleOption<T> simpleOption,
            Operation<Void> original
    ) {
        if (ConfigHandler.getConfig().enforceMoodyBrightness) {
            if (Objects.equals(key, "gamma")) {
                original.call(instance, key, this.lockedGamma);
                return;
            }
        }

        original.call(instance, key, simpleOption);
    }
}
