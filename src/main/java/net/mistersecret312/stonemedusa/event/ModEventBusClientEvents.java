package net.mistersecret312.stonemedusa.event;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.mistersecret312.stonemedusa.init.EffectInit;

@Mod.EventBusSubscriber(modid = StoneMedusa.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ModEventBusClientEvents
{
    @SubscribeEvent
    public static void dontScroll(InputEvent.MouseScrollingEvent event)
    {
        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.player != null && minecraft.player.getActiveEffectsMap().containsKey(EffectInit.PETRIFICATION.get()))
            event.setCanceled(true);
    }

    @SubscribeEvent
    public static void dontClick(InputEvent.MouseButton.Pre event)
    {
        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.player != null && minecraft.player.getActiveEffectsMap().containsKey(EffectInit.PETRIFICATION.get()))
            event.setCanceled(true);
    }

    @SubscribeEvent
    public static void dontPress(InputEvent.Key.InteractionKeyMappingTriggered event)
    {
        Minecraft minecraft = Minecraft.getInstance();
        if(!event.getKeyMapping().getKey().equals(InputConstants.Type.KEYSYM.getOrCreate(InputConstants.KEY_ESCAPE)) && minecraft.player != null && minecraft.player.getActiveEffectsMap().containsKey(EffectInit.PETRIFICATION.get()))
            event.setCanceled(true);
    }

}
