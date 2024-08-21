package net.mistersecret312.stonemedusa.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.mistersecret312.stonemedusa.client.layers.StoneRenderLayer;
import net.mistersecret312.stonemedusa.init.EffectInit;

@Mod.EventBusSubscriber(modid = StoneMedusa.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ModEventBusClientEvents
{
    @SubscribeEvent
    public static void dontScroll(ScreenEvent.MouseScrolled.Pre event)
    {
        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.player != null && minecraft.player.getActiveEffectsMap().containsKey(EffectInit.PETRIFICATION.get()))
            event.setCanceled(true);
    }

    @SubscribeEvent
    public static void dontClick(ScreenEvent.MouseButtonPressed.Pre event)
    {
        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.player != null && minecraft.player.getActiveEffectsMap().containsKey(EffectInit.PETRIFICATION.get()))
            event.setCanceled(true);
    }

}
