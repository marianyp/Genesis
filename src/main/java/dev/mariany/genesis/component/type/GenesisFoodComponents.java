package dev.mariany.genesis.component.type;

import net.minecraft.component.type.FoodComponent;

public class GenesisFoodComponents {
    public static final FoodComponent HEALTHY_STEW = createStew(2).alwaysEdible().build();

    private static FoodComponent.Builder createStew(int nutrition) {
        return new FoodComponent.Builder().nutrition(nutrition).saturationModifier(0.6F);
    }
}
