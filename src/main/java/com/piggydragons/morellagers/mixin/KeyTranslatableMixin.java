package com.piggydragons.morellagers.mixin;

import com.piggydragons.morellagers.datagen.IKeyTranslatable;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({MobEffect.class, Item.class, EntityType.class})
public abstract class KeyTranslatableMixin implements IKeyTranslatable {
}
