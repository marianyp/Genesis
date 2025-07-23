package dev.mariany.genesis.server.command;

import com.mojang.brigadier.CommandDispatcher;
import dev.mariany.genesis.age.AgeShareManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

public class AgeCommand {
    public static void register(
            CommandDispatcher<ServerCommandSource> dispatcher
    ) {
        dispatcher.register(
                CommandManager.literal("age")
                        .requires(CommandManager.requirePermissionLevel(2))
                        .then(CommandManager.literal("clear")
                                .then(CommandManager.literal("global")
                                        .executes(
                                                context -> executeClear(
                                                        context.getSource(), true
                                                )
                                        )
                                )
                                .then(CommandManager.literal("teams")
                                        .executes(
                                                context -> executeClear(
                                                        context.getSource(), false
                                                )
                                        )
                                )
                        )
        );
    }

    private static int executeClear(ServerCommandSource source, boolean global) {
        ServerWorld world = source.getWorld();
        MinecraftServer server = world.getServer();
        AgeShareManager ageShareManager = AgeShareManager.getServerState(server);

        int cleared = ageShareManager.clear(global);

        source.sendFeedback(() -> Text.translatable("commands.age.clear", cleared), true);

        return 1;
    }
}
