package dev.mariany.genesis.age;

import net.minecraft.util.StringIdentifiable;

public enum AgeCategory implements StringIdentifiable {
    ARMOR("armor"),
    BLOCKS("blocks"),
    TOOLS("tools");

    private final String name;

    AgeCategory(final String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }
}
