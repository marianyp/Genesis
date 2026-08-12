package dev.mariany.genesis.mixin;

import dev.mariany.genesis.event.world.GameRuleEvents;
import net.minecraft.world.level.gamerules.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(GameRules.class)
public class GameRulesMixin {
    @ModifyArgs(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/gamerules/GameRules;registerBoolean(Ljava/lang/String;Lnet/minecraft/world/level/gamerules/GameRuleCategory;Z)Lnet/minecraft/world/level/gamerules/GameRule;"
            )
    )
    private static void modifyRegisterBoolean(Args args) {
        args.set(2, GameRuleEvents.MODIFY_BOOLEAN_DEFAULT.invoker().modify(args.get(0), args.get(2)));
    }
}
