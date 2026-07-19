package dev.mariany.genesis.mixin.accessor;

import net.minecraft.client.gui.screens.recipebook.GhostSlots;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RecipeBookComponent.class)
public interface RecipeBookWidgetAccessor {
    @Accessor("ghostSlots")
    GhostSlots genesis$ghostRecipe();
}
