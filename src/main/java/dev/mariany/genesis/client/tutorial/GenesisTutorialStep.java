package dev.mariany.genesis.client.tutorial;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.tutorial.*;

import java.util.function.Function;

@Environment(EnvType.CLIENT)
public enum GenesisTutorialStep {
    NONE("none", FindClayTutorialStepHandler::new);

    private final String name;
    private final Function<GenesisTutorialManager, ? extends TutorialStepHandler> handlerFactory;

    <T extends TutorialStepHandler> GenesisTutorialStep(
            final String name,
            final Function<GenesisTutorialManager, T> factory
    ) {
        this.name = name;
        this.handlerFactory = factory;
    }

    public TutorialStepHandler createHandler(GenesisTutorialManager manager) {
        return this.handlerFactory.apply(manager);
    }

    public String getName() {
        return this.name;
    }

    public static GenesisTutorialStep byName(String name) {
        for (GenesisTutorialStep tutorialStep : values()) {
            if (tutorialStep.name.equals(name)) {
                return tutorialStep;
            }
        }

        return NONE;
    }
}
