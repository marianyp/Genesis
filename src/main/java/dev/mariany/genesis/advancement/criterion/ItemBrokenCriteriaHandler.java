package dev.mariany.genesis.advancement.criterion;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.event.item.ItemStackEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public final class ItemBrokenCriteriaHandler {
    private ItemBrokenCriteriaHandler() {
    }

    public static void bootstrap() {
        Genesis.bootstrapLog("Item Broken Criteria Handler");
        ItemStackEvents.STACK_DAMAGED.register(ItemBrokenCriteriaHandler::onStackDamaged);
    }

    private static void onStackDamaged(ItemStack stack, @Nullable ServerPlayer player) {
        if (player != null && stack.isBroken()) {
            GenesisCriteria.ITEM_BROKEN.trigger(player, stack);
        }
    }
}
