package com.piggydragons.morellagers.registry;

import com.piggydragons.morellagers.Morellagers;
import com.piggydragons.morellagers.entities.Necrillager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MorellagersEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Morellagers.MOD_ID);

    public static final RegistryObject<EntityType<Necrillager>> NECRILLAGER = ENTITIES.register("necrillager", () -> EntityType.Builder.of(Necrillager::new, MobCategory.MONSTER)
            .sized(0.6f, 1.8f).build("necrillager"));

    private static <T extends AbstractIllager> RegistryObject<EntityType<T>> simpleIllager(String id, EntityType.EntityFactory<T> factory) {
        return ENTITIES.register(id, () -> EntityType.Builder.of(factory, MobCategory.MONSTER)
                .sized(0.6f, 1.8f).build(id));
    }
}
