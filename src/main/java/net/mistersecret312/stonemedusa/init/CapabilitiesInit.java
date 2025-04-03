package net.mistersecret312.stonemedusa.init;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.capability.PetrifiedCapability;
import net.mistersecret312.stonemedusa.capability.WorldCapability;

@Mod.EventBusSubscriber(modid = StoneMedusa.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public class CapabilitiesInit
{
    public static final Capability<PetrifiedCapability> PETRIFIED = CapabilityManager.get(new CapabilityToken<>() {});
    public static final Capability<WorldCapability> WORLD = CapabilityManager.get(new CapabilityToken<>() {});

    @SubscribeEvent
    public static void register(RegisterCapabilitiesEvent event)
    {
        event.register(PetrifiedCapability.class);
    }
}

