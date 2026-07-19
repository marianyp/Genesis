package dev.mariany.genesis.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class SolaceParticle extends SingleQuadParticle {
    private SolaceParticle(
            ClientLevel level,
            double x,
            double y,
            double z,
            TextureAtlasSprite sprite
    ) {
        super(level, x, y, z, 0, 0, 0, sprite);

        this.speedUpWhenYMotionIsBlocked = true;
        this.friction = 0.86F;
        this.xd *= 0.01F;
        this.yd *= 0.01F;
        this.zd *= 0.01F;
        this.yd += 0.1;
        this.quadSize *= 0.5F;
        this.lifetime = 16;
        this.hasPhysics = false;
    }

    @Override
    public Layer getLayer() {
        return Layer.OPAQUE;
    }

    @Override
    public float getQuadSize(final float a) {
        return this.quadSize * Mth.clamp((this.age + a) / this.lifetime * 32, 0, 1);
    }

    @Environment(EnvType.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(final SpriteSet sprite) {
            this.sprite = sprite;
        }

        public Particle createParticle(
                SimpleParticleType options,
                ClientLevel level,
                double x,
                double y,
                double z,
                double xAux,
                double yAux,
                double zAux,
                RandomSource random
        ) {
            return new SolaceParticle(level, x, y, z, this.sprite.get(random));
        }
    }
}