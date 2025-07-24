package dev.mariany.genesis.datagen;

import dev.mariany.genesis.item.GenesisItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@SuppressWarnings("removal")
public class GenesisAdvancementsOverrideProvider extends FabricAdvancementProvider {
    public GenesisAdvancementsOverrideProvider(
            FabricDataOutput output,
            CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup
    ) {
        super(output, registryLookup);
    }

    @Override
    public void generateAdvancement(RegistryWrapper.WrapperLookup registryLookup, Consumer<AdvancementEntry> consumer) {
        Advancement.Builder.create()
                .parent(Identifier.ofVanilla("story/iron_tools"))
                .display(
                        GenesisItems.RAW_DIAMOND,
                        Text.translatable("advancements.story.mine_diamond.title"),
                        Text.translatable("advancements.story.mine_diamond.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criterion("diamond", InventoryChangedCriterion.Conditions.items(Items.DIAMOND))
                .criterion("raw_diamond", InventoryChangedCriterion.Conditions.items(GenesisItems.RAW_DIAMOND))
                .requirements(AdvancementRequirements.anyOf(List.of("diamond", "raw_diamond")))
                .build(consumer, "story/mine_diamond");
    }
}
