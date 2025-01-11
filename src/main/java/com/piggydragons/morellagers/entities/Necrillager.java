package com.piggydragons.morellagers.entities;

import com.piggydragons.morellagers.capability.SummonedMinionCap;
import com.piggydragons.morellagers.registry.MorellagersMobEffects;
import com.piggydragons.morellagers.util.MorellagersParticleUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;

public class Necrillager extends AbstractIllager implements GeoEntity {

    // constants
    private static final int SPELL_COOLDOWN = 300; // minimum time between casts

    // animations
    private static final RawAnimation ANIM_IDLE = RawAnimation.begin().thenLoop("animation.necrillager.idle");
    private static final RawAnimation ANIM_WALK = RawAnimation.begin().thenLoop("animation.necrillager.walk");
    private static final RawAnimation ANIM_CAST = RawAnimation.begin().thenLoop("animation.necrillager.casting");

    // geckolib stuff
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final AnimationController<Necrillager> animController = new AnimationController<>(this, "main", 5, this::selectAnimation);

    // entity data
    private static final EntityDataAccessor<Boolean> IS_CASTING = SynchedEntityData.defineId(Necrillager.class, EntityDataSerializers.BOOLEAN); // whether the mob is casting, only used for animations
    private int nextSpellTime = 0; // time when cooldown will be over

    public Necrillager(EntityType<? extends AbstractIllager> type, Level level) {
        super(type, level);
        this.entityData.set(IS_CASTING, false);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_CASTING, false);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new SummonGoal()); // cast spell if possible
        goalSelector.addGoal(2, new NecrillagerMoveToTargetGoal()); // if cast cooldown is up, move towards target in order to cast spell
        goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Player.class, 12F, 1, 1)); // otherwise flee target, as it is impossible to attack
        goalSelector.addGoal(4, new RandomStrollGoal(this, 0.6D));

        targetSelector.addGoal(0, new HurtByTargetGoal(this, Raider.class).setAlertOthers());
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true).setUnseenMemoryTicks(600));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true).setUnseenMemoryTicks(300));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }

    private boolean canCast() {
        return this.tickCount > this.nextSpellTime;
    }

    private boolean isCasting() {
        return this.entityData.get(IS_CASTING);
    }

    private void setCasting(boolean casting) {
        this.entityData.set(IS_CASTING, casting);
    }

    public static AttributeSupplier attributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.45)
                .add(Attributes.MAX_HEALTH, 25)
                .add(Attributes.ARMOR, 0)
                .add(Attributes.ATTACK_DAMAGE, 0)
                .add(Attributes.FOLLOW_RANGE, 64)
                .build();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registrar.add(animController);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void applyRaidBuffs(int idk, boolean idk2) {
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return SoundEvents.EVOKER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_33034_) {
        return SoundEvents.EVOKER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.EVOKER_DEATH;
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.EVOKER_CELEBRATE;
    }

    private PlayState selectAnimation(AnimationState<Necrillager> state) {
        if (isCasting()) state.setAnimation(ANIM_CAST); // if casting, use cast animation
        else if (state.isMoving()) state.setAnimation(ANIM_WALK); // if walking, use walk animation
        else state.setAnimation(ANIM_IDLE); // otherwise, use idle animation
        return PlayState.CONTINUE; // never stop animations
    }

    @Override
    public void tick() {
        super.tick();
        if (isCasting() && level().isClientSide())
            MorellagersParticleUtils.spawnEntityEffectWithDelta(level(), getX(), getY(0.5), getZ(), 0.45, 0.45, 0.45, 0, 1, (double) 1 / 0x94, 2); // spawn casting particles
    }

    class SummonGoal extends Goal {

        private static final int CAST_RANGE = 8;
        private static final int CAST_DURATION = 40;

        private final Necrillager mob = Necrillager.this;
        private int castTicks = 0;

        SummonGoal() {
            setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = mob.getTarget();
            return target != null && mob.canCast() && mob.distanceTo(target) < CAST_RANGE; // can cast if cast cooldown is up and mob is within cast range
        }

        @Override
        public void start() {
            mob.getNavigation().stop();
            mob.setCasting(true); // start animation
            mob.playSound(SoundEvents.EVOKER_PREPARE_SUMMON, 1, 1);
            mob.getLookControl().setLookAt(mob.getTarget());
        }

        @Override
        public void tick() {
            if (mob.level() instanceof ServerLevel level) {
                castTicks++; // increment castTicks until complete
                if (castTicks >= CAST_DURATION) // when complete, finish casting
                    finishCasting();
            }
        }

        private void finishCasting() {
            switch (mob.getRandom().nextInt(3)) {
                case 0:
                    summon(EntityType.SKELETON, Items.BOW, mob.getX() - 3, mob.getZ());
                    summon(EntityType.SKELETON, Items.BOW, mob.getX() + 3, mob.getZ());
                    summon(EntityType.SKELETON, Items.BOW, mob.getX(), mob.getZ() - 3);
                    summon(EntityType.SKELETON, Items.BOW, mob.getX(), mob.getZ() + 3);
                    break;
                case 1:
                    summon(EntityType.WITHER_SKELETON, Items.STONE_SWORD, mob.getX() - 3, mob.getZ());
                    summon(EntityType.WITHER_SKELETON, Items.STONE_SWORD, mob.getX() + 3, mob.getZ());
                    summon(EntityType.WITHER_SKELETON, Items.STONE_SWORD, mob.getX(), mob.getZ() - 3);
                    summon(EntityType.WITHER_SKELETON, Items.STONE_SWORD, mob.getX(), mob.getZ() + 3);

                    summon(EntityType.WITHER_SKELETON, Items.STONE_SWORD, mob.getX() + 2.2, mob.getZ() + 2.2);
                    summon(EntityType.WITHER_SKELETON, Items.STONE_SWORD, mob.getX() - 2.2, mob.getZ() + 2.2);
                    summon(EntityType.WITHER_SKELETON, Items.STONE_SWORD, mob.getX() + 2.2, mob.getZ() - 2.2);
                    summon(EntityType.WITHER_SKELETON, Items.STONE_SWORD, mob.getX() - 2.2, mob.getZ() - 2.2);
                    break;
                case 2:
                    Vec3 targetDir = mob.position().subtract(mob.getTarget().position()).normalize();
                    Vec2 targetDir2d = new Vec2((float) targetDir.x(), (float) targetDir.z());

                    Vec2 lineDir = new Vec2(-targetDir2d.y, targetDir2d.x);
                    Vec2 lineStart = targetDir2d.scale((float) (mob.position().distanceTo(mob.getTarget().position()) / -2))
                            .add(new Vec2((float) mob.getX(), (float) mob.getZ())).add(lineDir.scale(-3.5F));
                    for (int i = 0; i < 8; i++) {
                        Vec2 summonPos = lineStart.add(lineDir.scale(i));
                        summon(EntityType.ZOMBIE, Items.AIR, summonPos.x, summonPos.y);
                    }
            }
            mob.playSound(SoundEvents.EVOKER_CAST_SPELL); // play cast sound

            castTicks = 0; // reset castTicks for next cast
            nextSpellTime = mob.tickCount + SPELL_COOLDOWN; // calculate time when cooldown will be over
            this.stop(); // stop the goal since it is no longer needed
        }

        private <T extends Mob> void summon(EntityType<T> type, Item handItem, double x, double z) {
            if (mob.level() instanceof ServerLevel level) {
                T minion = type.create(level);
                minion.setPos(x, mob.level().getHeightmapPos(Heightmap.Types.WORLD_SURFACE, new BlockPos((int) Math.floor(x), 0, (int) Math.floor(z))).getY(), z);
                level.sendParticles(ParticleTypes.POOF, minion.getX(), minion.getY(0.5), minion.getZ(), 3, minion.getBbWidth() * 0.33, minion.getBbHeight() * 0.33, minion.getBbWidth() * 0.33, 0.1);
                minion.playSound(SoundEvents.BONE_BLOCK_PLACE, 0.3F, 1);
                minion.addEffect(new MobEffectInstance(MorellagersMobEffects.EPHEMERAL.get(), 200, 0, false, false));
                minion.setItemInHand(minion.getUsedItemHand(), handItem.getDefaultInstance());
                minion.getCapability(SummonedMinionCap.SUMMONED_MINION).ifPresent(cap -> {
                    cap.setMinion(true);
                    cap.setSummonerUUID(mob.getUUID());
                });
                level.addFreshEntity(minion);
            }
        }

        @Override
        public void stop() {
            mob.setCasting(false); // stop animation if not using goal
        }
    }

    class NecrillagerMoveToTargetGoal extends Goal {

        private static final int WANTED_RANGE_SQR = 36;
        private final Necrillager mob = Necrillager.this;

        @Override
        public boolean canUse() {
            return mob.getTarget() != null && mob.canCast() && mob.distanceToSqr(mob.getTarget()) > WANTED_RANGE_SQR;
        }

        @Override
        public void tick() {
            mob.getNavigation().moveTo(mob.getTarget(), 1);
            if (mob.distanceToSqr(mob.getTarget()) < WANTED_RANGE_SQR) stop();
        }
    }
}
