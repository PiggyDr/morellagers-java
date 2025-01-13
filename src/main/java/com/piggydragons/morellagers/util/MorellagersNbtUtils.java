package com.piggydragons.morellagers.util;

import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class MorellagersNbtUtils {

    public static CompoundTag copyCompoundTag(CompoundTag original) {
        CompoundTag newTag = new CompoundTag();
        original.getAllKeys().forEach(key -> newTag.put(key, original.get(key)));
        return newTag;
    }

    public static <T> T readRegistryId(String id, Registry<T> registry) {
        return registry.get(new ResourceLocation(id));
    }

    public static <T> String writeRegistryId(T t, Registry<T> registry) {
        return registry.getKey(t).toString();
    }
}
