package com.piggydragons.morellagers.capability;

import com.piggydragons.morellagers.Morellagers;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@AutoRegisterCapability
public class SummonedMinionCap implements ICapabilitySerializable<CompoundTag> {

    public static final ResourceLocation ID = new ResourceLocation(Morellagers.MOD_ID, "summoned_minion");
    public static final Capability<SummonedMinionCap> SUMMONED_MINION = CapabilityManager.get(new CapabilityToken<>(){});
    private final LazyOptional<SummonedMinionCap> optional = LazyOptional.of(() -> this);

    private boolean isMinion = false;
    private @Nullable UUID summonerUUID = null;

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return SUMMONED_MINION.orEmpty(cap, optional);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putBoolean("is_minion", isMinion);
        if (summonerUUID != null)
            nbt.putUUID("summoner", summonerUUID);

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        isMinion = nbt.getBoolean("is_minion");
        summonerUUID = nbt.hasUUID("summoner") ? nbt.getUUID("summoner") : null;
    }

    public boolean isMinion() {
        return isMinion;
    }

    public void setMinion(boolean b) {
        isMinion = b;
    }

    public @Nullable UUID getSummonerUUID() {
        return summonerUUID;
    }

    public void setSummonerUUID(@Nullable UUID uuid) {
        summonerUUID = uuid;
    }

    public static boolean isMinion(Entity potentialMinion) {
        return potentialMinion.getCapability(SUMMONED_MINION).map(SummonedMinionCap::isMinion).orElse(false);
    }
}
