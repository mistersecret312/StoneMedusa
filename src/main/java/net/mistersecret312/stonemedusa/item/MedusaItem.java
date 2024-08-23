package net.mistersecret312.stonemedusa.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.TickTask;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.registries.ForgeRegistries;
import net.mistersecret312.stonemedusa.entity.MedusaProjectile;
import net.mistersecret312.stonemedusa.init.ItemInit;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class MedusaItem extends Item
{
    public static final String ENERGY = "energy";
    public static final String RADIUS = "radius";
    public static final String ACTIVE_COUNTER = "activeTicks";
    public static final String DELAY = "delay";
    public static final String START_DELAY = "start_delay";
    public static final String IS_ACTIVE = "isActive";
    public static final String IS_COUNTINDOWN_ACTIVE= "isCountdownActive";
    public static final String TARGET_ENTITY_TYPE = "targetType";
    public static final int maxEnergy = 1000000;
    public static final float maxRadius = 200F;

    public MedusaItem(Properties pProperties)
    {
        super(pProperties);
    }

    public static ItemStack getMedusa(MedusaItem item, int energy, float radius, int delay)
    {
        ItemStack stack = new ItemStack(ItemInit.MEDUSA.get());
        item.setEnergy(stack, energy);
        item.setRadius(stack, radius);
        item.setDelay(stack, delay);
        item.setStartDelay(stack, delay);
        item.setActive(stack, false);
        item.setCountdownActive(stack, false);
        item.setActiveTicks(stack, 0);
        item.setTargetEntityType(stack, null);

        return stack;
    }

    public static ItemStack getMedusa(MedusaItem item, int energy, float radius, int delay, int startDelay, boolean active, boolean countdown, @Nullable ResourceKey<EntityType<?>> targetType)
    {
        ItemStack stack = new ItemStack(ItemInit.MEDUSA.get());
        item.setEnergy(stack, energy);
        item.setRadius(stack, radius);
        item.setDelay(stack, delay);
        item.setStartDelay(stack, startDelay);
        item.setActive(stack, active);
        item.setCountdownActive(stack, countdown);
        item.setActiveTicks(stack, 0);
        item.setTargetEntityType(stack, null);

        return stack;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand)
    {
        if(player.level().isClientSide())
            return InteractionResult.PASS;

        System.out.println(ForgeRegistries.ENTITY_TYPES.getKey(target.getType()));
        this.setTargetEntityType(stack,
                ResourceKey.create(ForgeRegistries.ENTITY_TYPES.getRegistryKey(),
                        ForgeRegistries.ENTITY_TYPES.getKey(target.getType())));

        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand)
    {
        ItemStack itemstack = pPlayer.getItemInHand(pUsedHand);
        if(Screen.hasShiftDown())
            return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());

        pLevel.playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(),
                SoundEvents.ENDER_PEARL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F));
        if (!pLevel.isClientSide)
            summonMedusa(itemstack, pLevel, pPlayer, true);

        pPlayer.awardStat(Stats.ITEM_USED.get(this));
        itemstack.shrink(1);


        return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int pSlotId, boolean pIsSelected)
    {
        if(level.isClientSide())
            return;

        if(this.isActive(stack) && this.getActiveTicks(stack) <= this.getRadius(stack)*45)
        {
            this.setActiveTicks(stack, this.getActiveTicks(stack)+1);
        } else this.setActiveTicks(stack, 0);

        if(this.getActiveTicks(stack) == this.getRadius(stack)*45)
            stack.shrink(1);

        if(this.isCountdownActive(stack))
        {
            this.setDelay(stack, this.getDelay(stack)-1);
            if(this.getDelay(stack) <= 0)
            {
                this.setCountdownActive(stack, false);
                this.setActive(stack, true);

                summonMedusa(stack, level, entity, false);
            }
        }
        else this.setDelay(stack, this.getStartDelay(stack));
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity)
    {
        if(entity.level().isClientSide())
            return false;

        if(this.isCountdownActive(stack))
        {
            this.setDelay(stack, this.getDelay(stack)-1);
            if(this.getDelay(stack) <= 0)
            {
                this.setCountdownActive(stack, false);
                this.setActive(stack, true);

                entity.discard();
                summonMedusa(stack, entity.level(), entity, false);
            }
        }
        return false;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
    {
        return false;
    }

    public void summonMedusa(ItemStack stack, Level level, Entity entity, boolean shoot)
    {
        if(entity instanceof LivingEntity living)
        {
            MedusaProjectile medusaProjectile = new MedusaProjectile(level, living,
                    this.getEnergy(stack), this.getRadius(stack), this.getDelay(stack), true, this.isActive(stack), this.getTargetEntityType(stack));
            medusaProjectile.setItem(stack);
            medusaProjectile.setDelay(this.getDelay(stack));
            medusaProjectile.shootFromRotation(living, living.getXRot(), living.getYRot(), 0.0F, shoot? 1F : 0f, 0F);
            level.addFreshEntity(medusaProjectile);
        }
        else
        {
            MedusaProjectile medusaProjectile = new MedusaProjectile(level,
                    this.getEnergy(stack), this.getRadius(stack), this.getDelay(stack), true, this.isActive(stack), this.getTargetEntityType(stack));
            medusaProjectile.setItem(stack);
            medusaProjectile.setDelay(this.getDelay(stack));
            medusaProjectile.shootFromRotation(entity, entity.getXRot(), entity.getYRot(), 0.0F, shoot ? 1F : 0F, 0F);
            medusaProjectile.setPos(entity.blockPosition().getX(), entity.blockPosition().getY(), entity.blockPosition().getZ());
            level.addFreshEntity(medusaProjectile);
        }

    }

    @Override
    public int getBarWidth(ItemStack pStack)
    {
        return Math.round(13.0F * (float)this.getEnergy(pStack) / (float)maxEnergy);
    }

    @Override
    public int getBarColor(ItemStack pStack)
    {
        return 5636095;
    }

    @Override
    public boolean isBarVisible(ItemStack pStack)
    {
        return true;
    }

    public int getEnergy(ItemStack stack)
    {
        if(stack.getTag() != null && stack.getTag().contains(ENERGY))
            return stack.getTag().getInt(ENERGY);
        else return 0;
    }

    public float getRadius(ItemStack stack)
    {
        if(stack.getTag() != null && stack.getTag().contains(RADIUS))
            return stack.getTag().getFloat(RADIUS);
        else return 0;
    }

    public int getDelay(ItemStack stack)
    {
        if(stack.getTag() != null && stack.getTag().contains(DELAY))
            return stack.getTag().getInt(DELAY);
        else return 0;
    }

    public int getStartDelay(ItemStack stack)
    {
        if(stack.getTag() != null && stack.getTag().contains(START_DELAY))
            return stack.getTag().getInt(START_DELAY);
        else return 0;
    }

    public boolean isActive(ItemStack stack)
    {
        if(stack.getTag() != null && stack.getTag().contains(IS_ACTIVE))
            return stack.getTag().getBoolean(IS_ACTIVE);
        else return false;
    }

    public boolean isCountdownActive(ItemStack stack)
    {
        if(stack.getTag() != null && stack.getTag().contains(IS_COUNTINDOWN_ACTIVE))
            return stack.getTag().getBoolean(IS_COUNTINDOWN_ACTIVE);
        else return false;
    }

    public int getActiveTicks(ItemStack stack)
    {
        if(stack.getTag() != null && stack.getTag().contains(ACTIVE_COUNTER))
            return stack.getTag().getInt(ACTIVE_COUNTER);
        else return 0;
    }

    public ResourceKey<EntityType<?>> getTargetEntityType(ItemStack stack)
    {
        String type = "";
        if(stack.getTag() != null && stack.getTag().contains(TARGET_ENTITY_TYPE))
            type = stack.getTag().getString(TARGET_ENTITY_TYPE);

        if(!type.isBlank() && ResourceLocation.tryParse(type) != null)
        {
            return ResourceKey.create(ForgeRegistries.ENTITY_TYPES.getRegistryKey(), ResourceLocation.tryParse(type));
        }
        else return null;
    }

    public void setEnergy(ItemStack stack, int energy)
    {
        if(stack.getTag() != null)
            stack.getTag().putInt(ENERGY, Math.min(energy, maxEnergy));
        else stack.getOrCreateTag().putInt(ENERGY, Math.min(energy, maxEnergy));
    }

    public void setRadius(ItemStack stack, float radius)
    {
        if (stack.getTag() != null)
            stack.getTag().putFloat(RADIUS, Math.min(radius, maxRadius));
        else stack.getOrCreateTag().putFloat(RADIUS, Math.min(radius, maxRadius));
    }

    public void setDelay(ItemStack stack, int delay)
    {
        if(stack.getTag() != null)
            stack.getTag().putInt(DELAY, delay);
        else stack.getOrCreateTag().putInt(DELAY, delay);
    }

    public void setStartDelay(ItemStack stack, int delay)
    {
        if(stack.getTag() != null)
            stack.getTag().putInt(START_DELAY, delay);
        else stack.getOrCreateTag().putInt(START_DELAY, delay);
    }

    public void setActive(ItemStack stack, boolean active)
    {
        if(stack.getTag() != null)
            stack.getTag().putBoolean(IS_ACTIVE, active);
        else stack.getOrCreateTag().putBoolean(IS_ACTIVE, active);
    }

    public void setCountdownActive(ItemStack stack, boolean active)
    {
        if(stack.getTag() != null)
            stack.getTag().putBoolean(IS_COUNTINDOWN_ACTIVE, active);
        else stack.getOrCreateTag().putBoolean(IS_COUNTINDOWN_ACTIVE, active);
    }

    public void setActiveTicks(ItemStack stack, int delay)
    {
        if(stack.getTag() != null)
            stack.getTag().putInt(ACTIVE_COUNTER, delay);
        else stack.getOrCreateTag().putInt(ACTIVE_COUNTER, delay);
    }

    public void setTargetEntityType(ItemStack stack, @Nullable ResourceKey<EntityType<?>> type)
    {
        String targetType = type == null ? "" : type.location().toString();

        if(stack.getTag() != null)
            stack.getTag().putString(TARGET_ENTITY_TYPE, targetType);
        else stack.getOrCreateTag().putString(TARGET_ENTITY_TYPE, targetType);
    }
}
