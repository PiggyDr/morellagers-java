package com.piggydragons.morellagers.client.renderer;

import com.piggydragons.morellagers.Morellagers;
import com.piggydragons.morellagers.entities.Necrillager;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class NecrillagerRenderer extends GeoEntityRenderer<Necrillager> {

    public NecrillagerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedEntityGeoModel<>(new ResourceLocation(Morellagers.MOD_ID, "necrillager")));
    }
}
