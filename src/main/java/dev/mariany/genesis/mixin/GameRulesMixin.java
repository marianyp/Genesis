package dev.mariany.genesis.mixin;

import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(GameRules.class)
public class GameRulesMixin {
    @Final
    @Shadow
    private static Map<GameRules.Key<?>, GameRules.Type<?>> RULE_TYPES;

    @Inject(method = "register", at = @At("HEAD"), cancellable = true)
    private static <T extends GameRules.Rule<T>> void onRegister(String name, GameRules.Category category,
                                                                 GameRules.Type<T> oldType,
                                                                 CallbackInfoReturnable<GameRules.Key<T>> cir) {
        if (name.equals("naturalRegeneration")) {
            GameRules.Key<T> key = new GameRules.Key<>(name, category);
            GameRules.Type<GameRules.BooleanRule> type = GameRules.BooleanRule.create(false);
            RULE_TYPES.put(key, type);

            cir.setReturnValue(key);
        }
    }
}
