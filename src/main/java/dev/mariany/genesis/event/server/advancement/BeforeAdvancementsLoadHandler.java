package dev.mariany.genesis.event.server.advancement;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.age.AgeEntry;
import dev.mariany.genesis.age.AgeManager;
import dev.mariany.genesis.config.ConfigHandler;
import dev.mariany.genesis.instruction.InstructionEntry;
import dev.mariany.genesis.instruction.InstructionManager;
import net.minecraft.advancement.*;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.AssetInfo;
import net.minecraft.util.Identifier;

import java.util.*;

public class BeforeAdvancementsLoadHandler {
    public static void beforeAdvancementsLoad(
            Map<Identifier, Advancement> advancementMap
    ) {
        loadAgeAdvancements(advancementMap);
    }

    private static void loadAgeAdvancements(Map<Identifier, Advancement> advancementMap) {
        AgeManager ageManager = AgeManager.getInstance();
        InstructionManager instructionManager = InstructionManager.getInstance();

        List<AdvancementEntry> advancements = getAdvancementEntries(ageManager, instructionManager);

        for (AdvancementEntry advancementEntry : advancements) {
            advancementMap.put(advancementEntry.id(), advancementEntry.value());
        }

        Genesis.LOGGER.info("Dynamically added {} age advancements successfully!", advancements.size());
    }

    private static List<AdvancementEntry> getAdvancementEntries(
            AgeManager ageManager,
            InstructionManager instructionManager
    ) {
        Collection<AgeEntry> ages = ageManager.getAges();
        Collection<InstructionEntry> instructions = instructionManager.getInstructions();
        List<AdvancementEntry> advancements = new ArrayList<>();

        advancements.add(getRootAgeAdvancement());

        if (ConfigHandler.getConfig().enableInstructions) {
            advancements.add(getRootInstructionAdvancement());
        }

        for (AgeEntry ageEntry : ages) {
            advancements.add(ageEntry.getAdvancementEntry());
        }

        for (InstructionEntry instructionEntry : instructions) {
            advancements.add(instructionEntry.getAdvancementEntry());
        }
        return advancements;
    }

    private static AdvancementEntry getRootAgeAdvancement() {
        return new AdvancementEntry(AgeEntry.ROOT_ADVANCEMENT_ID,
                new Advancement(
                        Optional.empty(),
                        Optional.of(new AdvancementDisplay(
                                Items.CAMPFIRE.getDefaultStack(),
                                Text.translatable("advancements.genesis.ages.title"),
                                Text.empty(),
                                Optional.of(new AssetInfo(Identifier.ofVanilla("block/dark_oak_planks"))),
                                AdvancementFrame.TASK,
                                false,
                                false,
                                false
                        )),
                        AdvancementRewards.NONE,
                        Map.of(
                                "root",
                                new AdvancementCriterion<>(
                                        Criteria.TICK, TickCriterion.Conditions.createTick().conditions()
                                )
                        ),
                        AdvancementRequirements.allOf(List.of("root")),
                        false
                )
        );
    }

    private static AdvancementEntry getRootInstructionAdvancement() {
        return new AdvancementEntry(InstructionEntry.ROOT_ADVANCEMENT_ID,
                new Advancement(
                        Optional.empty(),
                        Optional.of(new AdvancementDisplay(
                                Items.COMPASS.getDefaultStack(),
                                Text.translatable("advancements.genesis.instructions.title"),
                                Text.empty(),
                                Optional.of(new AssetInfo(Identifier.ofVanilla("block/lime_terracotta"))),
                                AdvancementFrame.TASK,
                                false,
                                false,
                                false
                        )),
                        AdvancementRewards.NONE,
                        Map.of(
                                "root",
                                new AdvancementCriterion<>(
                                        Criteria.TICK, TickCriterion.Conditions.createTick().conditions()
                                )
                        ),
                        AdvancementRequirements.allOf(List.of("root")),
                        false
                )
        );
    }
}
