package dev.mariany.genesis.block.custom.cauldron;

import dev.mariany.genesis.block.entity.custom.FilledPrimitiveCauldronBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BrushableBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FilledPrimitiveCauldronBlock extends BrushableBlock {
    private final Block particleBlock;
    private final RegistryKey<LootTable> lootTable;

    public FilledPrimitiveCauldronBlock(Block baseBlock, Block particleBlock, SoundEvent brushingSound, SoundEvent brushingCompleteSound, RegistryKey<LootTable> lootTable, Settings settings) {
        super(baseBlock, brushingSound, brushingCompleteSound, settings);
        this.particleBlock = particleBlock;
        this.lootTable = lootTable;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        ItemStack stack = player.getMainHandStack();

        if (stack.isEmpty() && hit.getSide() == Direction.UP && world instanceof ServerWorld serverWorld) {
            if (serverWorld.getBlockEntity(pos) instanceof FilledPrimitiveCauldronBlockEntity filledPrimitiveCauldronBlockEntity) {
                filledPrimitiveCauldronBlockEntity.brush(serverWorld, player, stack, true);

                return ActionResult.SUCCESS_SERVER;
            }
        }

        return ActionResult.PASS;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return PrimitiveCauldronBlock.SHAPE;
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
