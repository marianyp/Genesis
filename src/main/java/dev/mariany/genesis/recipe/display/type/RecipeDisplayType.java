package dev.mariany.genesis.recipe.display.type;

import com.mojang.serialization.MapCodec;
import dev.mariany.genesis.recipe.display.IdentifiedRecipeDisplay;
import dev.mariany.genesis.recipe.display.RecipeDisplayContents;
import dev.mariany.genesis.recipe.display.RecipeDisplayCategory;
import dev.mariany.genesis.recipe.display.RecipeDisplayTarget;
import dev.mariany.genesis.recipe.display.RecipeDisplayTransfer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public class RecipeDisplayType<D extends IdentifiedRecipeDisplay> {
    private final Identifier id;
    private final Class<D> displayClass;
    private final MapCodec<D> codec;
    private final RecipeDisplayCategory category;
    private final List<RecipeDisplayTarget<?>> targets;
    private final ToIntFunction<D> recipeWidth;
    private final Function<D, RecipeDisplayContents<SlotDisplay>> contents;
    private final Function<Collection<RecipeHolder<?>>, List<D>> provider;
    private final RecipeDisplayTransfer<D, ?> transfer;
    private final Priority priority;

    private RecipeDisplayType(
            Identifier id,
            Class<D> displayClass,
            MapCodec<D> codec,
            RecipeDisplayCategory category,
            List<RecipeDisplayTarget<?>> targets,
            ToIntFunction<D> recipeWidth,
            Function<D, RecipeDisplayContents<SlotDisplay>> contents,
            Function<Collection<RecipeHolder<?>>, List<D>> provider,
            RecipeDisplayTransfer<D, ?> transfer,
            Priority priority
    ) {
        this.id = id;
        this.displayClass = displayClass;
        this.codec = codec;
        this.category = category;
        this.targets = targets;
        this.recipeWidth = recipeWidth;
        this.contents = contents;
        this.provider = provider;
        this.transfer = transfer;
        this.priority = priority;
    }

    public static <D extends IdentifiedRecipeDisplay> Builder<D> builder(Identifier id, Class<D> displayClass) {
        return new Builder<>(id, displayClass);
    }

    public Identifier id() {
        return this.id;
    }

    public Class<D> displayClass() {
        return this.displayClass;
    }

    public MapCodec<D> codec() {
        return this.codec;
    }

    public RecipeDisplayCategory category() {
        return this.category;
    }

    public List<RecipeDisplayTarget<?>> targets() {
        return this.targets;
    }

    public int recipeWidth(D display) {
        return this.recipeWidth.applyAsInt(display);
    }

    public RecipeDisplayContents<SlotDisplay> contents(D display) {
        return this.contents.apply(display);
    }

    public List<D> provide(Collection<RecipeHolder<?>> recipes) {
        return List.copyOf(this.provider.apply(recipes));
    }

    public Optional<RecipeDisplayTransfer<D, ?>> transfer() {
        return Optional.ofNullable(this.transfer);
    }

    public Priority priority() {
        return this.priority;
    }

    public record Priority(@Nullable RecipeType<? extends Recipe<?>> anchor, Placement placement) {
        private static final Priority NONE = new Priority(null, Placement.NONE);
        private static final Priority FIRST = new Priority(null, Placement.FIRST);

        public Priority {
            Objects.requireNonNull(placement, "placement");

            if ((placement == Placement.BEFORE || placement == Placement.AFTER) && anchor == null) {
                throw new IllegalArgumentException("A relative recipe display priority requires an anchor");
            }

            if ((placement == Placement.NONE || placement == Placement.FIRST) && anchor != null) {
                throw new IllegalArgumentException("A non-relative recipe display priority cannot have an anchor");
            }
        }

        private static Priority before(RecipeType<? extends Recipe<?>> anchor) {
            return new Priority(Objects.requireNonNull(anchor, "anchor"), Placement.BEFORE);
        }

        private static Priority after(RecipeType<? extends Recipe<?>> anchor) {
            return new Priority(Objects.requireNonNull(anchor, "anchor"), Placement.AFTER);
        }

        public boolean isUnprioritized() {
            return this.placement == Placement.NONE;
        }

        public enum Placement {
            NONE,
            FIRST,
            BEFORE,
            AFTER
        }
    }

    public static final class Builder<D extends IdentifiedRecipeDisplay> {
        private final Identifier id;
        private final Class<D> displayClass;
        private final List<RecipeDisplayTarget<?>> targets = new ArrayList<>();

        private MapCodec<D> codec;
        private RecipeDisplayCategory category;
        private ToIntFunction<D> recipeWidth;
        private Function<D, RecipeDisplayContents<SlotDisplay>> contents;
        private Function<Collection<RecipeHolder<?>>, List<D>> provider;
        private RecipeDisplayTransfer<D, ?> transfer;
        private Priority priority = Priority.NONE;

        private Builder(Identifier id, Class<D> displayClass) {
            this.id = id;
            this.displayClass = displayClass;
        }

        public Builder<D> codec(MapCodec<D> codec) {
            this.codec = codec;
            return this;
        }

        public Builder<D> category(RecipeDisplayCategory category) {
            this.category = category;
            return this;
        }

        public <S> Builder<D> target(
                Supplier<Class<S>> screenClass,
                int x,
                int y,
                int width,
                int height
        ) {
            this.targets.add(new RecipeDisplayTarget<>(screenClass, x, y, width, height));
            return this;
        }

        public Builder<D> recipeWidth(ToIntFunction<D> recipeWidth) {
            this.recipeWidth = recipeWidth;
            return this;
        }

        public Builder<D> contents(Function<D, RecipeDisplayContents<SlotDisplay>> contents) {
            this.contents = contents;
            return this;
        }

        public Builder<D> provider(Function<Collection<RecipeHolder<?>>, List<D>> provider) {
            this.provider = provider;
            return this;
        }

        public <M extends AbstractContainerMenu> Builder<D> transfer(RecipeDisplayTransfer<D, M> transfer) {
            this.transfer = transfer;
            return this;
        }

        public Builder<D> prioritize() {
            this.priority = Priority.FIRST;
            return this;
        }

        public Builder<D> prioritizeBefore(RecipeType<? extends Recipe<?>> recipeType) {
            this.priority = Priority.before(recipeType);
            return this;
        }

        public Builder<D> prioritizeAfter(RecipeType<? extends Recipe<?>> recipeType) {
            this.priority = Priority.after(recipeType);
            return this;
        }

        public RecipeDisplayType<D> build() {
            return new RecipeDisplayType<>(
                    Objects.requireNonNull(this.id, "id"),
                    Objects.requireNonNull(this.displayClass, "displayClass"),
                    Objects.requireNonNull(this.codec, "codec"),
                    Objects.requireNonNull(this.category, "category"),
                    List.copyOf(this.targets),
                    Objects.requireNonNull(this.recipeWidth, "recipeWidth"),
                    Objects.requireNonNull(this.contents, "contents"),
                    Objects.requireNonNull(this.provider, "provider"),
                    this.transfer,
                    this.priority
            );
        }
    }
}
