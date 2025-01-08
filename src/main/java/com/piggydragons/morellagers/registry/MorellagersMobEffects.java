package com.piggydragons.morellagers.registry;

import com.piggydragons.morellagers.Morellagers;
import com.piggydragons.morellagers.effects.EphemeralEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MorellagersMobEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Morellagers.MOD_ID);

    public static final RegistryObject<MobEffect> EPHEMERAL = EFFECTS.register("ephemeral", () -> new EphemeralEffect(MobEffectCategory.HARMFUL, 0xBBD1CE));
}
