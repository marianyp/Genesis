package dev.mariany.genesis.block.custom.cauldron;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PrimitiveCauldronBlock extends Block {
    public static final VoxelShape SHAPE = VoxelShapes.union(
            VoxelShapes.cuboid(0, 0, 0, 0.125, 0.8125, 1),
            VoxelShapes.cuboid(0.125, 0, 0.125, 0.875, 0.0625, 0.875),
            VoxelShapes.cuboid(0.875, 0, 0, 1, 0.8125, 1),
            VoxelShapes.cuboid(0.125, 0, 0, 0.875, 0.8125, 0.125),
            VoxelShapes.cuboid(0.125, 0, 0.875, 0.875, 0.8125, 1)
    );

    public static final VoxelShape RAYCAST_SHAPE = Block.createColumnShape(12.0, 1.0, 13.0);

    @Nullable
    protected final PrimitiveCauldronBehavior.PrimitiveCauldronBehaviorMap behaviorMap;

    public PrimitiveCauldronBlock(Settings settings) {
        this(null, settings);
    }

    public PrimitiveCauldronBlock(@Nullable PrimitiveCauldronBehavior.PrimitiveCauldronBehaviorMap behaviorMap, Settings settings) {
        super(settings);
        this.behaviorMap = behaviorMap;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        return RAYCAST_SHAPE;
    }

    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (this.behaviorMap != null) {
            PrimitiveCauldronBehavior cauldronBehavior = this.behaviorMap.map().get(stack.getItem());
            return cauldronBehavior.interact(state, world, pos, player, hand, stack);
        }

        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }
}
