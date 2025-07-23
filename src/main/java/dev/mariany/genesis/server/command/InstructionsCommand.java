package dev.mariany.genesis.server.command;

import com.mojang.brigadier.CommandDispatcher;
import dev.mariany.genesis.advancement.AdvancementHelper;
import dev.mariany.genesis.instruction.InstructionEntry;
import dev.mariany.genesis.instruction.InstructionManager;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.function.BiFunction;

public class InstructionsCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                CommandManager.literal("instructions")
                        .then(CommandManager.literal("reset")
                                .executes(context -> executeReset(
                                                context.getSource()
                                        )
                                )
                        )
                        .then(CommandManager.literal("skip")
                                .executes(context -> executeSkip(
                                                context.getSource()
                                        )
                                )
                        )
        );
    }

    private static int executeReset(ServerCommandSource source) {
        int processed = handleAdvancements(source, AdvancementHelper::revokeAdvancement);

        if (processed > 0) {
            source.sendFeedback(() -> Text.translatable("commands.instructions.reset", processed), false);
        } else {
            source.sendError(Text.translatable("commands.instructions.reset.failure"));
        }

        return processed;
    }

    private static int executeSkip(ServerCommandSource source) {
        int processed = handleAdvancements(source, AdvancementHelper::giveAdvancement);

        if (processed > 0) {
            source.sendFeedback(() -> Text.translatable("commands.instructions.skip", processed), false);
        } else {
            source.sendError(Text.translatable("commands.instructions.skip.failure"));
        }

        return processed;
    }

    private static int handleAdvancements(
            ServerCommandSource source,
            BiFunction<ServerPlayerEntity, AdvancementEntry, Boolean> consumer
    ) {
        int processed = 0;

        if (source.getEntity() instanceof ServerPlayerEntity serverPlayer) {
            InstructionManager instructionManager = InstructionManager.getInstance();
            Collection<InstructionEntry> instructions = instructionManager.getInstructions();

            for (var instruction : instructions) {
                if (consumer.apply(serverPlayer, instruction.getAdvancementEntry())) {
                    ++processed;
                }
            }
        }

        return processed;
    }
}
