package com.piggydragons.morellagers.entities.nonliving;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.piggydragons.morellagers.Morellagers;
import com.piggydragons.morellagers.capability.SummonedMinionCap;
import com.piggydragons.morellagers.registry.MorellagersItems;
import com.piggydragons.morellagers.registry.MorellagersMobEffects;
import com.piggydragons.morellagers.util.MorellagersNbtUtils;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public class SummoningLine extends Entity {

    public enum MinionType { // TODO make this a registry instead
        ZOMBIE(line -> new Zombie(EntityType.ZOMBIE, line.level())),
        PILLAGER(line -> {
            Pillager minion = new Pillager(EntityType.PILLAGER, line.level());
            minion.setItemInHand(minion.getUsedItemHand(), Items.CROSSBOW.getDefaultInstance());
            line.getOwner().filter(owner -> owner instanceof Raider).ifPresent(owner -> minion.setCurrentRaid(((Raider)owner).getCurrentRaid()));
            return minion;
        }),
        VINDICATOR(line -> {
            Vindicator minion = new Vindicator(EntityType.VINDICATOR, line.level());
            minion.setItemInHand(minion.getUsedItemHand(), Items.IRON_AXE.getDefaultInstance());
            line.getOwner().filter(owner -> owner instanceof Raider).ifPresent(owner -> minion.setCurrentRaid(((Raider)owner).getCurrentRaid()));
            return minion;
        }),
        LIGHTNING_BOLT(line -> new LightningBolt(EntityType.LIGHTNING_BOLT, line.level()));

        private Function<SummoningLine, ? extends Entity> factory;

        MinionType(Function<SummoningLine, ? extends Entity> factory) {
            this.factory = factory;
        }

        public Entity create(SummoningLine summoner) {
            return factory.apply(summoner);
        }
    }

    private Vec2 summonOffset = new Vec2(0,0);
    private int remainingSummons = 0;
    private int ticksPerSummon = 1;
    private @Nullable UUID owner = null;
    private MinionType minionType = MinionType.ZOMBIE;
    private long spawnTimestamp;
    private @Nullable SoundEvent sound = null;
    private @Nullable ParticleOptions particle = null;

    public SummoningLine(EntityType<? extends SummoningLine> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public SummoningLine(
            EntityType<? extends SummoningLine> type,
            Level level,
            Vec2 dir,
            int duration,
            int minionCount,
            int range,
            Entity owner,
            MinionType minionType,
            @Nullable SoundEvent sound,
            @Nullable ParticleOptions particle
    ) {
        this(type, level);

        this.summonOffset = dir.normalized().scale((float) range / minionCount);
        this.remainingSummons = minionCount;
        this.ticksPerSummon = duration / minionCount;

        this.owner = owner.getUUID();
        this.minionType = minionType;
        this.sound = sound;
        this.particle = particle;

        this.spawnTimestamp = this.level().getGameTime();

//        Morellagers.LOGGER.debug("created SummoningLine {} {} {} {} {} {} {} {} {}", summonOffset.x, summonOffset.y, remainingSummons, ticksPerSummon, owner, minionType, sound, particle, spawnTimestamp);
    }

    public Optional<Entity> getOwner() {
        if (owner == null) return Optional.empty();
        return Optional.ofNullable(this.level().getEntities().get(owner));
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        this.summonOffset = new Vec2(nbt.getFloat("offset_x"), nbt.getFloat("offset_y"));
        this.remainingSummons = nbt.getInt("remaining_summons");
        this.ticksPerSummon = nbt.getInt("ticks_per_summon");
        if (nbt.contains("owner")) this.owner = nbt.getUUID("owner");
        else this.owner = null;
        this.minionType = MinionType.valueOf(nbt.getString("minion_type"));
        this.spawnTimestamp = nbt.getLong("spawn_timestamp");
        if (nbt.contains("sound")) this.sound = MorellagersNbtUtils.readRegistryId(nbt.getString("sound"), BuiltInRegistries.SOUND_EVENT);
        try {
            if (nbt.contains("particle")) this.particle = ParticleArgument.readParticle(new StringReader(nbt.getString("particle")), BuiltInRegistries.PARTICLE_TYPE.asLookup());
        } catch (CommandSyntaxException e) {
            this.particle = ParticleTypes.POOF;
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        nbt.putFloat("offset_x", summonOffset.x);
        nbt.putFloat("offset_y", summonOffset.y);
        nbt.putInt("remaining_summons", remainingSummons);
        nbt.putInt("ticks_per_summon", ticksPerSummon);
        if (owner != null) nbt.putUUID("owner", owner);
        nbt.putString("minion_type", minionType.name());
        nbt.putLong("spawn_timestamp", spawnTimestamp);
        if (sound != null) nbt.putString("sound", MorellagersNbtUtils.writeRegistryId(sound, BuiltInRegistries.SOUND_EVENT));
        if (particle != null) nbt.putString("particle", particle.writeToString());
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level() instanceof ServerLevel) {
            int lifetime = (int) (this.level().getGameTime() - this.spawnTimestamp);
            if (lifetime % ticksPerSummon == 0) {
                this.move(MoverType.SELF, new Vec3(summonOffset.x, 0, summonOffset.y));
                Entity minion = this.minionType.create(this);
                minion.setPos(getX(), level().getHeightmapPos(Heightmap.Types.WORLD_SURFACE, BlockPos.containing(position())).getY(), getZ());
                minion.getCapability(SummonedMinionCap.SUMMONED_MINION).ifPresent(cap -> {
                    if (minion instanceof LivingEntity)
                        ((LivingEntity) minion).addEffect(new MobEffectInstance(MorellagersMobEffects.EPHEMERAL.get(), 200, 0, false, false));
                    cap.setMinion(true);
                    cap.setSummonerUUID(this.owner);
                });
                if (sound != null) minion.playSound(sound, 0.3F, 1);
                if (particle != null) ((ServerLevel)level()).sendParticles(particle, minion.getX(), minion.getY(0.5), minion.getZ(), 3, minion.getBbWidth() * 0.33, minion.getBbHeight() * 0.33, minion.getBbWidth() * 0.33, 0.1);
                this.level().addFreshEntity(minion);
                this.remainingSummons--;
                if (this.remainingSummons == 0) this.discard();
            }
        }
    }
}
