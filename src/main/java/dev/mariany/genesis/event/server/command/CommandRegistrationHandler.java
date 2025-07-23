package dev.mariany.genesis.event.server.command;

import com.mojang.brigadier.CommandDispatcher;
import dev.mariany.genesis.server.command.AgeCommand;
import dev.mariany.genesis.server.command.InstructionsCommand;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class CommandRegistrationHandler {
    public static void onRegister(
            CommandDispatcher<ServerCommandSource> dispatcher,
            CommandRegistryAccess registryAccess,
            CommandManager.RegistrationEnvironment environment
    ) {
        AgeCommand.register(dispatcher);
        InstructionsCommand.register(dispatcher);
    }
}
