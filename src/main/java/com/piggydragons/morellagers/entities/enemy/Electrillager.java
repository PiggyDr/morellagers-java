package com.piggydragons.morellagers.entities.enemy;

import com.piggydragons.morellagers.Morellagers;
import com.piggydragons.morellagers.MorellagersEvents;
import com.piggydragons.morellagers.entities.ai.MorellagersMoveToTargetGoal;
import com.piggydragons.morellagers.entities.nonliving.SummoningLine;
import com.piggydragons.morellagers.registry.MorellagersEntities;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
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

public class Electrillager extends AbstractIllager implements GeoEntity {

    // constants
    private static final int SPELL_COOLDOWN = 100; // minimum time between casts

    // entity data
    private static final EntityDataAccessor<Boolean> IS_CASTING = SynchedEntityData.defineId(Electrillager.class, EntityDataSerializers.BOOLEAN); // whether the mob is casting, only used for animations
    private int nextSpellTime = 0; // time when cooldown will be over

    // animations
    private static final RawAnimation ANIM_IDLE = RawAnimation.begin().thenLoop("animation.electrillager.idle");
    private static final RawAnimation ANIM_WALK = RawAnimation.begin().thenLoop("animation.electrillager.walk");
    private static final RawAnimation ANIM_CAST = RawAnimation.begin().thenLoop("animation.electrillager.casting");

    // geckolib stuff
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final AnimationController<Electrillager> animController = new AnimationController<>(this, "main", 5, this::selectAnimation);

    public Electrillager(EntityType<? extends Electrillager> type, Level level) {
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
        goalSelector.addGoal(1, new LightningAttackGoal());
        goalSelector.addGoal(2, new MorellagersMoveToTargetGoal(this, 8));
        goalSelector.addGoal(3, new RandomStrollGoal(this, 0.6));

        targetSelector.addGoal(0, new HurtByTargetGoal(this, Raider.class).setAlertOthers());
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true).setUnseenMemoryTicks(600));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true).setUnseenMemoryTicks(300));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }

    public static AttributeSupplier attributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.15)
                .add(Attributes.MAX_HEALTH, 25)
                .add(Attributes.ARMOR, 0)
                .add(Attributes.ATTACK_DAMAGE, 0)
                .add(Attributes.FOLLOW_RANGE, 64)
                .build();
    }

    private boolean canCast() {
        return this.tickCount > this.nextSpellTime;
    }

    private boolean isCasting() {
        return this.entityData.get(IS_CASTING);
    }

    private void setCasting(boolean b) {
        this.entityData.set(IS_CASTING, b);
    }

    @Override
    public void applyRaidBuffs(int idk, boolean idk2) {
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return SoundEvents.EVOKER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
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

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(animController);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    private PlayState selectAnimation(AnimationState<Electrillager> state) {
        if (this.isCasting()) state.setAnimation(ANIM_CAST);
        else if (state.isMoving()) state.setAnimation(ANIM_WALK);
        else state.setAnimation(ANIM_IDLE);
        return PlayState.CONTINUE;
    }

    class LightningAttackGoal extends Goal {

        private static final int CAST_RANGE = 20;
        private static final int CAST_DURATION = 10;

        private final Electrillager mob = Electrillager.this;
        private int castTicks = 0;

        LightningAttackGoal() {
            setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = mob.getTarget();
            return target != null && mob.canCast() && mob.distanceTo(target) < CAST_RANGE; // can cast if cast cooldown is up and mob is within cast range;
        }

        @Override
        public void start() {
            mob.getNavigation().stop();
            mob.setCasting(true); // start animation
            mob.playSound(SoundEvents.EVOKER_PREPARE_ATTACK, 1, 1);
            mob.getLookControl().setLookAt(mob.getTarget());
        }

        @Override
        public void tick() {
            mob.getLookControl().setLookAt(mob.getTarget());
            if (mob.level() instanceof ServerLevel) {
                castTicks++; // increment castTicks until complete
                if (castTicks >= CAST_DURATION) // when complete, finish casting
                    finishCasting();
            }
        }

        private void finishCasting() {
            Vec3 targetDir = mob.getTarget().position().subtract(mob.position());
            SummoningLine line = new SummoningLine(MorellagersEntities.SUMMONING_LINE.get(), mob.level(), new Vec2((float) targetDir.x(), (float) targetDir.z()),
                    30, 4, 20, mob, SummoningLine.MinionType.LIGHTNING_BOLT, null, null);
            line.setPos(mob.position());
            mob.level().addFreshEntity(line);
            mob.playSound(SoundEvents.EVOKER_CAST_SPELL); // play cast sound

            castTicks = 0; // reset castTicks for next cast
            nextSpellTime = mob.tickCount + SPELL_COOLDOWN; // calculate time when cooldown will be over
            this.stop(); // stop the goal since it is no longer needed
        }

        @Override
        public void stop() {
            mob.setCasting(false); // stop animation if not using goal
        }
    }
}
