package dev.mariany.genesis.recipe;

import org.jetbrains.annotations.NotNull;

public record CraftingPattern(int patternBits) {
    public static final CraftingPattern ALL = fromGrid(new boolean[]{
            true, true, true,
            true, true, true,
            true, true, true
    });

    public static final CraftingPattern SWORD = fromGrid(new boolean[]{
            false, true, false,
            false, true, false,
            false, true, false
    });

    public static final CraftingPattern SHOVEL = fromGrid(new boolean[]{
            false, true, false,
            false, true, false,
            false, true, false
    });

    public static final CraftingPattern PICKAXE = fromGrid(new boolean[]{
            true, true, true,
            false, true, false,
            false, true, false
    });

    public static final CraftingPattern AXE = fromGrid(new boolean[]{
            true, true, false,
            true, true, false,
            false, true, false
    });

    public static final CraftingPattern HOE = fromGrid(new boolean[]{
            true, true, false,
            false, true, false,
            false, true, false
    });

    public static final CraftingPattern SHIELD = fromGrid(new boolean[]{
            true, true, true,
            true, true, true,
            false, true, false
    });

    public static final CraftingPattern ANVIL = fromGrid(new boolean[]{
            true, true, true,
            false, true, false,
            true, true, true
    });

    public CraftingPattern(int patternBits) {
        this.patternBits = patternBits & 0x1FF;
    }

    public boolean isSlotDisabled(int index) {
        if (index < 0 || index >= 9) {
            return false;
        }

        return ((patternBits >> index) & 1) == 0;
    }

    public static CraftingPattern fromGrid(boolean[] grid) {
        if (grid.length != 9) {
            throw new IllegalArgumentException("Grid must have a length 9");
        }

        int bits = 0;

        for (int i = 0; i < 9; i++) {
            if (grid[i]) {
                bits |= (1 << i);
            }
        }

        return new CraftingPattern(bits);
    }

    @Override
    @NotNull
    public String toString() {
        StringBuilder sb = new StringBuilder(11);

        for (int i = 0; i < 9; i++) {
            sb.append(isSlotDisabled(i) ? "0" : "1");

            if (i % 3 == 2) {
                sb.append('\n');
            }
        }

        return sb.toString();
    }
}

