package dev.mariany.genesis.block.custom;

import com.mojang.serialization.MapCodec;
import dev.mariany.genesis.screen.AssemblyScreenHandler;
import dev.mariany.genesis.stat.GenesisStats;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AssemblyTableBlock extends CraftingTableBlock {
    public static final MapCodec<AssemblyTableBlock> CODEC = createCodec(AssemblyTableBlock::new);
    private static final Text SCREEN_TITLE = Text.translatable("container.genesis.assembly_table.title");

    @Override
    public MapCodec<AssemblyTableBlock> getCodec() {
        return CODEC;
    }

    public AssemblyTableBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    @Override
    protected NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        return new SimpleNamedScreenHandlerFactory(
                (syncId, inventory, player) -> new AssemblyScreenHandler(
                        syncId, inventory, ScreenHandlerContext.create(world, pos)
                ),
                SCREEN_TITLE
        );
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
            player.incrementStat(GenesisStats.INTERACT_WITH_ASSEMBLY_TABLE);
        }

        return ActionResult.SUCCESS;
    }
}