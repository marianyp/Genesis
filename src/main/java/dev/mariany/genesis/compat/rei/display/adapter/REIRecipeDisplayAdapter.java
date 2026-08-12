package dev.mariany.genesis.compat.rei.display.adapter;

import dev.mariany.genesis.compat.rei.client.REIRecipeTransferHandler;
import dev.mariany.genesis.recipe.display.RecipeDisplayContents;
import dev.mariany.genesis.recipe.display.RecipeDisplayTransfer;
import dev.mariany.genesis.recipe.display.type.RecipeDisplayType;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandlerRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.registry.display.ServerDisplayRegistry;
import me.shedaniel.rei.plugin.common.displays.crafting.CraftingDisplay;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ToIntFunction;

public class REIRecipeDisplayAdapter<D extends CraftingDisplay> {
    private final RecipeDisplayType<?> displayType;
    private final CategoryIdentifier<D> categoryIdentifier;
    private final DisplaySerializer<D> serializer;
    private final Class<D> displayClass;
    private final ToIntFunction<D> recipeWidth;
    private final Function<D, RecipeDisplayContents<EntryIngredient>> contents;
    private final @Nullable Consumer<ServerDisplayRegistry> displayRegistrar;

    public <R extends Recipe<?>> REIRecipeDisplayAdapter(
            RecipeDisplayType<?> displayType,
            DisplaySerializer<D> serializer,
            Class<D> displayClass,
            ToIntFunction<D> recipeWidth,
            Function<D, RecipeDisplayContents<EntryIngredient>> contents,
            Class<R> recipeClass,
            RecipeType<? super R> recipeType,
            Function<RecipeHolder<R>, D> displayFactory
    ) {
        this(
                displayType,
                serializer,
                displayClass,
                recipeWidth,
                contents,
                registry -> registry
                        .<R, D>beginRecipeFiller(recipeClass)
                        .filterType(recipeType)
                        .fill(displayFactory)
        );
    }

    public REIRecipeDisplayAdapter(
            RecipeDisplayType<?> displayType,
            DisplaySerializer<D> serializer,
            Class<D> displayClass,
            ToIntFunction<D> recipeWidth,
            Function<D, RecipeDisplayContents<EntryIngredient>> contents
    ) {
        this(displayType, serializer, displayClass, recipeWidth, contents, null);
    }

    private REIRecipeDisplayAdapter(
            RecipeDisplayType<?> displayType,
            DisplaySerializer<D> serializer,
            Class<D> displayClass,
            ToIntFunction<D> recipeWidth,
            Function<D, RecipeDisplayContents<EntryIngredient>> contents,
            @Nullable Consumer<ServerDisplayRegistry> displayRegistrar
    ) {
        this.displayType = displayType;
        this.categoryIdentifier = CategoryIdentifier.of(displayType.category().id());
        this.serializer = serializer;
        this.displayClass = displayClass;
        this.recipeWidth = recipeWidth;
        this.contents = contents;
        this.displayRegistrar = displayRegistrar;
    }

    public RecipeDisplayType<?> displayType() {
        return this.displayType;
    }

    public CategoryIdentifier<D> categoryIdentifier() {
        return this.categoryIdentifier;
    }

    public boolean supports(Display display) {
        return this.displayClass.isInstance(display);
    }

    public D cast(Display display) {
        return this.displayClass.cast(display);
    }

    public int recipeWidth(CraftingDisplay display) {
        return this.recipeWidth.applyAsInt(this.displayClass.cast(display));
    }

    public RecipeDisplayContents<EntryIngredient> contents(CraftingDisplay display) {
        return this.contents.apply(this.displayClass.cast(display));
    }

    public void registerSerializer(DisplaySerializerRegistry registry) {
        registry.register(this.displayType.id(), this.serializer);
    }

    public void registerDisplays(ServerDisplayRegistry registry) {
        if (this.displayRegistrar == null) {
            return;
        }

        this.displayRegistrar.accept(registry);
    }

    public void registerTransferHandler(TransferHandlerRegistry registry) {
        this.displayType
                .transfer()
                .ifPresent(transfer -> registerTransferHandler(
                        registry,
                        transfer
                ));
    }

    private <M extends AbstractContainerMenu> void registerTransferHandler(
            TransferHandlerRegistry registry,
            RecipeDisplayTransfer<?, M> transfer
    ) {
        registry.register(new REIRecipeTransferHandler<>(this, transfer));
    }
}
