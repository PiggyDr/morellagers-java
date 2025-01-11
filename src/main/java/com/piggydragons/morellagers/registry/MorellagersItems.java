package com.piggydragons.morellagers.registry;

import com.piggydragons.morellagers.Morellagers;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MorellagersItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Morellagers.MOD_ID);

    private static final int PILLAGER_BACKGROUND = ((SpawnEggItem) Items.PILLAGER_SPAWN_EGG).getColor(0); // for reference
    private static final int PILLAGER_HIGHLIGHT = ((SpawnEggItem) Items.PILLAGER_SPAWN_EGG).getColor(1);
    public static final RegistryObject<ForgeSpawnEggItem> NECRILLAGER_SPAWN_EGG = spawnEgg(MorellagersEntities.NECRILLAGER, PILLAGER_BACKGROUND, 0x512e4f);
    public static final RegistryObject<ForgeSpawnEggItem> ARMORED_PILLAGER_SPAWN_EGG = spawnEgg(MorellagersEntities.ARMORED_PILLAGER, PILLAGER_BACKGROUND, PILLAGER_HIGHLIGHT);
    public static final RegistryObject<ForgeSpawnEggItem> ELITE_PILLAGER_SPAWN_EGG = spawnEgg(MorellagersEntities.ELITE_PILLAGER, PILLAGER_BACKGROUND, PILLAGER_HIGHLIGHT);

    private static <T extends Mob> RegistryObject<ForgeSpawnEggItem> spawnEgg(RegistryObject<EntityType<T>> type, int background, int highlight) {
        return spawnEgg(type, background, highlight, new Item.Properties());
    }

    private static <T extends Mob> RegistryObject<ForgeSpawnEggItem> spawnEgg(RegistryObject<EntityType<T>> type, int background, int highlight, Item.Properties properties) {
        return ITEMS.register(type.getId().getPath() + "_spawn_egg", () -> new ForgeSpawnEggItem(type, background, highlight, properties));
    }
}
