package dev.mariany.genesis.block.custom.cauldron;

import dev.mariany.genesis.block.entity.custom.FilledPrimitiveCauldronBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BrushableBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BrushItem;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FilledPrimitiveCauldronBlock extends BrushableBlock {
    private static final VoxelShape[] SHAPES_BY_DUSTED = Block.createShapeArray(3,
            FilledPrimitiveCauldronBlock::createShape
    );

    private final Block particleBlock;
    private final RegistryKey<LootTable> lootTable;

    public FilledPrimitiveCauldronBlock(Block baseBlock, Block particleBlock, SoundEvent brushingSound, SoundEvent brushingCompleteSound, RegistryKey<LootTable> lootTable, Settings settings) {
        super(baseBlock, brushingSound, brushingCompleteSound, settings);
        this.particleBlock = particleBlock;
        this.lootTable = lootTable;
    }

    private static VoxelShape createShape(float dusted) {
        return VoxelShapes.union(
                VoxelShapes.cuboid(0, 0, 0, 0.125, 0.8125, 1),
                VoxelShapes.cuboid(0.125, 0, 0.125, 0.875, 0.0625, 0.875),
                VoxelShapes.cuboid(0.875, 0, 0, 1, 0.8125, 1),
                VoxelShapes.cuboid(0.125, 0, 0, 0.875, 0.8125, 0.125),
                VoxelShapes.cuboid(0.125, 0, 0.875, 0.875, 0.8125, 1),
                VoxelShapes.cuboid(0.125, 0.0625, 0.125, 0.875, 0.75 - (0.1875 * dusted), 0.875)
        );
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        ItemStack stack = player.getMainHandStack();

        if (!(stack.getItem() instanceof BrushItem) && hit.getSide() == Direction.UP) {
            if (world instanceof ServerWorld serverWorld) {
                BlockEntity blockEntity = world.getBlockEntity(pos);

                if (blockEntity instanceof FilledPrimitiveCauldronBlockEntity filledPrimitiveCauldronBlockEntity) {
                    filledPrimitiveCauldronBlockEntity.brush(serverWorld, player, stack, true);
                }
            }

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES_BY_DUSTED[state.get(Properties.DUSTED)];
    }

    @Override
    protected VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        return PrimitiveCauldronBlock.RAYCAST_SHAPE;
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new FilledPrimitiveCauldronBlockEntity(pos, state, this.lootTable);
    }

    public Block getParticleBlock() {
        return this.particleBlock;
    }

    public RegistryKey<LootTable> getLootTable() {
        return this.lootTable;
    }
}
