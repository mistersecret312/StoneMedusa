package net.mistersecret312.stonemedusa.event;

import net.minecraft.client.renderer.entity.LeashKnotRenderer;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.minecraftforge.fml.common.Mod;
import net.mistersecret312.stonemedusa.capability.GenericProvider;
import net.mistersecret312.stonemedusa.capability.PetrifiedCapability;
import net.mistersecret312.stonemedusa.entity.MedusaProjectile;
import net.mistersecret312.stonemedusa.init.CapabilitiesInit;
import net.mistersecret312.stonemedusa.init.EffectInit;
import net.mistersecret312.stonemedusa.init.ItemInit;
import net.mistersecret312.stonemedusa.item.MedusaItem;
import net.mistersecret312.stonemedusa.item.NitricAcidBottleItem;

import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid = StoneMedusa.MOD_ID)
public class ModEvents
{
    @SubscribeEvent
    public static void chatEvent(ServerChatEvent event)
    {
        String message = event.getRawText();
        ServerPlayer player = event.getPlayer();
        Level level = player.level();

        if(!level.isClientSide() && (message.toLowerCase().contains("meter") || message.toLowerCase().contains("metre")) && message.toLowerCase().contains("second"))
        {
            String[] parts = message.replace("'", "").replace("seconds", "").replace("meter", "-").replace("meters", "-").replace("metre", "-").replace("metres", "-").split("-");
            float meters = Float.parseFloat(parts[0].replaceAll("[^1234567890.]", ""));
            int seconds = Integer.parseInt(parts[1].replaceAll("[^1234567890]", ""))*20;

            if(seconds <= 0)
                seconds = 1;

            if(meters > 0f)
            {
                List<ItemEntity> fallenItems = level.getEntities(EntityType.ITEM, new AABB(player.blockPosition().offset(new Vec3i(-3, -3, -3)), player.blockPosition().offset(new Vec3i(3, 3, 3))), item -> item.getItem().getItem() instanceof MedusaItem);
                List<Player> nearbyPlayers = level.getEntities(EntityType.PLAYER, new AABB(player.blockPosition().offset(new Vec3i(-3, -3, -3)), player.blockPosition().offset(new Vec3i(3, 3, 3))), playerEntity -> playerEntity.getInventory().hasAnyMatching(item -> item.getItem() instanceof MedusaItem));

                for(ItemEntity item : fallenItems)
                {
                    MedusaItem medusa = ((MedusaItem) item.getItem().getItem());
                    medusa.setStartDelay(item.getItem(), seconds);
                    medusa.setDelay(item.getItem(), seconds);
                    medusa.setRadius(item.getItem(), meters);
                    medusa.setCountdownActive(item.getItem(), true);
                }
                for(Player playerEntity : nearbyPlayers)
                {
                    for (ItemStack stack : playerEntity.getInventory().items)
                        if (stack.getItem() instanceof MedusaItem medusa)
                        {
                            medusa.setStartDelay(stack, seconds);
                            medusa.setDelay(stack, seconds);
                            medusa.setRadius(stack, meters);
                            medusa.setCountdownActive(stack, true);
                        }
                }
            }
        }
    }

    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof LivingEntity) {
            event.addCapability(new ResourceLocation(StoneMedusa.MOD_ID, "petrified"), new GenericProvider<>(CapabilitiesInit.PETRIFIED, new PetrifiedCapability()));
        }
    }

    @SubscribeEvent
    public static void livingTick(LivingEvent.LivingTickEvent event)
    {
        event.getEntity().getCapability(CapabilitiesInit.PETRIFIED).ifPresent(cap ->
        {
            cap.tick(event.getEntity().level(), event.getEntity());
        });
    }

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event)
    {
        Level level = event.player.level();
        Player player = event.player;
        Random random = new Random();
        if(level.isClientSide())
            return;

        if(level.getGameTime() % 159999*20 == 0 && random.nextFloat() > 0.75)
        {
            for (int i = 0; i < random.nextInt(2, 5); i++)
            {
                ItemStack stack = MedusaItem.getMedusa(ItemInit.MEDUSA.get(), MedusaItem.maxEnergy, 5f, 20);
                MedusaProjectile medusa = new MedusaProjectile(level, MedusaItem.maxEnergy, 5f, 20, false, false, "", true);
                medusa.setItem(stack);
                medusa.setPos(player.blockPosition().getX()+random.nextInt(-15, 15), player.blockPosition().getY()+350+random.nextInt(-5, 5), player.blockPosition().getZ()+random.nextInt(-15, 15));
                medusa.setDeltaMovement(new Vec3(0, -3f, 0));
                level.addFreshEntity(medusa);
            }
        }
    }

    @SubscribeEvent
    public static void entityHurt(LivingAttackEvent event)
    {
        LivingEntity entity = event.getEntity();
        if(entity.getActiveEffectsMap().containsKey(EffectInit.PETRIFICATION.get()))
        {
            if(event.getAmount() > 5)
            {
                event.getEntity().level().playSound(null, event.getEntity().blockPosition(), SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 1f, 1f);
                entity.getCapability(CapabilitiesInit.PETRIFIED).ifPresent(cap -> cap.setBreakStage(cap.getBreakStage() + 1));
            }
            event.setCanceled(true);
        }
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
        if(event.getTarget() instanceof LivingEntity living)
        {
            ItemStack stack = event.getEntity().getItemInHand(event.getHand());
            if (stack.getItem() instanceof MedusaItem item)
            {
                if (ForgeRegistries.ENTITY_TYPES.containsValue(event.getTarget().getType()))
                {
                    item.setTargetEntityType(stack, ForgeRegistries.ENTITY_TYPES.getKey(event.getTarget().getType()).toString());
                    event.getEntity().displayClientMessage(Component.translatable("stonemedusa.target_type.set").append(Component.literal(item.getTargetEntityType(stack))), true);
                }

            }
            if (stack.getItem() instanceof NitricAcidBottleItem acid)
            {
                living.getCapability(CapabilitiesInit.PETRIFIED).ifPresent(cap ->
                {
                    if (cap.getTimePetrified() < 1200 && living.getActiveEffectsMap().containsKey(EffectInit.PETRIFICATION.get()))
                        living.getActiveEffectsMap().put(EffectInit.PETRIFICATION.get(), new MobEffectInstance(EffectInit.PETRIFICATION.get(), 100, 0, false, false, true));
                    else if(cap.getTimePetrified() > 10000 && living instanceof Player player)
                        player.getActiveEffectsMap().put(EffectInit.PETRIFICATION.get(), new MobEffectInstance(EffectInit.PETRIFICATION.get(), 100, 0, false, false, true));
                });
            }
            if (event.getEntity().getActiveEffectsMap().containsKey(EffectInit.PETRIFICATION.get()))
            {
                event.setCancellationResult(InteractionResult.FAIL);
                event.setCanceled(true);
            }
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
