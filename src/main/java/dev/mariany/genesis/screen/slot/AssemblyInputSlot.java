package dev.mariany.genesis.screen.slot;

import dev.mariany.genesis.screen.AssemblyScreenHandler;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class AssemblyInputSlot extends Slot {
    private final AssemblyScreenHandler assemblyScreenHandler;

    public AssemblyInputSlot(
            AssemblyScreenHandler assemblyScreenHandler,
            Container inventory,
            int index,
            int x,
            int y
    ) {
        super(inventory, index, x, y);
        this.assemblyScreenHandler = assemblyScreenHandler;
    }

    public boolean canInsert() {
        return !this.assemblyScreenHandler.isInputSlotDisabled(this.index);
    }

    @Override
    public boolean isActive() {
        return this.canInsert();
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return this.canInsert() && super.mayPlace(stack);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        this.assemblyScreenHandler.slotsChanged(this.container);
    }

    @Override
    public boolean isHighlightable() {
        return this.canInsert() && super.isHighlightable();
    }
}
