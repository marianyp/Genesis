package dev.mariany.genesis.block.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public abstract class SealedContainerBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {
    protected SealedContainerBlockEntity(
            BlockEntityType<?> type,
            BlockPos worldPosition,
            BlockState blockState
    ) {
        super(type, worldPosition, blockState);
    }

    @Override
    public final int[] getSlotsForFace(Direction side) {
        return new int[0];
    }

    @Override
    public final boolean canPlaceItemThroughFace(
            int slot,
            ItemStack stack,
            @Nullable Direction direction
    ) {
        return false;
    }

    @Override
    public final boolean canTakeItemThroughFace(
            int slot,
            ItemStack stack,
            Direction direction
    ) {
        return false;
    }
}
