package dev.mariany.genesis.recipe.display;

public record RecipeDisplayViewport(
        int width,
        int height,
        RecipeDisplayLayout.Position layoutOffset
) {
    public static RecipeDisplayViewport standard(RecipeDisplayCategory category) {
        return standard(category, 150);
    }

    public static RecipeDisplayViewport standard(RecipeDisplayCategory category, int maxWidth) {
        int width = Math.min(category.width(), maxWidth);
        int xOffset = Math.floorDiv(width - category.width(), 2);

        return new RecipeDisplayViewport(
                width,
                category.height(),
                new RecipeDisplayLayout.Position(xOffset, 0)
        );
    }

    public RecipeDisplayLayout.Position translate(RecipeDisplayLayout.Position position) {
        return new RecipeDisplayLayout.Position(
                this.layoutOffset.x() + position.x(),
                this.layoutOffset.y() + position.y()
        );
    }
}
