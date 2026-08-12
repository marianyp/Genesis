package dev.mariany.genesis.datagen;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.advancement.criterion.SiftPrimitiveCauldronCriteria;
import dev.mariany.genesis.advancement.criterion.CookWithKilnCriteria;
import dev.mariany.genesis.advancement.criterion.GenesisCriteria;
import dev.mariany.genesis.block.GenesisBlocks;
import dev.mariany.genesis.item.GenesisItems;
import dev.mariany.genesis.world.effect.GenesisMobEffects;
import dev.mariany.genesisframework.datagen.InstructionProvider;
import dev.mariany.genesisframework.instruction.Instruction;
import dev.mariany.genesisframework.instruction.InstructionEntry;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.advancements.predicates.MobEffectsPredicate;
import net.minecraft.advancements.triggers.EffectsChangedTrigger;
import net.minecraft.advancements.triggers.PlayerTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class GenesisInstructionProvider extends InstructionProvider {
    public GenesisInstructionProvider(
            FabricPackOutput output,
            CompletableFuture<HolderLookup.Provider> registryLookup
    ) {
        super(output, registryLookup);
    }

    @Override
    public void generateInstructions(
            HolderLookup.Provider registryLookup,
            Consumer<InstructionEntry> consumer
    ) {
        InstructionEntry findFlint = Instruction.Builder
                .create()
                .parent(InstructionEntry.VIEW_AGES_INSTRUCTION_ID)
                .display(
                        Items.FLINT,
                        Component.translatable("instruction.genesis.find_flint")
                )
                .requireItem(Items.FLINT)
                .build(consumer, Genesis.id("find_flint"));

        InstructionEntry makeFlints = Instruction.Builder
                .create()
                .parent(findFlint)
                .display(
                        GenesisItems.FLINTS,
                        Component.translatable("instruction.genesis.craft_flints"),
                        Component.translatable("instruction.genesis.craft_flints.description")
                )
                .requireCraft(GenesisItems.FLINTS)
                .build(consumer, Genesis.id("make_flints"));

        InstructionEntry makeCampfire = Instruction.Builder
                .create()
                .parent(makeFlints)
                .display(
                        Items.CAMPFIRE,
                        Component.translatable("instruction.genesis.make_campfire"),
                        Component.translatable("instruction.genesis.make_campfire.description")
                )
                .criterion(
                        "started_fire",
                        GenesisCriteria.FIRE_STARTED.createCriterion(
                                new PlayerTrigger.TriggerInstance(Optional.empty())
                        )
                )
                .build(consumer, Genesis.id("make_campfire"));

        InstructionEntry findClay = Instruction.Builder
                .create()
                .parent(makeCampfire)
                .display(Items.CLAY_BALL, Component.translatable("instruction.genesis.find_clay"))
                .requireItem(Items.CLAY_BALL)
                .build(consumer, Genesis.id("find_clay"));

        InstructionEntry craftClayCauldron = Instruction.Builder
                .create()
                .parent(findClay)
                .display(
                        GenesisBlocks.CLAY_CAULDRON,
                        Component.translatable("instruction.genesis.craft_clay_cauldron"),
                        Component.translatable("instruction.genesis.craft_clay_cauldron.description")
                )
                .requireCraft(GenesisBlocks.CLAY_CAULDRON)
                .build(consumer, Genesis.id("craft_clay_cauldron"));

        InstructionEntry cookTerracottaCauldron = Instruction.Builder
                .create()
                .parent(craftClayCauldron)
                .display(
                        GenesisBlocks.TERRACOTTA_CAULDRON,
                        Component.translatable("instruction.genesis.cook_terracotta_cauldron")
                )
                .requireItem(GenesisBlocks.TERRACOTTA_CAULDRON)
                .build(consumer, Genesis.id("cook_terracotta_cauldron"));

        InstructionEntry siftTerracottaCauldron = Instruction.Builder
                .create()
                .parent(cookTerracottaCauldron)
                .display(
                        GenesisBlocks.DIRT_TERRACOTTA_CAULDRON,
                        Component.translatable("instruction.genesis.sift_terracotta_cauldron"),
                        Component.translatable("instruction.genesis.sift_terracotta_cauldron.description")
                )
                .criterion("sifted_terracotta_cauldron", SiftPrimitiveCauldronCriteria.Conditions.create())
                .build(consumer, Genesis.id("sift_terracotta_cauldron"));

        InstructionEntry craftBlankClayCast = Instruction.Builder
                .create()
                .parent(siftTerracottaCauldron)
                .display(
                        GenesisItems.BLANK_CLAY_CAST,
                        Component.translatable("instruction.genesis.craft_blank_clay_cast"),
                        Component.translatable("instruction.genesis.craft_blank_clay_cast.description")
                )
                .requireCraft(GenesisItems.BLANK_CLAY_CAST)
                .build(consumer, Genesis.id("craft_blank_clay_cast"));

        InstructionEntry craftClayPickaxeCast = Instruction.Builder
                .create()
                .parent(craftBlankClayCast)
                .display(
                        GenesisItems.CLAY_PICKAXE_CAST,
                        Component.translatable("instruction.genesis.craft_clay_pickaxe_cast"),
                        Component.translatable("instruction.genesis.craft_clay_pickaxe_cast.description")
                )
                .requireCraft(GenesisItems.CLAY_PICKAXE_CAST)
                .build(consumer, Genesis.id("craft_clay_pickaxe_cast"));

        InstructionEntry cookClayPickaxeCast = Instruction.Builder
                .create()
                .parent(craftClayPickaxeCast)
                .display(
                        GenesisItems.PICKAXE_CAST,
                        Component.translatable("instruction.genesis.cook_clay_pickaxe_cast")
                )
                .requireItem(GenesisItems.PICKAXE_CAST)
                .build(consumer, Genesis.id("cook_clay_pickaxe_cast"));

        InstructionEntry craftAssemblyTable = Instruction.Builder
                .create()
                .parent(cookClayPickaxeCast)
                .display(
                        GenesisBlocks.ASSEMBLY_TABLE,
                        Component.translatable("instruction.genesis.craft_assembly_table"),
                        Component.translatable("instruction.genesis.craft_assembly_table.description")
                )
                .requireCraft(GenesisBlocks.ASSEMBLY_TABLE)
                .build(consumer, Genesis.id("craft_assembly_table"));

        InstructionEntry craftWoodenPickaxe = Instruction.Builder
                .create()
                .parent(craftAssemblyTable)
                .display(
                        Items.WOODEN_PICKAXE,
                        Component.translatable("instruction.genesis.craft_wooden_pickaxe"),
                        Component.translatable("instruction.genesis.craft_wooden_pickaxe.description")
                )
                .requireCraft(Items.WOODEN_PICKAXE)
                .build(consumer, Genesis.id("craft_wooden_pickaxe"));

        InstructionEntry craftClaySpearCast = Instruction.Builder
                .create()
                .parent(craftWoodenPickaxe)
                .display(
                        GenesisItems.CLAY_SPEAR_CAST,
                        Component.translatable("instruction.genesis.craft_clay_spear_cast"),
                        Component.translatable("instruction.genesis.craft_clay_spear_cast.description")
                )
                .requireCraft(GenesisItems.CLAY_SPEAR_CAST)
                .build(consumer, Genesis.id("craft_clay_spear_cast"));

        InstructionEntry cookClaySpearCast = Instruction.Builder
                .create()
                .parent(craftClaySpearCast)
                .display(
                        GenesisItems.SPEAR_CAST,
                        Component.translatable("instruction.genesis.cook_clay_spear_cast")
                )
                .requireItem(GenesisItems.SPEAR_CAST)
                .build(consumer, Genesis.id("cook_clay_spear_cast"));

        InstructionEntry craftWoodenSpear = Instruction.Builder
                .create()
                .parent(cookClaySpearCast)
                .display(
                        Items.WOODEN_SPEAR,
                        Component.translatable("instruction.genesis.craft_wooden_spear"),
                        Component.translatable("instruction.genesis.craft_wooden_spear.description")
                )
                .requireCraft(Items.WOODEN_SPEAR)
                .build(consumer, Genesis.id("craft_wooden_tool"));

        InstructionEntry craftClayKiln = Instruction.Builder
                .create()
                .parent(craftWoodenSpear)
                .display(
                        GenesisBlocks.CLAY_KILN,
                        Component.translatable("instruction.genesis.craft_clay_kiln"),
                        Component.translatable("instruction.genesis.craft_clay_kiln.description")
                )
                .requireCraft(GenesisBlocks.CLAY_KILN)
                .build(consumer, Genesis.id("craft_clay_kiln"));

        InstructionEntry cookKiln = Instruction.Builder
                .create()
                .parent(craftClayKiln)
                .display(
                        GenesisBlocks.KILN,
                        Component.translatable("instruction.genesis.cook_kiln")
                )
                .requireItem(GenesisBlocks.KILN)
                .build(consumer, Genesis.id("cook_kiln"));

        InstructionEntry useKiln = Instruction.Builder
                .create()
                .parent(cookKiln)
                .display(
                        Items.CHARCOAL,
                        Component.translatable("instruction.genesis.use_kiln"),
                        Component.translatable("instruction.genesis.use_kiln.description")
                )
                .criterion("used_kiln", CookWithKilnCriteria.Conditions.create())
                .build(consumer, Genesis.id("use_kiln"));

        Instruction.Builder
                .create()
                .parent(useKiln)
                .display(
                        Items.COOKED_CHICKEN,
                        Component.translatable("instruction.genesis.obtain_solace"),
                        Component.translatable("instruction.genesis.obtain_solace.description")
                )
                .criterion(
                        "obtained_solace",
                        EffectsChangedTrigger.TriggerInstance.hasEffects(
                                MobEffectsPredicate.Builder
                                        .effects()
                                        .and(GenesisMobEffects.SOLACE)
                        )
                )
                .build(consumer, Genesis.id("obtain_solace"));
    }

    @Override
    public String getName() {
        return "Genesis Instructions";
    }
}
