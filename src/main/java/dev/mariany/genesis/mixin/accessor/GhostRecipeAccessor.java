package dev.mariany.genesis.mixin.accessor;

import net.minecraft.client.gui.screen.recipebook.GhostRecipe;
import net.minecraft.recipe.display.SlotDisplay;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.context.ContextParameterMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GhostRecipe.class)
public interface GhostRecipeAccessor {
    @Invoker("addItems")
    void genesis$addItems(Slot slot, ContextParameterMap context, SlotDisplay display, boolean resultSlot);

    @Invoker("addInputs")
    void genesis$addInputs(Slot slot, ContextParameterMap context, SlotDisplay display);

    @Invoker("addResults")
    void genesis$addResults(Slot slot, ContextParameterMap context, SlotDisplay display);
}
