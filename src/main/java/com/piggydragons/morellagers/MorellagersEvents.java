package com.piggydragons.morellagers;

import com.piggydragons.morellagers.capability.SummonedMinionCap;
import com.piggydragons.morellagers.entities.ArmoredPillager;
import com.piggydragons.morellagers.entities.ElitePillager;
import com.piggydragons.morellagers.entities.Necrillager;
import com.piggydragons.morellagers.registry.MorellagersEntities;
import com.piggydragons.morellagers.registry.MorellagersItems;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class MorellagersEvents {

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = Morellagers.MOD_ID)
    public static class ModBus {

        @SubscribeEvent
        public static void registerAttributes(EntityAttributeCreationEvent event) {
            event.put(MorellagersEntities.NECRILLAGER.get(), Necrillager.attributes());
            event.put(MorellagersEntities.ARMORED_PILLAGER.get(), ArmoredPillager.attributes());
            event.put(MorellagersEntities.ELITE_PILLAGER.get(), ElitePillager.attributes());
        }

        @SubscribeEvent
        public static void addTabItems(BuildCreativeModeTabContentsEvent event) {
            if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
                event.accept(MorellagersItems.NECRILLAGER_SPAWN_EGG);
                event.accept(MorellagersItems.ARMORED_PILLAGER_SPAWN_EGG);
                event.accept(MorellagersItems.ELITE_PILLAGER_SPAWN_EGG);
            }
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
