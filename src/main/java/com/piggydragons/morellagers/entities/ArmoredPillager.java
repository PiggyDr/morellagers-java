package com.piggydragons.morellagers.entities;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.level.Level;

import java.util.Arrays;

public class ArmoredPillager extends Pillager {

    public ArmoredPillager(EntityType<? extends ArmoredPillager> type, Level level) {
        super(type, level);
    }

    @Override
    public void die(DamageSource source) {
        if (this.getRandom().nextFloat() < survivalChance()) {
            if (!this.level().isClientSide()) {
                level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ANVIL_DESTROY, SoundSource.HOSTILE, 10, 1);
                Pillager survivor = new Pillager(EntityType.PILLAGER, this.level());
                survivor.setPos(this.position());
                this.getActiveEffects().forEach(survivor::addEffect);
                survivor.setCurrentRaid(this.getCurrentRaid());
                survivor.setCustomName(this.getCustomName());
                level().addFreshEntity(survivor);
                Arrays.stream(EquipmentSlot.values()).forEach(slot -> survivor.setItemSlot(slot, this.getItemBySlot(slot)));
                survivor.setXRot(this.getXRot());
                survivor.setYRot(this.getYRot());
                this.armorDropChances[0] = 0;
                this.armorDropChances[1] = 0;
                this.armorDropChances[2] = 0;
                this.armorDropChances[3] = 0;
                this.handDropChances[0] = 0;
                this.handDropChances[1] = 0;
                super.die(source);
                this.discard();
            }
        } else {
            super.die(source);
        }
    }

    private static float survivalChance() {
        return 0.5F;
    }

    public static AttributeSupplier attributes() {
        return Pillager.createAttributes()
                .add(Attributes.MAX_HEALTH, 34)
                .build();
    }
}
