package com.piggydragons.morellagers.util;

import net.minecraft.nbt.CompoundTag;

public class MorellagersNbtUtils {

    public static CompoundTag copyCompoundTag(CompoundTag original) {
        CompoundTag newTag = new CompoundTag();
        original.getAllKeys().forEach(key -> newTag.put(key, original.get(key)));
        return newTag;
    }
}
