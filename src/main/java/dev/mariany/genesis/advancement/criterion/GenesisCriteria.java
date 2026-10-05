package dev.mariany.genesis.advancement.criterion;

import dev.mariany.genesis.Genesis;
import net.minecraft.advancements.triggers.CriterionTrigger;
import net.minecraft.advancements.triggers.PlayerTrigger;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class GenesisCriteria {
    public static final PlayerTrigger FIRE_STARTED = register("fire_started", new PlayerTrigger());

    public static final SiftPrimitiveCauldronTrigger SIFT_PRIMITIVE_CAULDRON = register(
            "sift_primitive_cauldron",
            new SiftPrimitiveCauldronTrigger()
    );

    public static final CookWithKilnTrigger COOK_WITH_KILN = register(
            "cook_with_kiln",
            new CookWithKilnTrigger()
    );

    public static final PlayerTrigger COMPLETE_MONUMENT = register("complete_monument", new PlayerTrigger());

    public static <T extends CriterionTrigger<?>> T register(String name, T criterion) {
        return Registry.register(BuiltInRegistries.TRIGGER_TYPES, Genesis.id(name), criterion);
    }

    public static void bootstrap() {
        Genesis.bootstrapLog("Criteria");
    }
}
