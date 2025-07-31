package dev.mariany.genesis.screen.slot;

import dev.mariany.genesis.screen.AssemblyScreenHandler;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class AssemblyInputSlot extends Slot {
    private final AssemblyScreenHandler assemblyScreenHandler;

    public AssemblyInputSlot(
            AssemblyScreenHandler assemblyScreenHandler,
            Inventory inventory,
            int index,
            int x,
            int y
    ) {
        super(inventory, index, x, y);
        this.assemblyScreenHandler = assemblyScreenHandler;
    }

    public boolean canInsert() {
        return !this.assemblyScreenHandler.isInputSlotDisabled(this.id);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return this.canInsert() && super.canInsert(stack);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        this.assemblyScreenHandler.onContentChanged(this.inventory);
    }

    @Override
    public boolean canBeHighlighted() {
        return this.canInsert() && super.canBeHighlighted();
    }
}
