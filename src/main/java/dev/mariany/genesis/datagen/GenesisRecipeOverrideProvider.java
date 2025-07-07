package dev.mariany.genesis.datagen;

import dev.mariany.genesis.item.GenesisItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class GenesisRecipeOverrideProvider extends FabricRecipeProvider {
    public GenesisRecipeOverrideProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    protected Identifier getRecipeIdentifier(Identifier identifier) {
        return Identifier.ofVanilla(identifier.getPath());
    }

    @Override
    protected RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter) {
        return new RecipeGenerator(registryLookup, exporter) {
            @Override
            public void generate() {
                this.registerSpecialCastRecipes();
            }

            private void registerSpecialCastRecipes() {
                this.createShaped(RecipeCategory.COMBAT, Items.SHIELD)
                        .pattern("III")
                        .pattern("PCP")
                        .pattern("III")
                        .input('I', Items.IRON_INGOT)
                        .input('P', ItemTags.WOODEN_TOOL_MATERIALS)
                        .input('C', GenesisItems.SHIELD_CAST)
                        .criterion(hasItem(GenesisItems.SHIELD_CAST), conditionsFromItem(GenesisItems.SHIELD_CAST))
                        .offerTo(this.exporter);

                this.createShaped(RecipeCategory.DECORATIONS, Items.ANVIL)
                        .pattern("BBB")
                        .pattern("ICI")
                        .pattern("BBB")
                        .input('B', Items.IRON_BLOCK)
                        .input('I', Items.IRON_INGOT)
                        .input('C', GenesisItems.ANVIL_CAST)
                        .criterion(hasItem(GenesisItems.ANVIL_CAST), conditionsFromItem(GenesisItems.ANVIL_CAST))
                        .offerTo(this.exporter);
            }
        };
    }

    @Override
    public String getName() {
        return "Genesis Recipe Overrides";
    }
}
