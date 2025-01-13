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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
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

    public enum MinionType {
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
        });

        private Function<SummoningLine, ? extends Mob> factory;

        MinionType(Function<SummoningLine, ? extends Mob> factory) {
            this.factory = factory;
        }

        public Mob create(SummoningLine summoner) {
            return factory.apply(summoner);
        }
    }

    private Vec2 summonOffset = new Vec2(0,0);
    private int remainingSummons = 0;
    private int ticksPerSummon = 1;
    private @Nullable UUID owner = null;
    private MinionType minionType = MinionType.ZOMBIE;
    private long spawnTimestamp;
    private SoundEvent sound;
    private ParticleOptions particle;

    public SummoningLine(EntityType<? extends SummoningLine> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public SummoningLine(EntityType<? extends SummoningLine> type, Level level, Vec2 dir, int duration, int minionCount, int range, Entity owner, MinionType minionType, SoundEvent sound, ParticleOptions particle) {
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
        this.sound = MorellagersNbtUtils.readRegistryId(nbt.getString("sound"), BuiltInRegistries.SOUND_EVENT);
        try {
            this.particle = ParticleArgument.readParticle(new StringReader(nbt.getString("particle")), BuiltInRegistries.PARTICLE_TYPE.asLookup());
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
        nbt.putString("sound", MorellagersNbtUtils.writeRegistryId(sound, BuiltInRegistries.SOUND_EVENT));
        nbt.putString("particle", particle.writeToString());
    }

    @Override
    public void tick() {
        super.tick();

//        Morellagers.LOGGER.debug("ticking SummoningLine");
        if (this.level() instanceof ServerLevel) {
            int lifetime = (int) (this.level().getGameTime() - this.spawnTimestamp);
//            Morellagers.LOGGER.debug("lifetime: {}", lifetime);
            if (lifetime % ticksPerSummon == 0) {
//                Morellagers.LOGGER.info("spawned minion");
                this.move(MoverType.SELF, new Vec3(summonOffset.x, 0, summonOffset.y));
                Mob minion = this.minionType.create(this);
                minion.setPos(getX(), level().getHeightmapPos(Heightmap.Types.WORLD_SURFACE, BlockPos.containing(position())).getY(), getZ());
//                Morellagers.LOGGER.debug("moved to {}", this.position());
                minion.getCapability(SummonedMinionCap.SUMMONED_MINION).ifPresent(cap -> {
                    minion.addEffect(new MobEffectInstance(MorellagersMobEffects.EPHEMERAL.get(), 200, 0, false, false));
                    cap.setMinion(true);
                    cap.setSummonerUUID(this.owner);
//                    Morellagers.LOGGER.debug("minioned minion");
                });
                minion.playSound(sound, 0.3F, 1);
                ((ServerLevel)level()).sendParticles(particle, minion.getX(), minion.getY(0.5), minion.getZ(), 3, minion.getBbWidth() * 0.33, minion.getBbHeight() * 0.33, minion.getBbWidth() * 0.33, 0.1);
                this.level().addFreshEntity(minion);
                this.remainingSummons--;
                if (this.remainingSummons == 0) this.discard();
            }
        }
    }
}
