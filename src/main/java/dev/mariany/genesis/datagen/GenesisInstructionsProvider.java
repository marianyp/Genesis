package dev.mariany.genesis.datagen;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.advancement.criterion.BrushPrimitiveCauldronCriteria;
import dev.mariany.genesis.advancement.criterion.CookWithKilnCriteria;
import dev.mariany.genesis.advancement.criterion.GenesisCriteria;
import dev.mariany.genesis.block.GenesisBlocks;
import dev.mariany.genesis.instruction.Instruction;
import dev.mariany.genesis.instruction.InstructionEntry;
import dev.mariany.genesis.item.GenesisItems;
import dev.mariany.genesis.tag.GenesisTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class GenesisInstructionsProvider extends InstructionsProvider {
    private static final Identifier FIND_FLINT = Genesis.id("find_flint");
    private static final Identifier MAKE_FLINTS = Genesis.id("make_flints");
    private static final Identifier MAKE_CAMPFIRE = Genesis.id("make_campfire");
    private static final Identifier CRAFT_CLAY_CAULDRON = Genesis.id("craft_clay_cauldron");
    private static final Identifier COOK_TERRACOTTA_CAULDRON = Genesis.id("cook_terracotta_cauldron");
    private static final Identifier DUST_TERRACOTTA_CAULDRON = Genesis.id("dust_terracotta_cauldron");
    private static final Identifier CRAFT_BLANK_CLAY_CAST = Genesis.id("craft_blank_clay_cast");
    private static final Identifier CRAFT_CLAY_TOOL_CAST = Genesis.id("craft_clay_tool_cast");
    private static final Identifier COOK_TOOL_CAST = Genesis.id("cook_tool_cast");
    private static final Identifier CRAFT_WOODEN_TOOL = Genesis.id("craft_wooden_tool");
    private static final Identifier CRAFT_CLAY_KILN = Genesis.id("craft_clay_kiln");
    private static final Identifier COOK_KILN = Genesis.id("cook_kiln");
    private static final Identifier USE_KILN = Genesis.id("use_kiln");
    private static final Identifier CRAFT_HEALTHY_STEW = Genesis.id("craft_healthy_stew");

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

        Instruction.Builder.create()
                .display(Items.FLINT, Text.translatable("instruction.genesis.find_flint"))
                .criterion("obtained_flint", InventoryChangedCriterion.Conditions.items(Items.FLINT))
                .build(consumer, FIND_FLINT);

        Instruction.Builder.create()
                .parent(FIND_FLINT)
                .display(
                        GenesisItems.FLINTS,
                        Text.translatable("instruction.genesis.craft_flints"),
                        Text.translatable("instruction.genesis.craft_flints.description")
                )
                .criterion("obtained_flints", InventoryChangedCriterion.Conditions.items(GenesisItems.FLINTS))
                .build(consumer, MAKE_FLINTS);

        Instruction.Builder.create()
                .parent(MAKE_FLINTS)
                .display(
                        Items.CAMPFIRE,
                        Text.translatable("instruction.genesis.make_campfire"),
                        Text.translatable("instruction.genesis.make_campfire.description")
                )
                .criterion("started_fire", GenesisCriteria.FIRE_STARTED.create(new TickCriterion.Conditions(
                        Optional.empty()
                )))
                .build(consumer, MAKE_CAMPFIRE);

        Instruction.Builder.create()
                .parent(MAKE_CAMPFIRE)
                .display(
                        GenesisBlocks.CLAY_CAULDRON.asItem(),
                        Text.translatable("instruction.genesis.craft_clay_cauldron"),
                        Text.translatable("instruction.genesis.craft_clay_cauldron.description")
                )
                .criterion("obtained_clay_cauldron", InventoryChangedCriterion.Conditions.items(
                        GenesisBlocks.CLAY_CAULDRON
                ))
                .build(consumer, CRAFT_CLAY_CAULDRON);

        Instruction.Builder.create()
                .parent(CRAFT_CLAY_CAULDRON)
                .display(
                        GenesisBlocks.TERRACOTTA_CAULDRON.asItem(),
                        Text.translatable("instruction.genesis.cook_terracotta_cauldron")
                )
                .criterion("obtained_terracotta_cauldron", InventoryChangedCriterion.Conditions.items(
                        GenesisBlocks.TERRACOTTA_CAULDRON
                ))
                .build(consumer, COOK_TERRACOTTA_CAULDRON);

        Instruction.Builder.create()
                .parent(COOK_TERRACOTTA_CAULDRON)
                .display(
                        GenesisBlocks.DIRT_TERRACOTTA_CAULDRON.asItem(),
                        Text.translatable("instruction.genesis.dust_terracotta_cauldron"),
                        Text.translatable("instruction.genesis.dust_terracotta_cauldron.description")
                )
                .criterion("dusted_terracotta_cauldron", BrushPrimitiveCauldronCriteria.Conditions.create())
                .build(consumer, DUST_TERRACOTTA_CAULDRON);

        Instruction.Builder.create()
                .parent(DUST_TERRACOTTA_CAULDRON)
                .display(
                        GenesisItems.BLANK_CLAY_CAST,
                        Text.translatable("instruction.genesis.craft_blank_clay_cast"),
                        Text.translatable("instruction.genesis.craft_blank_clay_cast.description")
                )
                .criterion("obtained_blank_clay_cast", InventoryChangedCriterion.Conditions.items(
                        GenesisItems.BLANK_CLAY_CAST
                ))
                .build(consumer, CRAFT_BLANK_CLAY_CAST);

        Instruction.Builder.create()
                .parent(CRAFT_BLANK_CLAY_CAST)
                .display(
                        GenesisItems.PICKAXE_CLAY_CAST,
                        Text.translatable("instruction.genesis.craft_clay_tool_cast"),
                        Text.translatable("instruction.genesis.craft_clay_tool_cast.description")
                )
                .criterion("obtained_clay_tool_cast", InventoryChangedCriterion.Conditions.items(
                        ItemPredicate.Builder.create().tag(itemLookup, GenesisTags.Items.INSTRUCTIONS_CLAY_TOOL_CASTS)
                ))
                .build(consumer, CRAFT_CLAY_TOOL_CAST);

        Instruction.Builder.create()
                .parent(CRAFT_CLAY_TOOL_CAST)
                .display(
                        GenesisItems.PICKAXE_CAST,
                        Text.translatable("instruction.genesis.cook_tool_cast")
                )
                .criterion("cooked_tool_cast", InventoryChangedCriterion.Conditions.items(
                        ItemPredicate.Builder.create().tag(itemLookup, GenesisTags.Items.INSTRUCTIONS_TOOL_CASTS)
                ))
                .build(consumer, COOK_TOOL_CAST);

        Instruction.Builder.create()
                .parent(COOK_TOOL_CAST)
                .display(
                        Items.WOODEN_PICKAXE,
                        Text.translatable("instruction.genesis.craft_wooden_tool"),
                        Text.translatable("instruction.genesis.craft_wooden_tool.description")
                )
                .criterion("obtained_wooden_tool", InventoryChangedCriterion.Conditions.items(
                        ItemPredicate.Builder.create().tag(itemLookup, GenesisTags.Items.WOODEN_TOOLS)
                ))
                .build(consumer, CRAFT_WOODEN_TOOL);

        Instruction.Builder.create()
                .parent(CRAFT_WOODEN_TOOL)
                .display(
                        GenesisBlocks.CLAY_KILN.asItem(),
                        Text.translatable("instruction.genesis.craft_clay_kiln"),
                        Text.translatable("instruction.genesis.craft_clay_kiln.description")
                )
                .criterion("obtained_clay_kiln", InventoryChangedCriterion.Conditions.items(
                        GenesisBlocks.CLAY_KILN
                ))
                .build(consumer, CRAFT_CLAY_KILN);

        Instruction.Builder.create()
                .parent(CRAFT_CLAY_KILN)
                .display(
                        GenesisBlocks.KILN.asItem(),
                        Text.translatable("instruction.genesis.cook_kiln")
                )
                .criterion("obtained_kiln", InventoryChangedCriterion.Conditions.items(
                        GenesisBlocks.KILN
                ))
                .build(consumer, COOK_KILN);

        Instruction.Builder.create()
                .parent(COOK_KILN)
                .display(
                        Items.CHARCOAL,
                        Text.translatable("instruction.genesis.use_kiln"),
                        Text.translatable("instruction.genesis.use_kiln.description")
                )
                .criterion("used_kiln", CookWithKilnCriteria.Conditions.create())
                .build(consumer, USE_KILN);

        Instruction.Builder.create()
                .parent(USE_KILN)
                .display(
                        GenesisItems.HEALTHY_STEW,
                        Text.translatable("instruction.genesis.craft_healthy_stew"),
                        Text.translatable("instruction.genesis.craft_healthy_stew.description")
                )
                .criterion("obtained_healthy_stew", InventoryChangedCriterion.Conditions.items(
                        GenesisItems.HEALTHY_STEW
                ))
                .build(consumer, CRAFT_HEALTHY_STEW);
    }

    @Override
    public String getName() {
        return "Genesis Instructions";
    }
}
