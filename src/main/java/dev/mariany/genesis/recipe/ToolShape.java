package dev.mariany.genesis.recipe;

import java.util.List;

public enum ToolShape {
    SWORD('X',
            "X",
            "X",
            "#"
    ),
    SHOVEL('X',
            "X",
            "#",
            "#"
    ),
    PICKAXE('X',
            "XXX",
            " # ",
            " # "
    ),
    AXE('X',
            "XX",
            "X#",
            " #"
    ),
    HOE('X',
            "XX",
            " #",
            " #"
    );

    public final Character materialKey;
    public final List<String> rows;

    ToolShape(Character materialKey, String... patternRows) {
        this.materialKey = materialKey;
        this.rows = List.of(patternRows);
    }
}

