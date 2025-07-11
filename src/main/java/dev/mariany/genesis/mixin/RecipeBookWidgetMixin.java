package dev.mariany.genesis.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.mariany.genesis.client.age.ClientAgeManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeDisplayEntry;
import net.minecraft.recipe.display.SlotDisplayContexts;
import net.minecraft.util.context.ContextParameterMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Mixin(RecipeBookWidget.class)
public class RecipeBookWidgetMixin {
    @Shadow
    protected MinecraftClient client;

    @Inject(
            method = "refreshResults",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/recipebook/RecipeBookResults;setResults(Ljava/util/List;ZZ)V"
            )
    )
    private void filterLockedRecipes(
            boolean resetCurrentPage,
            boolean filteringCraftable,
            CallbackInfo ci,
            @Local(ordinal = 1) List<RecipeResultCollection> filteredResults
    ) {
        ContextParameterMap contextParameterMap = SlotDisplayContexts.createParameters(
                Objects.requireNonNull(this.client.world)
        );

        Iterator<RecipeResultCollection> iterator = filteredResults.iterator();

        while (iterator.hasNext()) {
            RecipeResultCollection resultCollection = iterator.next();

            for (RecipeDisplayEntry recipe : resultCollection.getAllRecipes()) {
                List<ItemStack> stacks = recipe.getStacks(contextParameterMap);
                boolean shouldRemove = stacks.stream().anyMatch(stack ->
                        !ClientAgeManager.getInstance().isUnlocked(stack)
                );

                if (shouldRemove) {
                    iterator.remove();
                    break;
                }
            }
        }
    }
}
