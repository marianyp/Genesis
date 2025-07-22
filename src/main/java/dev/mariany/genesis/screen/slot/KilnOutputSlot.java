package dev.mariany.genesis.screen.slot;

import dev.mariany.genesis.advancement.criterion.GenesisCriteria;
import dev.mariany.genesis.block.entity.custom.KilnBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;

public class KilnOutputSlot extends Slot {
    private final PlayerEntity player;
    private int amount;

    public KilnOutputSlot(PlayerEntity player, Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
        this.player = player;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return false;
    }

    @Override
    public ItemStack takeStack(int amount) {
        if (this.hasStack()) {
            this.amount = this.amount + Math.min(amount, this.getStack().getCount());
        }

        return super.takeStack(amount);
    }

    @Override
    public void onTakeItem(PlayerEntity player, ItemStack stack) {
        this.onCrafted(stack);
        super.onTakeItem(player, stack);
    }

    @Override
    protected void onCrafted(ItemStack stack, int amount) {
        this.amount += amount;
        this.onCrafted(stack);
    }

    @Override
    protected void onCrafted(ItemStack stack) {
        stack.onCraftByPlayer(this.player, this.amount);

        if (
                this.player instanceof ServerPlayerEntity serverPlayer &&
                        this.inventory instanceof KilnBlockEntity kilnBlockEntity
        ) {
            kilnBlockEntity.dropExperienceForRecipesUsed(serverPlayer);
            GenesisCriteria.COOK_WITH_KILN.trigger(serverPlayer, stack);
        }

        this.amount = 0;
    }
}
