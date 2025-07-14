package dev.mariany.genesis.client.tutorial;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class GenesisTutorialState {
    private static final GenesisTutorialState INSTANCE = new GenesisTutorialState();

    public GenesisTutorialStep step = GenesisTutorialStep.NONE;

    private GenesisTutorialState() {
    }

    public static GenesisTutorialState getInstance() {
        return INSTANCE;
    }
}
