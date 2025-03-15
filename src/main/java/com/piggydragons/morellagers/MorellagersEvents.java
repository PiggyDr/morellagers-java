package com.piggydragons.morellagers;

import com.piggydragons.morellagers.capability.SummonedMinionCap;
import com.piggydragons.morellagers.datagen.MorellagersItemModels;
import com.piggydragons.morellagers.datagen.MorellagersTranslations;
import com.piggydragons.morellagers.entities.enemy.ArmoredPillager;
import com.piggydragons.morellagers.entities.enemy.ElitePillager;
import com.piggydragons.morellagers.entities.enemy.Necrillager;
import com.piggydragons.morellagers.entities.enemy.Electrillager;
import com.piggydragons.morellagers.registry.MorellagersEntities;
import com.piggydragons.morellagers.registry.MorellagersItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class MorellagersEvents {

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = Morellagers.MOD_ID)
    public static class ModBus {

        @SubscribeEvent
        public static void registerAttributes(EntityAttributeCreationEvent event) {
            event.put(MorellagersEntities.NECRILLAGER.get(), Necrillager.attributes());
            event.put(MorellagersEntities.ARMORED_PILLAGER.get(), ArmoredPillager.attributes());
            event.put(MorellagersEntities.ELITE_PILLAGER.get(), ElitePillager.attributes());
            event.put(MorellagersEntities.ELECTRILLAGER.get(), Electrillager.attributes());
        }

        @SubscribeEvent
        public static void addTabItems(BuildCreativeModeTabContentsEvent event) {
            if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
                event.accept(MorellagersItems.NECRILLAGER_SPAWN_EGG);
                event.accept(MorellagersItems.ARMORED_PILLAGER_SPAWN_EGG);
                event.accept(MorellagersItems.ELITE_PILLAGER_SPAWN_EGG);
                event.accept(MorellagersItems.ELECTRILLAGER_SPAWN_EGG);
            }
        }

        @SubscribeEvent
        public static void runDatagen(GatherDataEvent event) {
            DataGenerator generator = event.getGenerator();
            PackOutput packOutput = generator.getPackOutput();
            ExistingFileHelper efh = event.getExistingFileHelper();

            generator.addProvider(event.includeClient(), (DataProvider.Factory<MorellagersItemModels>) output -> new MorellagersItemModels(packOutput, Morellagers.MOD_ID, efh));
            generator.addProvider(event.includeClient(), (DataProvider.Factory<MorellagersTranslations>) output -> new MorellagersTranslations(packOutput, Morellagers.MOD_ID, "en_us"));
        }

        @SubscribeEvent
        public static void commonSetup(FMLCommonSetupEvent event) {
            event.enqueueWork(() -> {
                MorellagersEntities.registerRaiders();
            });
        }
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = Morellagers.MOD_ID)
    public static class ForgeBus {

        @SubscribeEvent
        public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof Mob) {
                event.addCapability(SummonedMinionCap.ID, new SummonedMinionCap());
            }
        }

        @SubscribeEvent
        public static void onMobTarget(LivingChangeTargetEvent event) {
            if (event.getNewTarget() != null
                    && event.getEntity().getCapability(SummonedMinionCap.SUMMONED_MINION)
                    .filter(SummonedMinionCap::isMinion)
                    .map(cap -> cap.getSummonerUUID() == event.getNewTarget().getUUID()
                        || event.getNewTarget().getCapability(SummonedMinionCap.SUMMONED_MINION)
                            .map(targetCap -> targetCap.getSummonerUUID() == cap.getSummonerUUID())
                            .orElse(false))
                    .orElse(false)) {
                event.setCanceled(true);
            }
        }
    }
}
