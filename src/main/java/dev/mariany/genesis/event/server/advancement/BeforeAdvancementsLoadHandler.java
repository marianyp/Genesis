package dev.mariany.genesis.event.server.advancement;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.age.AgeEntry;
import dev.mariany.genesis.age.AgeManager;
import net.minecraft.advancement.*;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.block.Blocks;
import net.minecraft.text.Text;
import net.minecraft.util.AssetInfo;
import net.minecraft.util.Identifier;

import java.util.*;

public class BeforeAdvancementsLoadHandler {
    public static void onAdvancementsLoad(Map<Identifier, Advancement> advancementMap) {
        AgeManager ageManager = AgeManager.getInstance();

        Collection<AgeEntry> ages = ageManager.getAges();
        List<AdvancementEntry> advancements = new ArrayList<>();

        advancements.add(getRootAdvancement());

        for (AgeEntry ageEntry : ages) {
            advancements.add(ageEntry.getAdvancementEntry());
        }

        for (AdvancementEntry advancementEntry : advancements) {
            advancementMap.put(advancementEntry.id(), advancementEntry.value());
        }

        Genesis.LOGGER.info("Dynamically added {} age advancements successfully!", advancements.size());
    }

    public static AdvancementEntry getRootAdvancement() {
        return new AdvancementEntry(AgeEntry.ROOT_ADVANCEMENT_ID,
                new Advancement(
                        Optional.empty(),
                        Optional.of(new AdvancementDisplay(
                                Blocks.CAMPFIRE.asItem().getDefaultStack(),
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
}
