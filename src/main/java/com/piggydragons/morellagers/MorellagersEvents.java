package com.piggydragons.morellagers;

import com.piggydragons.morellagers.client.renderer.DefaultedEntityRenderer;
import com.piggydragons.morellagers.entities.Necrillager;
import com.piggydragons.morellagers.registry.MorellagersEntities;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class MorellagersEvents {

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = Morellagers.MOD_ID)
    public static class ModBus {

        @SubscribeEvent
        public static void registerAttributes(EntityAttributeCreationEvent event) {
            event.put(MorellagersEntities.NECRILLAGER.get(), Necrillager.attributes());
        }

        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            DefaultedEntityRenderer.register(event, MorellagersEntities.NECRILLAGER);
        }
    }
}
