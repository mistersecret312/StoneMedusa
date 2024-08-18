package net.mistersecret312.stonemedusa.event;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.minecraftforge.fml.common.Mod;
import net.mistersecret312.stonemedusa.init.EffectInit;

@Mod.EventBusSubscriber(modid = StoneMedusa.MOD_ID)
public class ModEvents
{

    @SubscribeEvent
    public static void entityHurt(LivingHurtEvent event)
    {
        if(event.getEntity().getActiveEffectsMap().containsKey(EffectInit.PETRIFICATION.get()))
            event.setCanceled(true);
    }

    @SubscribeEvent
    public static void playerBreakBlock(PlayerEvent.BreakSpeed event)
    {
        if(event.getEntity().getActiveEffectsMap().containsKey(EffectInit.PETRIFICATION.get()))
            event.setNewSpeed(0);
    }

    @SubscribeEvent
    public static void playerInteract(PlayerInteractEvent.EntityInteractSpecific event)
    {
        if(event.getEntity().getActiveEffectsMap().containsKey(EffectInit.PETRIFICATION.get()))
        {
            event.setCancellationResult(InteractionResult.FAIL);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void playerInteract(PlayerInteractEvent.LeftClickBlock event)
    {
        if(event.getEntity().getActiveEffectsMap().containsKey(EffectInit.PETRIFICATION.get()))
            event.setCanceled(true);

    }

    @SubscribeEvent
    public static void playerInteract(PlayerInteractEvent.RightClickBlock event)
    {
        if(event.getEntity().getActiveEffectsMap().containsKey(EffectInit.PETRIFICATION.get()))
            event.setCanceled(true);

    }

    @SubscribeEvent
    public static void playerInteract(PlayerInteractEvent.RightClickItem event)
    {
        if(event.getEntity().getActiveEffectsMap().containsKey(EffectInit.PETRIFICATION.get()))
            event.setCanceled(true);

    }

    @SubscribeEvent
    public static void playerAttack(AttackEntityEvent event)
    {
        if(event.getEntity().getActiveEffectsMap().containsKey(EffectInit.PETRIFICATION.get()))
            event.setCanceled(true);
    }

    @SubscribeEvent
    public static void playerDontPickUp(EntityItemPickupEvent event)
    {
        if(event.getEntity().getActiveEffectsMap().containsKey(EffectInit.PETRIFICATION.get()))
            event.setCanceled(true);
    }

    @SubscribeEvent
    public static void playerDontToss(ItemTossEvent event)
    {
        if(event.getPlayer().getActiveEffectsMap().containsKey(EffectInit.PETRIFICATION.get()))
            event.setCanceled(true);
    }

    public static void entityOnLightning(EntityStruckByLightningEvent event)
    {
        if(event.getEntity() instanceof LivingEntity living)
            if(living.getActiveEffectsMap().containsKey(EffectInit.PETRIFICATION.get()))
                event.setCanceled(true);
    }


}
