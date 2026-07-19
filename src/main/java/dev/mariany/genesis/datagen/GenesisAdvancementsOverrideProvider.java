package dev.mariany.genesis.datagen;

import dev.mariany.genesis.item.GenesisItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@SuppressWarnings("removal")
public class GenesisAdvancementsOverrideProvider extends FabricAdvancementProvider {
    public GenesisAdvancementsOverrideProvider(
            FabricPackOutput output,
            CompletableFuture<HolderLookup.Provider> registryLookup
    ) {
        super(output, registryLookup);
    }

    @Override
    public void generateAdvancement(HolderLookup.Provider registryLookup, Consumer<AdvancementHolder> consumer) {
        Advancement.Builder.advancement()
                .parent(Identifier.withDefaultNamespace("story/iron_tools"))
                .display(
                        GenesisItems.RAW_DIAMOND,
                        Component.translatable("advancements.story.mine_diamond.title"),
                        Component.translatable("advancements.story.mine_diamond.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("diamond", InventoryChangeTrigger.TriggerInstance.hasItems(Items.DIAMOND))
                .addCriterion("raw_diamond", InventoryChangeTrigger.TriggerInstance.hasItems(GenesisItems.RAW_DIAMOND))
                .requirements(AdvancementRequirements.anyOf(List.of("diamond", "raw_diamond")))
                .save(consumer, "story/mine_diamond");
    }
}
