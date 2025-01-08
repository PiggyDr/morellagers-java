package com.piggydragons.morellagers.util;

import com.piggydragons.morellagers.Morellagers;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

public class ParticleUtils {

    public static void spawnEntityEffectWithDelta(Level level, double x, double y, double z, double dx, double dy, double dz, double r, double g, double b, int count) {
        if (!level.isClientSide())
            throw new IllegalArgumentException("Level must be client-side");

        RandomSource random = level.getRandom();
        for (int i = 0; i < count; i++) {
            level.addParticle(
                    ParticleTypes.ENTITY_EFFECT,
                    x + dx * random.nextGaussian(),
                    y + dy * random.nextGaussian(),
                    z + dz * random.nextGaussian(),
                    r, g, b
            );
        }
    }
}
