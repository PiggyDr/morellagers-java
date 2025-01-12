package com.piggydragons.morellagers.datagen;

import com.piggydragons.morellagers.registry.MorellagersItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class MorellagersItemModels extends ItemModelProvider {

    public MorellagersItemModels(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
        super(output, modid, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        for (RegistryObject<Item> item : MorellagersItems.ITEMS.getEntries()) {
            if (item.get() instanceof SpawnEggItem) {
                spawnEgg(item.getId());
            }
        }
    }

    private void spawnEgg(ResourceLocation id) {
        this.getBuilder(id.toString()).parent(new ModelFile.ExistingModelFile(new ResourceLocation("minecraft", "item/template_spawn_egg"), existingFileHelper));
    }
}
