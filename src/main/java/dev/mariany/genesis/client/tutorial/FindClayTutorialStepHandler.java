package dev.mariany.genesis.client.tutorial;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.tutorial.TutorialStepHandler;

@Environment(EnvType.CLIENT)
public class FindClayTutorialStepHandler implements TutorialStepHandler {
    private final GenesisTutorialManager manager;

    public FindClayTutorialStepHandler(GenesisTutorialManager manager) {
        this.manager = manager;
    }
}
