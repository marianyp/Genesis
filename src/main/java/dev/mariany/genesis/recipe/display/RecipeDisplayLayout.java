package dev.mariany.genesis.recipe.display;

import org.jspecify.annotations.Nullable;

import java.util.*;

public record RecipeDisplayLayout(
        List<Slot> slots,
        @Nullable Position arrowPosition,
        int craftingGridWidth,
        int craftingGridHeight
) {
    public RecipeDisplayLayout {
        slots = List.copyOf(slots);
    }

    public static Builder builder(int craftingGridWidth, int craftingGridHeight) {
        return new Builder(craftingGridWidth, craftingGridHeight);
    }

    public <T> List<PopulatedSlot<T>> populate(int recipeWidth, RecipeDisplayContents<T> contents) {
        if (recipeWidth <= 0) {
            throw new IllegalArgumentException("Recipe width must be positive");
        }

        int recipeHeight = Math.max(1, Math.ceilDiv(contents.craftingInputs().size(), recipeWidth));

        int xOffset = centeredOffset(this.craftingGridWidth, recipeWidth);
        int yOffset = centeredOffset(this.craftingGridHeight, recipeHeight);

        Map<Integer, T> craftingContents = new HashMap<>();

        for (int ingredientIndex = 0; ingredientIndex < contents.craftingInputs().size(); ingredientIndex++) {
            craftingContents.put(
                    craftingSlotIndex(recipeWidth, ingredientIndex, xOffset, yOffset),
                    contents.craftingInputs().get(ingredientIndex)
            );
        }

        List<PopulatedSlot<T>> populatedSlots = new ArrayList<>(this.slots.size());

        for (Slot slot : this.slots) {
            T content = switch (slot.type()) {
                case ADDITIONAL_INPUT -> get(contents.additionalInputs(), slot.index());
                case CRAFTING_INPUT -> craftingContents.get(slot.index());
                case OUTPUT -> get(contents.outputs(), slot.index());
            };

            populatedSlots.add(new PopulatedSlot<>(slot, Optional.ofNullable(content)));
        }

        return List.copyOf(populatedSlots);
    }

    private static <T> T get(List<T> contents, int index) {
        return index < contents.size() ? contents.get(index) : null;
    }

    private int craftingSlotIndex(int recipeWidth, int ingredientIndex, int xOffset, int yOffset) {
        int x = ingredientIndex % recipeWidth + xOffset;
        int y = ingredientIndex / recipeWidth + yOffset;

        return this.craftingGridWidth * y + x;
    }

    private static int centeredOffset(int gridSize, int recipeSize) {
        if (recipeSize <= 0 || recipeSize > gridSize) {
            throw new IllegalArgumentException("Recipe dimensions must fit inside the crafting grid");
        }

        return (gridSize - recipeSize) / 2;
    }

    public enum SlotType {
        ADDITIONAL_INPUT,
        CRAFTING_INPUT,
        OUTPUT
    }

    public enum SlotBackground {
        STANDARD,
        OUTPUT
    }

    public record Position(int x, int y) {
    }

    public record Slot(
            SlotType type,
            int index,
            Position position,
            SlotBackground background
    ) {
        public boolean isInput() {
            return this.type != SlotType.OUTPUT;
        }

        public boolean isCraftingInput() {
            return this.type == SlotType.CRAFTING_INPUT;
        }
    }

    public record PopulatedSlot<T>(Slot slot, Optional<T> content) {
        public boolean isCraftingInput() {
            return this.slot.isCraftingInput();
        }
    }

    public static final class Builder {
        private final int craftingGridWidth;
        private final int craftingGridHeight;
        private final List<Slot> slots = new ArrayList<>();
        private Position arrowPosition;

        private Builder(int craftingGridWidth, int craftingGridHeight) {
            if (craftingGridWidth <= 0 || craftingGridHeight <= 0) {
                throw new IllegalArgumentException("Crafting grid dimensions must be positive");
            }

            this.craftingGridWidth = craftingGridWidth;
            this.craftingGridHeight = craftingGridHeight;
        }

        public Builder additionalInput(int index, int x, int y) {
            return slot(SlotType.ADDITIONAL_INPUT, index, x, y, SlotBackground.STANDARD);
        }

        public Builder craftingGrid(int x, int y, int slotSpacing) {
            for (int gridY = 0; gridY < this.craftingGridHeight; gridY++) {
                for (int gridX = 0; gridX < this.craftingGridWidth; gridX++) {
                    slot(
                            SlotType.CRAFTING_INPUT,
                            gridY * this.craftingGridWidth + gridX,
                            x + gridX * slotSpacing,
                            y + gridY * slotSpacing,
                            SlotBackground.STANDARD
                    );
                }
            }

            return this;
        }

        public Builder output(int index, int x, int y) {
            return slot(SlotType.OUTPUT, index, x, y, SlotBackground.OUTPUT);
        }

        public Builder arrow(int x, int y) {
            this.arrowPosition = new Position(x, y);
            return this;
        }

        public Builder slot(SlotType type, int index, int x, int y, SlotBackground background) {
            this.slots.add(new Slot(type, index, new Position(x, y), background));
            return this;
        }

        public RecipeDisplayLayout build() {
            return new RecipeDisplayLayout(
                    this.slots,
                    this.arrowPosition,
                    this.craftingGridWidth,
                    this.craftingGridHeight
            );
        }
    }
}
