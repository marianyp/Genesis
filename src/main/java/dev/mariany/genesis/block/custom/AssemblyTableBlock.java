package dev.mariany.genesis.block.custom;

import com.mojang.serialization.MapCodec;
import dev.mariany.genesis.screen.AssemblyScreenHandler;
import dev.mariany.genesis.stat.GenesisStats;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CraftingTableBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class AssemblyTableBlock extends CraftingTableBlock {
    public static final MapCodec<AssemblyTableBlock> CODEC = simpleCodec(AssemblyTableBlock::new);
    private static final Component SCREEN_TITLE = Component.translatable("container.genesis.assembly_table.title");

    @Override
    public MapCodec<AssemblyTableBlock> codec() {
        return CODEC;
    }

    public AssemblyTableBlock(BlockBehaviour.Properties settings) {
        super(settings);
    }

    @Override
    protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return new SimpleMenuProvider(
                (syncId, inventory, player) -> new AssemblyScreenHandler(
                        syncId, inventory, ContainerLevelAccess.create(level, pos)
                ),
                SCREEN_TITLE
        );
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide()) {
            player.openMenu(state.getMenuProvider(level, pos));
            player.awardStat(GenesisStats.INTERACT_WITH_ASSEMBLY_TABLE);
        }

        return InteractionResult.SUCCESS;
    }
}