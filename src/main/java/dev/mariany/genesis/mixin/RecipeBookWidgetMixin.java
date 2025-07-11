package dev.mariany.genesis.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mariany.genesis.client.age.ClientAgeManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.recipebook.RecipeBookResults;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeDisplayEntry;
import net.minecraft.recipe.display.SlotDisplayContexts;
import net.minecraft.util.context.ContextParameterMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Objects;

@Mixin(RecipeBookWidget.class)
public class RecipeBookWidgetMixin {
    @Shadow
    protected MinecraftClient client;

    @WrapOperation(
            method = "refreshResults",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/recipebook/RecipeBookResults;setResults(Ljava/util/List;ZZ)V"
            )
    )
    private void filterLockedRecipes(
            RecipeBookResults recipeBookResults, List<RecipeResultCollection> resultCollections, boolean resetCurrentPage, boolean filteringCraftable, Operation<Void> original
    ) {
        ClientAgeManager clientAgeManager = ClientAgeManager.getInstance();
        ContextParameterMap contextParameterMap = SlotDisplayContexts.createParameters(
                Objects.requireNonNull(this.client.world)
        );

        List<RecipeResultCollection> filteredRecipes = resultCollections.stream().filter(resultCollection -> {
            for (RecipeDisplayEntry recipe : resultCollection.getAllRecipes()) {
                List<ItemStack> stacks = recipe.getStacks(contextParameterMap);
                if (stacks.stream().filter(clientAgeManager::isUnlocked).findAny().isEmpty()) {
                    return false;
                }
            }
            return true;
        }).toList();

        original.call(recipeBookResults, filteredRecipes, resetCurrentPage, filteringCraftable);
    }
}
