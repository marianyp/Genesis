package dev.mariany.genesis.mixin;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.gamerules.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.ToIntFunction;

@Mixin(GameRules.class)
public class GameRulesMixin {
    @Inject(method = "register", at = @At("HEAD"), cancellable = true)
    private static <T> void injectRegister(
            String id,
            GameRuleCategory category,
            GameRuleType typeHint,
            ArgumentType<T> argumentType,
            Codec<T> codec,
            T defaultValue,
            FeatureFlagSet requiredFeatures,
            GameRules.VisitorCaller<T> visitorCaller,
            ToIntFunction<T> commandResultFunction,
            CallbackInfoReturnable<GameRule<T>> cir
    ) {
        if (!id.equals("natural_health_regeneration") || typeHint != GameRuleType.BOOL) {
            return;
        }

        @SuppressWarnings({"unchecked"})
        GameRule<T> gameRule = (GameRule<T>) Registry.register(
                BuiltInRegistries.GAME_RULE,
                id,
                new GameRule<Boolean>(
                        category,
                        GameRuleType.BOOL,
                        BoolArgumentType.bool(),
                        GameRuleTypeVisitor::visitBoolean,
                        Codec.BOOL,
                        value -> value ? 1 : 0,
                        false,
                        requiredFeatures
                )
        );

        cir.setReturnValue(gameRule);
    }
}
