package dev.mariany.genesis.advancement.criterion;

import dev.mariany.genesis.Genesis;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class GenesisCriteria {
    public static final ItemBrokenCriterion ITEM_BROKEN = register("item_broken", new ItemBrokenCriterion());
    public static final ObtainAdvancementCriterion OBTAIN_ADVANCEMENT = register("obtain_advancement",
            new ObtainAdvancementCriterion()
    );
    public static final CompleteTrialSpawnerCriteria COMPLETE_TRIAL_SPAWNER_ADVANCEMENT = register(
            "complete_trial_spawner",
            new CompleteTrialSpawnerCriteria()
    );
    public static final TickCriterion FIRE_STARTED = register(
            "fire_started",
            new TickCriterion()
    );
    public static final BrushPrimitiveCauldronCriteria BRUSH_PRIMITIVE_CAULDRON = register(
            "brush_primitive_cauldron",
            new BrushPrimitiveCauldronCriteria()
    );
    public static final CookWithKilnCriteria COOK_WITH_KILN = register(
            "cook_with_kiln",
            new CookWithKilnCriteria()
    );
    public static final OpenAdvancementTabCriteria OPEN_ADVANCEMENT_TAB = register(
            "open_advancement_tab",
            new OpenAdvancementTabCriteria()
    );

    public static <T extends Criterion<?>> T register(String name, T criterion) {
        return Registry.register(Registries.CRITERION, Genesis.id(name), criterion);
    }

    public static void bootstrap() {
        Genesis.LOGGER.info("Registering Criteria for " + Genesis.MOD_ID);
    }
}
