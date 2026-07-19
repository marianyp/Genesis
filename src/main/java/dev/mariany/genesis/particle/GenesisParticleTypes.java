package dev.mariany.genesis.particle;

import dev.mariany.genesis.Genesis;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

public class GenesisParticleTypes {
    public static final SimpleParticleType SOLACE = register("solace", false);

    private static SimpleParticleType register(String id, boolean alwaysSpawn) {
        return Registry.register(
                BuiltInRegistries.PARTICLE_TYPE,
                Genesis.id(id),
                FabricParticleTypes.simple(alwaysSpawn)
        );
    }

    public static void bootstrap() {
        Genesis.bootstrapLog("Particle Types");
    }
}
