package com.piggydragons.morellagers;

import com.mojang.logging.LogUtils;
import com.piggydragons.morellagers.registry.MorellagersEntities;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(Morellagers.MOD_ID)
public class Morellagers {
    public static final String MOD_ID = "morellagers";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Morellagers(FMLJavaModLoadingContext context) {
        IEventBus bus = context.getModEventBus();

        MorellagersEntities.ENTITIES.register(bus);
    }
}
