package dev.mariany.genesis.datagen;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.advancement.criterion.BrushPrimitiveCauldronCriteria;
import dev.mariany.genesis.advancement.criterion.CookWithKilnCriteria;
import dev.mariany.genesis.advancement.criterion.GenesisCriteria;
import dev.mariany.genesis.advancement.criterion.OpenAdvancementTabCriteria;
import dev.mariany.genesis.age.AgeEntry;
import dev.mariany.genesis.block.GenesisBlocks;
import dev.mariany.genesis.instruction.Instruction;
import dev.mariany.genesis.instruction.InstructionEntry;
import dev.mariany.genesis.item.GenesisItems;
import dev.mariany.genesis.tag.GenesisTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class GenesisInstructionsProvider extends InstructionsProvider {
    public GenesisInstructionsProvider(
            FabricDataOutput output,
            CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup
    ) {
        super(output, registryLookup);
    }

    @Override
    public void generateInstructions(
            RegistryWrapper.WrapperLookup registryLookup,
            Consumer<InstructionEntry> consumer
    ) {
        RegistryEntryLookup<Item> itemLookup = registryLookup.getOrThrow(RegistryKeys.ITEM);

        InstructionEntry viewAges = Instruction.Builder.create()
                .display(
                        Items.WRITTEN_BOOK,
                        Text.translatable("instruction.genesis.view_ages"),
                        Text.translatable("instruction.genesis.view_ages.description",
                                Text.keybind(MinecraftClient.getInstance().options.advancementsKey.getTranslationKey())
                        )
                )
                .criterion("view_ages", OpenAdvancementTabCriteria.Conditions.create(AgeEntry.ROOT_ADVANCEMENT_ID))
                .build(consumer, Genesis.id("view_ages"));

        InstructionEntry findFlint = Instruction.Builder.create()
                .parent(viewAges)
                .display(Items.FLINT, Text.translatable("instruction.genesis.find_flint"))
                .criterion("obtained_flint", InventoryChangedCriterion.Conditions.items(Items.FLINT))
                .build(consumer, Genesis.id("find_flint"));

        InstructionEntry makeFlints = Instruction.Builder.create()
                .parent(findFlint)
                .display(
                        GenesisItems.FLINTS,
                        Text.translatable("instruction.genesis.craft_flints"),
                        Text.translatable("instruction.genesis.craft_flints.description")
                )
                .criterion("obtained_flints", InventoryChangedCriterion.Conditions.items(GenesisItems.FLINTS))
                .build(consumer, Genesis.id("make_flints"));

        InstructionEntry makeCampfire = Instruction.Builder.create()
                .parent(makeFlints)
                .display(
                        Items.CAMPFIRE,
                        Text.translatable("instruction.genesis.make_campfire"),
                        Text.translatable("instruction.genesis.make_campfire.description")
                )
                .criterion("started_fire", GenesisCriteria.FIRE_STARTED.create(new TickCriterion.Conditions(
                        Optional.empty()
                )))
                .build(consumer, Genesis.id("make_campfire"));

        InstructionEntry findClay = Instruction.Builder.create()
                .parent(makeCampfire)
                .display(Items.CLAY_BALL, Text.translatable("instruction.genesis.find_clay"))
                .criterion("obtained_clay", InventoryChangedCriterion.Conditions.items(Items.CLAY_BALL))
                .build(consumer, Genesis.id("find_clay"));

        InstructionEntry craftClayCauldron = Instruction.Builder.create()
                .parent(findClay)
                .display(
                        GenesisBlocks.CLAY_CAULDRON.asItem(),
                        Text.translatable("instruction.genesis.craft_clay_cauldron"),
                        Text.translatable("instruction.genesis.craft_clay_cauldron.description")
                )
                .criterion("obtained_clay_cauldron", InventoryChangedCriterion.Conditions.items(
                        GenesisBlocks.CLAY_CAULDRON
                ))
                .build(consumer, Genesis.id("craft_clay_cauldron"));

        InstructionEntry cookTerracottaCauldron = Instruction.Builder.create()
                .parent(craftClayCauldron)
                .display(
                        GenesisBlocks.TERRACOTTA_CAULDRON.asItem(),
                        Text.translatable("instruction.genesis.cook_terracotta_cauldron")
                )
                .criterion("obtained_terracotta_cauldron", InventoryChangedCriterion.Conditions.items(
                        GenesisBlocks.TERRACOTTA_CAULDRON
                ))
                .build(consumer, Genesis.id("cook_terracotta_cauldron"));

        InstructionEntry dustTerracottaCauldron = Instruction.Builder.create()
                .parent(cookTerracottaCauldron)
                .display(
                        GenesisBlocks.DIRT_TERRACOTTA_CAULDRON.asItem(),
                        Text.translatable("instruction.genesis.dust_terracotta_cauldron"),
                        Text.translatable("instruction.genesis.dust_terracotta_cauldron.description")
                )
                .criterion("dusted_terracotta_cauldron", BrushPrimitiveCauldronCriteria.Conditions.create())
                .build(consumer, Genesis.id("dust_terracotta_cauldron"));

        InstructionEntry craftBlankClayCast = Instruction.Builder.create()
                .parent(dustTerracottaCauldron)
                .display(
                        GenesisItems.BLANK_CLAY_CAST,
                        Text.translatable("instruction.genesis.craft_blank_clay_cast"),
                        Text.translatable("instruction.genesis.craft_blank_clay_cast.description")
                )
                .criterion("obtained_blank_clay_cast", InventoryChangedCriterion.Conditions.items(
                        GenesisItems.BLANK_CLAY_CAST
                ))
                .build(consumer, Genesis.id("craft_blank_clay_cast"));

        InstructionEntry craftClayToolCast = Instruction.Builder.create()
                .parent(craftBlankClayCast)
                .display(
                        GenesisItems.PICKAXE_CLAY_CAST,
                        Text.translatable("instruction.genesis.craft_clay_tool_cast"),
                        Text.translatable("instruction.genesis.craft_clay_tool_cast.description")
                )
                .criterion("obtained_clay_tool_cast", InventoryChangedCriterion.Conditions.items(
                        ItemPredicate.Builder.create().tag(itemLookup, GenesisTags.Items.INSTRUCTIONS_CLAY_TOOL_CASTS)
                ))
                .build(consumer, Genesis.id("craft_clay_tool_cast"));

        InstructionEntry cookToolCast = Instruction.Builder.create()
                .parent(craftClayToolCast)
                .display(
                        GenesisItems.PICKAXE_CAST,
                        Text.translatable("instruction.genesis.cook_tool_cast")
                )
                .criterion("cooked_tool_cast", InventoryChangedCriterion.Conditions.items(
                        ItemPredicate.Builder.create().tag(itemLookup, GenesisTags.Items.INSTRUCTIONS_TOOL_CASTS)
                ))
                .build(consumer, Genesis.id("cook_tool_cast"));

        InstructionEntry craftWoodenTool = Instruction.Builder.create()
                .parent(cookToolCast)
                .display(
                        Items.WOODEN_PICKAXE,
                        Text.translatable("instruction.genesis.craft_wooden_tool"),
                        Text.translatable("instruction.genesis.craft_wooden_tool.description")
                )
                .criterion("obtained_wooden_tool", InventoryChangedCriterion.Conditions.items(
                        ItemPredicate.Builder.create().tag(itemLookup, GenesisTags.Items.WOODEN_TOOLS)
                ))
                .build(consumer, Genesis.id("craft_wooden_tool"));

        InstructionEntry craftClayKiln = Instruction.Builder.create()
                .parent(craftWoodenTool)
                .display(
                        GenesisBlocks.CLAY_KILN.asItem(),
                        Text.translatable("instruction.genesis.craft_clay_kiln"),
                        Text.translatable("instruction.genesis.craft_clay_kiln.description")
                )
                .criterion("obtained_clay_kiln", InventoryChangedCriterion.Conditions.items(
                        GenesisBlocks.CLAY_KILN
                ))
                .build(consumer, Genesis.id("craft_clay_kiln"));

        InstructionEntry cookKiln = Instruction.Builder.create()
                .parent(craftClayKiln)
                .display(
                        GenesisBlocks.KILN.asItem(),
                        Text.translatable("instruction.genesis.cook_kiln")
                )
                .criterion("obtained_kiln", InventoryChangedCriterion.Conditions.items(
                        GenesisBlocks.KILN
                ))
                .build(consumer, Genesis.id("cook_kiln"));

        InstructionEntry useKiln = Instruction.Builder.create()
                .parent(cookKiln)
                .display(
                        Items.CHARCOAL,
                        Text.translatable("instruction.genesis.use_kiln"),
                        Text.translatable("instruction.genesis.use_kiln.description")
                )
                .criterion("used_kiln", CookWithKilnCriteria.Conditions.create())
                .build(consumer, Genesis.id("use_kiln"));

        Instruction.Builder.create()
                .parent(useKiln)
                .display(
                        GenesisItems.HEALTHY_STEW,
                        Text.translatable("instruction.genesis.craft_healthy_stew"),
                        Text.translatable("instruction.genesis.craft_healthy_stew.description")
                )
                .criterion("obtained_healthy_stew", InventoryChangedCriterion.Conditions.items(
                        GenesisItems.HEALTHY_STEW
                ))
                .build(consumer, Genesis.id("craft_healthy_stew"));
    }

    @Override
    public String getName() {
        return "Genesis Instructions";
    }
}
