package com.piggydragons.morellagers.client;

import com.piggydragons.morellagers.Morellagers;
import com.piggydragons.morellagers.client.model.ArmoredPillagerModel;
import com.piggydragons.morellagers.client.renderer.ArmoredPillagerRenderer;
import com.piggydragons.morellagers.client.renderer.DefaultedEntityRenderer;
import com.piggydragons.morellagers.registry.MorellagersEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class MorellagersClientEvents {

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = Morellagers.MOD_ID, value = Dist.CLIENT)
    public static class ModEvents {

        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            DefaultedEntityRenderer.register(event, MorellagersEntities.NECRILLAGER);

            event.registerEntityRenderer(MorellagersEntities.ARMORED_PILLAGER.get(), ArmoredPillagerRenderer.provider("textures/entity/armored_pillager.png"));
            event.registerEntityRenderer(MorellagersEntities.ELITE_PILLAGER.get(), ArmoredPillagerRenderer.provider("textures/entity/elite_pillager.png"));
        }

        @SubscribeEvent
        public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(ArmoredPillagerModel.LAYER_LOCATION, ArmoredPillagerModel::createBodyLayer);
        }
    }
}
