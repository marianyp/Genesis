package dev.mariany.genesis.mixin.accessor;

import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AdvancementManager.class)
public interface AdvancementManagerAccesor {
    @Invoker("tryAdd")
    boolean genesis$tryAdd(AdvancementEntry advancement);
}
