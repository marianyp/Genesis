package dev.mariany.genesis.block.custom.cauldron;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PrimitiveCauldronBlock extends Block {
    public static final VoxelShape SHAPE = Shapes.or(
            Shapes.box(0, 0, 0, 0.125, 0.8125, 1),
            Shapes.box(0.125, 0, 0.125, 0.875, 0.0625, 0.875),
            Shapes.box(0.875, 0, 0, 1, 0.8125, 1),
            Shapes.box(0.125, 0, 0, 0.875, 0.8125, 0.125),
            Shapes.box(0.125, 0, 0.875, 0.875, 0.8125, 1)
    );

    public static final VoxelShape INTERACTION_SHAPE = Block.column(12, 1, 13);

    @Nullable
    protected final PrimitiveCauldronBehavior.PrimitiveCauldronBehaviorMap behaviorMap;

    public PrimitiveCauldronBlock(Properties settings) {
        this(null, settings);
    }

    public PrimitiveCauldronBlock(
            @Nullable PrimitiveCauldronBehavior.PrimitiveCauldronBehaviorMap behaviorMap,
            Properties settings
    ) {
        super(settings);
        this.behaviorMap = behaviorMap;
    }

    @Override
    protected VoxelShape getShape(
            BlockState state,
            BlockGetter world,
            BlockPos pos,
            CollisionContext context
    ) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getInteractionShape(
            BlockState state,
            BlockGetter world,
            BlockPos pos
    ) {
        return INTERACTION_SHAPE;
    }

    @Override
    protected InteractionResult useItemOn(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        Optional<InteractionResult> result = attemptInteract(state, level, pos, player, hand);
        return result.orElseGet(() -> super.useItemOn(stack, state, level, pos, player, hand, hit));
    }

    public Optional<InteractionResult> attemptInteract(
            BlockState state,
            Level level,
            BlockPos pos,
            LivingEntity livingEntity,
            InteractionHand hand
    ) {
        ItemStack stack = livingEntity.getItemInHand(hand);

        if (this.behaviorMap == null) {
            return Optional.empty();
        }

        for (PrimitiveCauldronBehavior.PrimitiveCauldronBehaviorEntry entry : behaviorMap.entries()) {
            if (entry.ingredient().test(stack)) {
                return Optional.ofNullable(entry.behavior().interact(state, level, pos, livingEntity, stack));
            }
        }

        return Optional.empty();
    }
}
