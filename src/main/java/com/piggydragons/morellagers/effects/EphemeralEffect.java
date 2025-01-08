package com.piggydragons.morellagers.effects;

import com.piggydragons.morellagers.capability.SummonedMinionCap;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

public class EphemeralEffect extends MobEffect {

    public EphemeralEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributes, int idk) {
        super.removeAttributeModifiers(entity, attributes, idk);

        if (SummonedMinionCap.isMinion(entity) && entity.level() instanceof ServerLevel level) {
            level.sendParticles(ParticleTypes.POOF, entity.getX(), entity.getY(0.5), entity.getZ(), 3, entity.getBbWidth() * 0.33, entity.getBbHeight() * 0.33, entity.getBbWidth() * 0.33, 0.1);
            level.playSound(null, entity, SoundEvents.INK_SAC_USE, SoundSource.HOSTILE, 2F, 1F);
            entity.discard();
        }
    }
}
