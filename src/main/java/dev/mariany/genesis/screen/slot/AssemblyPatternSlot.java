package dev.mariany.genesis.screen.slot;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.item.custom.AssemblyPatternItem;
import dev.mariany.genesis.screen.AssemblyScreenHandler;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class AssemblyPatternSlot extends Slot {
    private final AssemblyScreenHandler assemblyScreenHandler;

    public AssemblyPatternSlot(
            AssemblyScreenHandler assemblyScreenHandler,
            Container inventory,
            int index,
            int x,
            int y
    ) {
        super(inventory, index, x, y);
        this.assemblyScreenHandler = assemblyScreenHandler;
    }

    public boolean isEmpty() {
        return this.container.isEmpty();
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return super.mayPlace(stack) && stack.getItem() instanceof AssemblyPatternItem;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        this.assemblyScreenHandler.slotsChanged(this.container);
    }

    @Override
    public Identifier getNoItemIcon() {
        return Genesis.id("container/assembly/cast");
    }
}
