package dev.mariany.genesis.advancement.criterion;

import dev.mariany.genesis.Genesis;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class GenesisCriteria {
    public static final ItemBrokenCriterion ITEM_BROKEN = register("item_broken", new ItemBrokenCriterion());
    public static final ObtainAdvancementCriterion OBTAIN_ADVANCEMENT = register("obtain_advancement", new ObtainAdvancementCriterion());

    public static <T extends Criterion<?>> T register(String name, T criterion) {
        return Registry.register(Registries.CRITERION, Genesis.id(name), criterion);
    }

    public static void bootstrap() {
        Genesis.LOGGER.info("Registering Criteria for " + Genesis.MOD_ID);
    }
}
