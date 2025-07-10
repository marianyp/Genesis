package dev.mariany.genesis.mixin;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.age.AgeEntry;
import dev.mariany.genesis.age.AgeManager;
import dev.mariany.genesis.block.GenesisBlocks;
import dev.mariany.genesis.mixin.accessor.AdvancementManagerAccesor;
import net.minecraft.advancement.*;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.text.Text;
import net.minecraft.util.AssetInfo;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(ServerAdvancementLoader.class)
public class ServerAdvancementLoaderMixin {
    @Shadow
    private AdvancementManager manager;
    @Shadow
    private Map<Identifier, AdvancementEntry> advancements;

    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V", at = @At(value = "TAIL"))
    protected void apply(Map<Identifier, JsonElement> map, ResourceManager resourceManager, Profiler profiler,
                         CallbackInfo ci) {
        AgeManager ageManager = AgeManager.getInstance();
        List<AdvancementEntry> ageAdvancementEntries = new ArrayList<>();

        for (AgeEntry ageEntry : ageManager.getAges()) {
            ageAdvancementEntries.add(ageEntry.getAdvancementEntry());
        }

        ageAdvancementEntries.add(new AdvancementEntry(AgeEntry.ROOT_ADVANCEMENT_ID, new Advancement(
                Optional.empty(),
                Optional.of(new AdvancementDisplay(
                        GenesisBlocks.KILN.asItem().getDefaultStack(),
                        Text.translatable("advancements.ages.title"),
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
        )));

        Map<Identifier, AdvancementEntry> allAdvancements = new HashMap<>(advancements);

        for (AdvancementEntry entry : ageAdvancementEntries) {
            allAdvancements.put(entry.id(), entry);
        }

        this.advancements = ImmutableMap.copyOf(allAdvancements);

        addAll(ageAdvancementEntries);

        Genesis.LOGGER.info("Dynamically loaded {} advancements", ageAdvancementEntries.size());
    }

    @Unique
    private void addAll(Collection<AdvancementEntry> advancements) {
        List<AdvancementEntry> pending = new ArrayList<>(advancements);

        while (!pending.isEmpty()) {
            List<AdvancementEntry> completed = new ArrayList<>();

            for (AdvancementEntry entry : advancements) {
                boolean added = ((AdvancementManagerAccesor) manager).genesis$tryAdd(entry);
                if (added) {
                    completed.add(entry);
                }
            }

            pending.removeAll(completed);
        }
    }
}
