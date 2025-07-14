package dev.mariany.genesis.block.custom;

import com.mojang.serialization.MapCodec;
import dev.mariany.genesis.block.entity.GenesisBlockEntities;
import dev.mariany.genesis.block.entity.custom.KilnBlockEntity;
import dev.mariany.genesis.stat.GenesisStats;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class KilnBlock extends BlockWithEntity {
    public static final MapCodec<KilnBlock> CODEC = createCodec(KilnBlock::new);

    public KilnBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new KilnBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(world, type);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            this.openScreen(world, pos, player);
        }

        return ActionResult.SUCCESS;
    }

    protected void openScreen(World world, BlockPos pos, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof KilnBlockEntity) {
            player.openHandledScreen((NamedScreenHandlerFactory) blockEntity);
            player.incrementStat(GenesisStats.INTERACT_WITH_KILN);
        }
    }

    @Override
    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        ItemScatterer.onStateReplaced(state, world, pos);
    }

    @Override
    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }

    @Nullable
    protected static <T extends BlockEntity> BlockEntityTicker<T> validateTicker(
            World world, BlockEntityType<T> givenType
    ) {
        return world instanceof ServerWorld serverWorld
                ? validateTicker(givenType, (BlockEntityType<? extends KilnBlockEntity>) GenesisBlockEntities.KILN, (worldx, pos, state, blockEntity) -> KilnBlockEntity.tick(serverWorld, pos, state, blockEntity))
                : null;
    }
}
