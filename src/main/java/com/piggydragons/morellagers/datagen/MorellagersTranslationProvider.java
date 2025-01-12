package com.piggydragons.morellagers.datagen;

import com.piggydragons.morellagers.registry.MorellagersEntities;
import com.piggydragons.morellagers.registry.MorellagersItems;
import com.piggydragons.morellagers.registry.MorellagersMobEffects;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.Arrays;

public class MorellagersTranslationProvider extends LanguageProvider {

    public MorellagersTranslationProvider(PackOutput output, String modid, String locale) {
        super(output, modid, locale);
    }

    @Override
    protected void addTranslations() {
        addAllFromRegistries(MorellagersMobEffects.EFFECTS, MorellagersItems.ITEMS, MorellagersEntities.ENTITIES);
    }

    private void addAllFromRegistries(DeferredRegister<?>... registries) {
        Arrays.stream(registries).forEach(r -> r.getEntries().forEach(this::addIfAbsent));
    }

    private void addIfAbsent(RegistryObject<?> object) {
        String key = ((IKeyTranslatable)object.get()).getDescriptionId();
        if (!this.data.containsKey(key)) add(key, nameFromId(object.getId().getPath()));
    }

    private String nameFromId(String id) {
        StringBuilder builder = new StringBuilder();
        for (String segment : id.split("_")) {
            builder.append(segment.substring(0, 1).toUpperCase());
            builder.append(segment.substring(1));
            builder.append(" ");
        }
        builder.deleteCharAt(id.length());
        return builder.toString();
    }
}
