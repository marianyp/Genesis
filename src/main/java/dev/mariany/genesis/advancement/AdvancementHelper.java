package dev.mariany.genesis.advancement;

import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.network.ServerPlayerEntity;

public class AdvancementHelper {
    public static boolean giveAdvancement(ServerPlayerEntity player, AdvancementEntry advancementEntry) {
        PlayerAdvancementTracker tracker = player.getAdvancementTracker();
        AdvancementProgress progress = tracker.getProgress(advancementEntry);

        if (progress.isDone()) {
            return false;
        }

        for (String criterion : progress.getUnobtainedCriteria()) {
            tracker.grantCriterion(advancementEntry, criterion);
        }

        return true;
    }

    public static boolean revokeAdvancement(ServerPlayerEntity player, AdvancementEntry advancementEntry) {
        PlayerAdvancementTracker tracker = player.getAdvancementTracker();
        AdvancementProgress progress = tracker.getProgress(advancementEntry);

        if (!progress.isDone()) {
            return false;
        }

        for (String criterion : progress.getObtainedCriteria()) {
            tracker.revokeCriterion(advancementEntry, criterion);
        }

        return true;
    }
}
