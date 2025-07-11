package dev.mariany.genesis.event.block;

import dev.mariany.genesis.age.AgeEntry;
import dev.mariany.genesis.age.AgeManager;
import dev.mariany.genesis.packet.clientbound.NotifyAgeLockedPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

public class UseBlockHandler {
    public static ActionResult onUseBlock(PlayerEntity player, World world, Hand hand, BlockHitResult result) {
        if (player instanceof ServerPlayerEntity serverPlayerEntity) {
            AgeManager ageManager = AgeManager.getInstance();
            BlockState state = world.getBlockState(result.getBlockPos());

            if (!ageManager.isUnlocked(serverPlayerEntity, state)) {
                String itemTranslation = state.getBlock().getName().getString();
                List<AgeEntry> incompleteAges = ageManager.getAges()
                        .stream()
                        .filter(ageEntry -> !ageEntry.isDone(serverPlayerEntity))
                        .toList();

                Optional<AgeEntry> optionalAgeEntry = incompleteAges
                        .stream()
                        .filter(ageEntry -> ageEntry.getAge().unlocks()
                                .stream()
                                .anyMatch(ingredient -> ingredient.test(
                                        state.getBlock().asItem().getDefaultStack())
                                ))
                        .findAny();

                optionalAgeEntry.ifPresent(ageEntry -> ServerPlayNetworking.send(serverPlayerEntity,
                        new NotifyAgeLockedPayload(itemTranslation, ageEntry.getAge().display().title().getString()))
                );

                return ActionResult.FAIL;
            }
        }

        return ActionResult.PASS;
    }
}
