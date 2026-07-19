package dev.mariany.genesis.stat;

import dev.mariany.genesis.Genesis;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;

public class GenesisStats {
    public static final Identifier INTERACT_WITH_KILN = register("interact_with_kiln", StatFormatter.DEFAULT);

    public static final Identifier INTERACT_WITH_ASSEMBLY_TABLE = register(
            "interact_with_assembly_table",
            StatFormatter.DEFAULT
    );

    private static Identifier register(String id, StatFormatter formatter) {
        Identifier identifier = Genesis.id(id);
        Registry.register(BuiltInRegistries.CUSTOM_STAT, id, identifier);
        Stats.CUSTOM.get(identifier, formatter);
        return identifier;
    }

    public static void bootstrap() {
        Genesis.bootstrapLog("Stats");
    }
}
