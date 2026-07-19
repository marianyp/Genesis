package dev.mariany.genesis.screen.slot;

import dev.mariany.genesis.advancement.criterion.GenesisCriteria;
import dev.mariany.genesis.block.entity.custom.KilnBlockEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class KilnOutputSlot extends Slot {
    private final Player player;
    private int amount;

    public KilnOutputSlot(Player player, Container inventory, int index, int x, int y) {
        super(inventory, index, x, y);
        this.player = player;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }

    @Override
    public ItemStack remove(int amount) {
        if (this.hasItem()) {
            this.amount = this.amount + Math.min(amount, this.getItem().getCount());
        }

        return super.remove(amount);
    }

    @Override
    public void onTake(Player player, ItemStack stack) {
        this.checkTakeAchievements(stack);
        super.onTake(player, stack);
    }

    @Override
    protected void onQuickCraft(ItemStack stack, int amount) {
        this.amount += amount;
        this.checkTakeAchievements(stack);
    }

    @Override
    protected void checkTakeAchievements(ItemStack stack) {
        stack.onCraftedBy(this.player, this.amount);

        if (
                this.player instanceof ServerPlayer serverPlayer &&
                        this.container instanceof KilnBlockEntity kilnBlockEntity
        ) {
            kilnBlockEntity.dropExperienceForRecipesUsed(serverPlayer);
            GenesisCriteria.COOK_WITH_KILN.trigger(serverPlayer, stack);
        }

        this.amount = 0;
    }
}
