package dev.mariany.genesis.block.custom;

import com.mojang.serialization.MapCodec;
import dev.mariany.genesis.block.entity.GenesisBlockEntities;
import dev.mariany.genesis.block.entity.custom.KilnBlockEntity;
import dev.mariany.genesis.stat.GenesisStats;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class KilnBlock extends BaseEntityBlock {
    public static final MapCodec<KilnBlock> CODEC = simpleCodec(KilnBlock::new);

    public KilnBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new KilnBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> type
    ) {
        return validateTicker(level, type);
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hit
    ) {
        if (!level.isClientSide()) {
            this.openScreen(level, pos, player);
        }

        return InteractionResult.SUCCESS;
    }

    protected void openScreen(Level level, BlockPos pos, Player player) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof KilnBlockEntity) {
            player.openMenu((MenuProvider) blockEntity);
            player.awardStat(GenesisStats.INTERACT_WITH_KILN);
        }
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean moved) {
        Containers.updateNeighboursAfterDestroy(state, level, pos);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
    }

    @Nullable
    protected static <T extends BlockEntity> BlockEntityTicker<T> validateTicker(
            Level level,
            BlockEntityType<T> givenType
    ) {
        return level instanceof ServerLevel
                ? createTickerHelper(givenType, GenesisBlockEntities.KILN, KilnBlock::tick)
                : null;
    }

    private static void tick(Level level, BlockPos pos, BlockState state, KilnBlockEntity kilnBlockEntity) {
        if (level instanceof ServerLevel serverLevel) {
            KilnBlockEntity.tick(serverLevel, pos, state, kilnBlockEntity);
        }
    }
}
