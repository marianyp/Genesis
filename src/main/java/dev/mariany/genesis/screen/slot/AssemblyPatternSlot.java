package dev.mariany.genesis.screen.slot;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.item.custom.AssemblyPatternItem;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

public class AssemblyPatternSlot extends Slot {
    public AssemblyPatternSlot(
            Inventory inventory,
            int index,
            int x,
            int y
    ) {
        super(inventory, index, x, y);
    }

    public boolean isEmpty() {
        return this.inventory.isEmpty();
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return super.canInsert(stack) && stack.getItem() instanceof AssemblyPatternItem;
    }

    @Override
    public Identifier getBackgroundSprite() {
        return Genesis.id("container/assembly/cast");
    }
}
