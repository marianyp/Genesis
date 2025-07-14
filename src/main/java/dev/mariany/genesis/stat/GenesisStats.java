package dev.mariany.genesis.stat;

import dev.mariany.genesis.Genesis;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;

public class GenesisStats {
    public static final Identifier INTERACT_WITH_KILN = register("interact_with_kiln", StatFormatter.DEFAULT);

    private static Identifier register(String id, StatFormatter formatter) {
        Identifier identifier = Genesis.id(id);
        Registry.register(Registries.CUSTOM_STAT, id, identifier);
        Stats.CUSTOM.getOrCreateStat(identifier, formatter);
        return identifier;
    }

    public static void bootstrap() {
        Genesis.LOGGER.info("Registering Stats for " + Genesis.MOD_ID);
    }
}
