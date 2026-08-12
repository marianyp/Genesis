package dev.mariany.genesis.event.item;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public final class BrushEvents {
    public static final Event<ModifyParticleState> MODIFY_PARTICLE_STATE = EventFactory.createArrayBacked(
            ModifyParticleState.class,
            callbacks -> (hitResult, state) -> {
                BlockState modifiedState = state;

                for (ModifyParticleState callback : callbacks) {
                    modifiedState = callback.modify(hitResult, modifiedState);
                }

                return modifiedState;
            }
    );

    public static final Event<UseTick> USE_TICK = EventFactory.createArrayBacked(
            UseTick.class,
            callbacks -> (level, entity, stack, hitResult) -> {
                for (UseTick callback : callbacks) {
                    callback.onUseTick(level, entity, stack, hitResult);
                }
            }
    );

    private BrushEvents() {
    }

    @FunctionalInterface
    public interface ModifyParticleState {
        BlockState modify(BlockHitResult hitResult, BlockState state);
    }

    @FunctionalInterface
    public interface UseTick {
        void onUseTick(Level level, Player player, ItemStack stack, HitResult hitResult);
    }
}
