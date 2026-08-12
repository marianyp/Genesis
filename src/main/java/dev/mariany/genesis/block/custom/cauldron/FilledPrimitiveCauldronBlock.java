package dev.mariany.genesis.block.custom.cauldron;

import dev.mariany.genesis.block.entity.custom.FilledPrimitiveCauldronBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BrushItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BrushableBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class FilledPrimitiveCauldronBlock extends BrushableBlock {
    private static final VoxelShape[] SHAPES_BY_DUSTED = Block.boxes(
            3,
            FilledPrimitiveCauldronBlock::createShape
    );

    private final Block particleBlock;
    private final ResourceKey<LootTable> lootTable;

    public FilledPrimitiveCauldronBlock(
            Block baseBlock,
            Block particleBlock,
            SoundEvent brushingSound,
            SoundEvent brushingCompleteSound,
            ResourceKey<LootTable> lootTable,
            Properties settings
    ) {
        super(baseBlock, brushingSound, brushingCompleteSound, settings);
        this.particleBlock = particleBlock;
        this.lootTable = lootTable;
    }

    private static VoxelShape createShape(float dusted) {
        return Shapes.or(
                Shapes.box(0, 0, 0, 0.125, 0.8125, 1),
                Shapes.box(0.125, 0, 0.125, 0.875, 0.0625, 0.875),
                Shapes.box(0.875, 0, 0, 1, 0.8125, 1),
                Shapes.box(0.125, 0, 0, 0.875, 0.8125, 0.125),
                Shapes.box(0.125, 0, 0.875, 0.875, 0.8125, 1),
                Shapes.box(
                        0.125,
                        0.0625,
                        0.125,
                        0.875,
                        0.75 - (0.1875 * dusted),
                        0.875
                )
        );
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hit
    ) {
        ItemStack stack = player.getMainHandItem();

        if (!(stack.getItem() instanceof BrushItem) && hit.getDirection() == Direction.UP) {
            if (level instanceof ServerLevel serverLevel) {
                BlockEntity blockEntity = level.getBlockEntity(pos);

                if (blockEntity instanceof FilledPrimitiveCauldronBlockEntity filledPrimitiveCauldronBlockEntity) {
                    filledPrimitiveCauldronBlockEntity.sift(serverLevel, player, stack, true);
                }
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPES_BY_DUSTED[state.getValue(BlockStateProperties.DUSTED)];
    }

    @Override
    protected VoxelShape getInteractionShape(BlockState state, BlockGetter world, BlockPos pos) {
        return PrimitiveCauldronBlock.INTERACTION_SHAPE;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FilledPrimitiveCauldronBlockEntity(pos, state, this.lootTable);
    }

    public Block getParticleBlock() {
        return this.particleBlock;
    }

    public ResourceKey<LootTable> getPrimitiveLootTable() {
        return this.lootTable;
    }
}
