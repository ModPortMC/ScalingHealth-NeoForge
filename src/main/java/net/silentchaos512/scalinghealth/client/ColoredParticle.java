package net.silentchaos512.scalinghealth.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.silentchaos512.lib.util.Color;
import net.silentchaos512.lib.util.MathUtils;

public class ColoredParticle extends SingleQuadParticle {
    private static final int[] FRAMES = {0, 1, 2, 3, 2, 1, 0};

    private final SpriteSet sprites;

    public ColoredParticle(ClientLevel level, Color color, double x, double y, double z,
                           double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites.first());
        this.sprites = sprites;
        this.lifetime = 16;
        this.hasPhysics = false;
        this.setColor(color.getRed(), color.getGreen(), color.getBlue());
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.removed) {
            int frame = FRAMES.length * this.age / this.lifetime;
            int frameIndex = FRAMES[MathUtils.clamp(frame, 0, FRAMES.length - 1)];
            this.setSprite(this.sprites.get(frameIndex, 3));
        }
    }

    @Override
    protected Layer getLayer() {
        return Layer.TRANSLUCENT;
    }

    public record Factory(Color color, SpriteSet sprites) implements ParticleProvider<SimpleParticleType> {
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed, RandomSource random) {
            return new ColoredParticle(level, this.color, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
        }
    }
}
