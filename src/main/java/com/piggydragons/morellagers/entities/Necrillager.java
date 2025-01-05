package com.piggydragons.morellagers.entities;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.level.Level;

public class Necrillager extends AbstractIllager {

    public Necrillager(EntityType<? extends AbstractIllager> type, Level level) {
        super(type, level);
    }

    @Override
    public void applyRaidBuffs(int idk, boolean idk2) {
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return null; // TODO add something
    }

    public static AttributeSupplier attributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.MAX_HEALTH, 25)
                .add(Attributes.ARMOR, 0)
                .add(Attributes.ATTACK_DAMAGE, 0)
                .add(Attributes.FOLLOW_RANGE, 64)
                .build();
    }
}
