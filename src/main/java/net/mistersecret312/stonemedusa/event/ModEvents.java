package net.mistersecret312.stonemedusa.event;

import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.registries.ForgeRegistries;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.minecraftforge.fml.common.Mod;
import net.mistersecret312.stonemedusa.capability.GenericProvider;
import net.mistersecret312.stonemedusa.capability.PetrifiedCapability;
import net.mistersecret312.stonemedusa.capability.WorldCapability;
import net.mistersecret312.stonemedusa.config.MedusaConfig;
import net.mistersecret312.stonemedusa.config.PetrificationConfig;
import net.mistersecret312.stonemedusa.config.RevivalConfig;
import net.mistersecret312.stonemedusa.damagesource.PetrificationDamageSource;
import net.mistersecret312.stonemedusa.entity.MedusaProjectile;
import net.mistersecret312.stonemedusa.init.*;
import net.mistersecret312.stonemedusa.item.MedusaItem;
import net.mistersecret312.stonemedusa.item.NitricAcidBottleItem;

import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid = StoneMedusa.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
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
            float meters = (float) (Float.parseFloat(parts[0].replaceAll("[^1234567890.]", ""))/1.5);
            int seconds = Integer.parseInt(parts[1].replaceAll("[^1234567890]", ""))*20;

            if(seconds <= 0)
                seconds = 1;

            if(meters > 0f)
            {
                if(player.getMainHandItem().getItem() instanceof MedusaItem medusa)
                {
                    if(medusa.getEnergy(player.getMainHandItem()) != 0)
                    {
                        medusa.setStartDelay(player.getMainHandItem(), seconds);
                        medusa.setDelay(player.getMainHandItem(), seconds);
                        medusa.setRadius(player.getMainHandItem(), meters);

                        medusa.setCountdownActive(player.getMainHandItem(), true);
                    }
                }
                else
                if(player.getOffhandItem().getItem() instanceof MedusaItem medusa)
                {
                    if(medusa.getEnergy(player.getOffhandItem()) != 0)
                    {
                        medusa.setStartDelay(player.getOffhandItem(), seconds);
                        medusa.setDelay(player.getOffhandItem(), seconds);
                        medusa.setRadius(player.getOffhandItem(), meters);

                        medusa.setCountdownActive(player.getOffhandItem(), true);
                    }
                }
                else
                {
                    List<ItemEntity> fallenItems = level.getEntities(EntityType.ITEM, new AABB(player.blockPosition().offset(new Vec3i(-3, -3, -3)), player.blockPosition().offset(new Vec3i(3, 3, 3))), item -> item.getItem().getItem() instanceof MedusaItem);
                    List<Player> nearbyPlayers = level.getEntities(EntityType.PLAYER, new AABB(player.blockPosition().offset(new Vec3i(-3, -3, -3)), player.blockPosition().offset(new Vec3i(3, 3, 3))), playerEntity -> playerEntity.getInventory().hasAnyMatching(item -> item.getItem() instanceof MedusaItem));
                    List<MedusaProjectile> nearbyMedusa = level.getEntities(EntityInit.MEDUSA.get(), new AABB(player.blockPosition().offset(new Vec3i(-3, -3, -3)), player.blockPosition().offset(new Vec3i(3, 3, 3))), medusaProjectile -> !medusaProjectile.isCountingDown());

                    for (ItemEntity item : fallenItems)
                    {
                        MedusaItem medusa = ((MedusaItem) item.getItem().getItem());
                        if (medusa.getEnergy(item.getItem()) == 0) continue;
                        medusa.setStartDelay(item.getItem(), seconds);
                        medusa.setDelay(item.getItem(), seconds);
                        medusa.setRadius(item.getItem(), meters);
                        medusa.setCountdownActive(item.getItem(), true);
                    }

                    for (Player playerEntity : nearbyPlayers)
                    {
                        for (ItemStack stack : playerEntity.getInventory().items)
                            if (stack.getItem() instanceof MedusaItem medusa)
                            {
                                if (medusa.getEnergy(stack) == 0) continue;
                                medusa.setStartDelay(stack, seconds);
                                medusa.setDelay(stack, seconds);
                                medusa.setRadius(stack, meters);
                                medusa.setCountdownActive(stack, true);
                            }
                        for (ItemStack stack : playerEntity.getInventory().offhand)
                            if (stack.getItem() instanceof MedusaItem medusa)
                            {
                                if (medusa.getEnergy(stack) == 0) continue;
                                medusa.setStartDelay(stack, seconds);
                                medusa.setDelay(stack, seconds);
                                medusa.setRadius(stack, meters);
                                medusa.setCountdownActive(stack, true);
                            }
                    }

                    for (MedusaProjectile medusa : nearbyMedusa)
                    {
                        if (medusa.getEnergy() == 0) continue;
                        medusa.setFading(0f);
                        medusa.setCurrentRadius(0f);
                        medusa.setDelay(seconds);
                        medusa.setTargetRadius(meters);
                        medusa.setCountingDown(true);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void effectClearing(MobEffectEvent.Remove event)
    {
        if(event.getEffect().equals(EffectInit.PETRIFICATION.get()))
        {
            event.getEntity().getCapability(CapabilitiesInit.PETRIFIED).ifPresent(cap -> cap.setPetrified(false));
        }
    }

    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof LivingEntity) {
            event.addCapability(new ResourceLocation(StoneMedusa.MOD_ID, "petrified"), new GenericProvider<>(CapabilitiesInit.PETRIFIED, new PetrifiedCapability()));
        }
    }

    @SubscribeEvent
    public static void attachWorldCapabilities(AttachCapabilitiesEvent<Level> event)
    {
        event.addCapability(new ResourceLocation(StoneMedusa.MOD_ID, "stone_world"), new GenericProvider<>(CapabilitiesInit.WORLD, new WorldCapability()));
    }

    @SubscribeEvent
    public static void levelTick(TickEvent.LevelTickEvent event)
    {
        if(event.side == LogicalSide.CLIENT)
            return;
        if(event.type == TickEvent.Type.CLIENT)
            return;

        event.level.getCapability(CapabilitiesInit.WORLD).ifPresent(cap ->
        {
            cap.tick(event.level);
        });
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
    public static void entityHurt(LivingAttackEvent event)
    {
        LivingEntity living = event.getEntity();
        Level level = living.level();
        if(level.isClientSide())
            return;

        living.getCapability(CapabilitiesInit.PETRIFIED).ifPresent(cap ->
        {
            if(cap.isPetrified() && !cap.isBroken() && !(event.getSource() instanceof PetrificationDamageSource))
            {
                if(event.getAmount() > 5f && PetrificationConfig.petrified_entity_damage.get())
                    cap.setBreakStage(cap.breakStage+1);

                if(cap.getBreakStage() >= 9 && PetrificationConfig.petrified_entity_destroy.get())
                {
                    living.setInvulnerable(false);
                    living.hurt(PetrificationDamageSource.source(event.getEntity().level(), ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(StoneMedusa.MOD_ID, "petrification"))), Float.MAX_VALUE);
                }

                event.setCanceled(true);
            }
        });

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
                    event.getEntity().displayClientMessage(Component.translatable("stonemedusa.target_type.set").append(Component.translatable(BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.tryParse(item.getTargetEntityType(stack))).getDescriptionId())), true);
                }

            }
            if (RevivalConfig.nitric_revival.get() && stack.getItem() instanceof NitricAcidBottleItem acid)
            {
                living.getCapability(CapabilitiesInit.PETRIFIED).ifPresent(cap ->
                {
                    if (RevivalConfig.nitric_revival_early.get() && cap.getTimePetrified() < RevivalConfig.nitric_revival_early_time.get()*20 && living.getActiveEffectsMap().containsKey(EffectInit.PETRIFICATION.get()))
                        living.getActiveEffectsMap().put(EffectInit.PETRIFICATION.get(), new MobEffectInstance(EffectInit.PETRIFICATION.get(), 100, 0, false, false, true));
                    else if(RevivalConfig.nitric_revival_late.get() && cap.getTimePetrified() > RevivalConfig.nitric_revival_late_time.get() && living instanceof Player player)
                        player.getActiveEffectsMap().put(EffectInit.PETRIFICATION.get(), new MobEffectInstance(EffectInit.PETRIFICATION.get(), 100, 0, false, false, true));
                });
            }
            if(stack.getItem().equals(Items.GLASS_BOTTLE))
            {
                if(living instanceof Bat)
                {
                    stack.shrink(1);
                    event.getEntity().getInventory().add(new ItemStack(ItemInit.NITRIC_ACID_FLASK.get()));
                }
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
        if(event.getTarget() instanceof LivingEntity living)
        {
            living.getCapability(CapabilitiesInit.PETRIFIED).ifPresent(cap ->
            {
                if(cap.isPetrified())
                {
                    if(!cap.isBroken())
                    {
                        if(PetrificationConfig.petrified_entity_damage.get())
                            cap.setBreakStage(cap.breakStage+1);

                        if(cap.getBreakStage() >= 9 && PetrificationConfig.petrified_entity_destroy.get())
                        {
                            living.setInvulnerable(false);
                            living.hurt(PetrificationDamageSource.source(event.getEntity().level(), ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(StoneMedusa.MOD_ID, "petrification"))), Float.MAX_VALUE);
                        }
                        event.setCanceled(true);
                    }

                }

            });
        }

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


}
