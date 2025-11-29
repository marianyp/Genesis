package dev.mariany.genesis.mixin;

import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;
import dev.mariany.genesis.datafixer.CopperItemsFix;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.Schemas;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BiFunction;

@Mixin(Schemas.class)
public abstract class SchemasMixin {
    @Shadow
    @Final
    private static BiFunction<Integer, Schema, Schema> EMPTY_IDENTIFIER_NORMALIZE;

    @Inject(method = "build", at = @At(value = "TAIL"))
    private static void build(DataFixerBuilder builder, CallbackInfo ci) {
        Schema schema = builder.addSchema(
                SharedConstants.getGameVersion().dataVersion().id(),
                EMPTY_IDENTIFIER_NORMALIZE
        );

        builder.addFixer(new CopperItemsFix(schema, "Genesis copper items rename"));
    }
}
