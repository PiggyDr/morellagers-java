package com.piggydragons.morellagers.registry;

import com.piggydragons.morellagers.Morellagers;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MorellagersItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Morellagers.MOD_ID);

    public static final RegistryObject<ForgeSpawnEggItem> NECRILLAGER_SPAWN_EGG = ITEMS.register("necrillager_spawn_egg", () -> new ForgeSpawnEggItem(MorellagersEntities.NECRILLAGER, 0x353538, 0x0c31b4, new Item.Properties()));
}
