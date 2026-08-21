package dev.mariany.genesis.recipe.display.type;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.block.GenesisBlocks;
import dev.mariany.genesis.block.custom.cauldron.FilledPrimitiveCauldronBlock;
import dev.mariany.genesis.client.gui.screen.ingame.AssemblyScreen;
import dev.mariany.genesis.loot.PrimitiveCauldronLoot;
import dev.mariany.genesis.packet.serverbound.QuickCraftAssemblyPayload;
import dev.mariany.genesis.logic.AssemblyRecipeLogic;
import dev.mariany.genesis.recipe.display.*;
import dev.mariany.genesis.recipe.transfer.AssemblyRecipeTransfer;
import dev.mariany.genesis.screen.AssemblyScreenHandler;
import dev.mariany.genesis.screen.GenesisScreenHandlers;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public final class RecipeDisplayTypes {
    private static final List<RecipeDisplayType<?>> ALL = new ArrayList<>();

    public static final RecipeDisplayType<AssemblyCraftingRecipeDisplay> ASSEMBLY =
            register(
                    "assembly",
                    id -> RecipeDisplayType
                            .builder(id, AssemblyCraftingRecipeDisplay.class)
                            .codec(AssemblyCraftingRecipeDisplay.CODEC)
                            .prioritizeAfter(RecipeType.CRAFTING)
                            .category(
                                    new RecipeDisplayCategory(
                                            Genesis.id("plugins/assembly"),
                                            "category.genesis.assembly",
                                            175,
                                            66,
                                            () -> GenesisBlocks.ASSEMBLY_TABLE,
                                            RecipeDisplayLayout
                                                    .builder(3, 3)
                                                    .additionalInput(0, 21, 25)
                                                    .craftingGrid(43, 7, 18)
                                                    .output(0, 135, 25)
                                                    .arrow(100, 24)
                                                    .build()
                                    )
                            )
                            .target(() -> AssemblyScreen.class, 111, 32, 28, 23)
                            .recipeWidth(AssemblyCraftingRecipeDisplay::width)
                            .contents(AssemblyCraftingRecipeDisplay::contents)
                            .provider(RecipeDisplayTypes::provideAssemblyDisplays)
                            .transfer(
                                    new RecipeDisplayTransfer<>(
                                            AssemblyScreenHandler.class,
                                            () -> GenesisScreenHandlers.ASSEMBLY,
                                            AssemblyRecipeTransfer::findRecipeKey,
                                            AssemblyRecipeTransfer::isMissingRequirements,
                                            QuickCraftAssemblyPayload::new
                                    )
                            )
            );

    public static final RecipeDisplayType<SiftingRecipeDisplay> SIFTING =
            register(
                    "sifting",
                    id -> RecipeDisplayType
                            .builder(id, SiftingRecipeDisplay.class)
                            .codec(SiftingRecipeDisplay.CODEC)
                            .category(
                                    new RecipeDisplayCategory(
                                            Genesis.id("plugins/sifting"),
                                            "category.genesis.sifting",
                                            120,
                                            44,
                                            () -> GenesisBlocks.TERRACOTTA_CAULDRON,
                                            RecipeDisplayLayout
                                                    .builder(1, 1)
                                                    .slot(
                                                            RecipeDisplayLayout.SlotType.CRAFTING_INPUT,
                                                            0,
                                                            19,
                                                            14,
                                                            RecipeDisplayLayout.SlotBackground.STANDARD
                                                    )
                                                    .output(0, 89, 14)
                                                    .arrow(50, 13)
                                                    .build()
                                    )
                            )
                            .recipeWidth(_ -> 1)
                            .contents(SiftingRecipeDisplay::contents)
                            .provider(_ -> provideSiftingDisplays())
            );

    private RecipeDisplayTypes() {
    }

    public static List<RecipeDisplayType<?>> all() {
        return List.copyOf(ALL);
    }

    private static <D extends IdentifiedRecipeDisplay> RecipeDisplayType<D> register(
            String id,
            Function<Identifier, RecipeDisplayType.Builder<D>> builderFactory
    ) {
        RecipeDisplayType<D> type = builderFactory.apply(Genesis.id(id)).build();
        ALL.add(type);
        return type;
    }

    private static List<AssemblyCraftingRecipeDisplay> provideAssemblyDisplays(Collection<RecipeHolder<?>> recipes) {
        return AssemblyRecipeLogic.createAssemblyRecipesFrom(recipes)
                .stream()
                .filter(recipe -> recipe.value().isPossible())
                .map(recipe -> recipe.value().getDisplay())
                .toList();
    }

    private static List<SiftingRecipeDisplay> provideSiftingDisplays() {
        return PrimitiveCauldronLoot
                .streamFilledPrimitiveCauldronBlocks()
                .map(RecipeDisplayTypes::createSiftingDisplay)
                .flatMap(Optional::stream)
                .toList();
    }

    private static Optional<SiftingRecipeDisplay> createSiftingDisplay(
            FilledPrimitiveCauldronBlock filledPrimitiveCauldronBlock
    ) {
        List<SlotDisplay> possibleDrops = Genesis.PRIMITIVE_CAULDRON_LOOT
                .getPossibleDrops(filledPrimitiveCauldronBlock.getPrimitiveLootTable())
                .stream()
                .map(Ingredient::display)
                .toList();

        if (possibleDrops.isEmpty()) {
            return Optional.empty();
        }

        SiftingRecipeDisplay siftingRecipeDisplay = new SiftingRecipeDisplay(
                filledPrimitiveCauldronBlock,
                new SlotDisplay.Composite(possibleDrops)
        );

        return Optional.of(siftingRecipeDisplay);
    }
}
