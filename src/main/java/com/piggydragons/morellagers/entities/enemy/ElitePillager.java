package com.piggydragons.morellagers.entities.enemy;

import com.piggydragons.morellagers.Morellagers;
import com.piggydragons.morellagers.util.MorellagersNbtUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ElitePillager extends ArmoredPillager {

    public ElitePillager(EntityType<? extends ArmoredPillager> type, Level level) {
        super(type, level);
    }

    @Override
    public void performCrossbowAttack(LivingEntity self, float powerOrSmth) {
        try {
            CompoundTag crossbowData = self.getMainHandItem().getTag();
            ListTag projectiles = crossbowData.getList("ChargedProjectiles", Tag.TAG_COMPOUND);
            if (projectiles.size() == 1) {
                projectiles.add(MorellagersNbtUtils.copyCompoundTag((CompoundTag) projectiles.get(0)));
            }
        } catch (Exception e) {
            Morellagers.LOGGER.warn("Could not duplicate Elite Pillager's crossbow projectile", e);
        }

        super.performCrossbowAttack(self, powerOrSmth);
    }

    @Override
    public void shootCrossbowProjectile(LivingEntity self, ItemStack itemStack, Projectile projectile, float angle) {
        super.shootCrossbowProjectile(self, itemStack, projectile, angle + 5);
    }

    public static AttributeSupplier attributes() {
        return Pillager.createAttributes()
                .add(Attributes.MAX_HEALTH, 44)
                .build();
    }
}
