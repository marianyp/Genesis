package dev.mariany.genesis.mixin.accessor;

import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.client.network.ClientAdvancementManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ClientAdvancementManager.class)
public interface ClientAdvancementManagerAccessor {
    @Accessor("advancementProgresses")
    Map<AdvancementEntry, AdvancementProgress> genesis$advancementProgresses();
}
