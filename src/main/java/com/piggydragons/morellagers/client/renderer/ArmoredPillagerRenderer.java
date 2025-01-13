package com.piggydragons.morellagers.client.renderer;

import com.piggydragons.morellagers.Morellagers;
import com.piggydragons.morellagers.client.model.ArmoredPillagerModel;
import com.piggydragons.morellagers.entities.enemy.ArmoredPillager;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

public class ArmoredPillagerRenderer<T extends ArmoredPillager> extends MobRenderer<T, ArmoredPillagerModel<T>> {

    private final ResourceLocation texture;

    public ArmoredPillagerRenderer(EntityRendererProvider.Context context, String texture) {
        super(context, new ArmoredPillagerModel<>(context.bakeLayer(ArmoredPillagerModel.LAYER_LOCATION)), 0.5F);
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
        this.addLayer(new CustomHeadLayer<>(this, context.getModelSet(), context.getItemInHandRenderer()));
        this.texture = new ResourceLocation(Morellagers.MOD_ID, texture);
    }

    public static <T extends ArmoredPillager> EntityRendererProvider<T> provider(String texture) {
        return context -> new ArmoredPillagerRenderer<T>(context, texture);
    }

    @Override
    public ResourceLocation getTextureLocation(T t) {
        return texture;
    }
}
