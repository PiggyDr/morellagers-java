package com.piggydragons.morellagers.client.renderer;

import com.piggydragons.morellagers.Morellagers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.registries.RegistryObject;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DefaultedEntityRenderer<T extends Entity & GeoAnimatable> extends GeoEntityRenderer<T> {

    public DefaultedEntityRenderer(EntityRendererProvider.Context renderManager, String path) {
        super(renderManager, new DefaultedEntityGeoModel<>(new ResourceLocation(Morellagers.MOD_ID, path)));
    }

    public static <T extends Entity & GeoAnimatable> void register(EntityRenderersEvent.RegisterRenderers event, RegistryObject<EntityType<T>> type) {
        event.registerEntityRenderer(type.get(), context -> new DefaultedEntityRenderer<>(context, type.getId().getPath()));
    }
}
