package net.mistersecret312.stonemedusa.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SpawnerBlock;
import net.mistersecret312.stonemedusa.config.MedusaConfig;
import net.mistersecret312.stonemedusa.entity.MedusaProjectile;
import net.mistersecret312.stonemedusa.init.ItemInit;
import org.jetbrains.annotations.Nullable;

import java.text.NumberFormat;
import java.util.List;
import java.util.Objects;

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
        setActive(stack, false);
        item.setCountdownActive(stack, false);
        item.setActiveTicks(stack, 0);
        item.setTargetEntityType(stack, "");

        return stack;
    }

    public static ItemStack getMedusa(MedusaItem item, int energy, float radius, int delay, int startDelay, boolean active, boolean countdown, String targetType)
    {
        ItemStack stack = new ItemStack(ItemInit.MEDUSA.get());
        item.setEnergy(stack, energy);
        item.setRadius(stack, radius);
        item.setDelay(stack, delay);
        item.setStartDelay(stack, startDelay);
        setActive(stack, active);
        item.setCountdownActive(stack, countdown);
        item.setActiveTicks(stack, 0);
        item.setTargetEntityType(stack, targetType);

        return stack;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced)
    {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        NumberFormat percentage = NumberFormat.getPercentInstance();
        percentage.setMaximumFractionDigits(1);
        percentage.setMinimumFractionDigits(0);
        pTooltipComponents.add(Component.translatable("medusa.charge").withStyle(ChatFormatting.GREEN).append(percentage.format((double)this.getEnergy(pStack) / (double)MedusaConfig.max_energy.get())));
        if(!this.getTargetEntityType(pStack).isEmpty() && !this.getTargetEntityType(pStack).equals("minecraft:player"))
        {
            pTooltipComponents.add(Component.translatable("medusa.target").withStyle(ChatFormatting.DARK_GREEN).append(Component.translatable(BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.tryParse(this.getTargetEntityType(pStack))).getDescriptionId()).withStyle(ChatFormatting.DARK_GREEN)));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand)
    {
        ItemStack itemstack = pPlayer.getItemInHand(pUsedHand);
        if(pUsedHand.equals(InteractionHand.OFF_HAND) && pPlayer.isShiftKeyDown())
        {
            if(pPlayer.getItemInHand(InteractionHand.MAIN_HAND).isEmpty())
            {
                pPlayer.setItemInHand(InteractionHand.MAIN_HAND, DiamondBatteryItem.getBattery(ItemInit.BATTERY.get(), this.getEnergy(itemstack)));
                this.setEnergy(itemstack, 0);
            }
            else if(pPlayer.getItemInHand(InteractionHand.MAIN_HAND).is(ItemInit.BATTERY.get()))
            {
                this.setEnergy(itemstack, DiamondBatteryItem.getEnergy(pPlayer.getItemInHand(InteractionHand.MAIN_HAND)));
                pPlayer.setItemInHand(InteractionHand.MAIN_HAND, Items.AIR.getDefaultInstance());
            }

            return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());
        }

        if(Screen.hasShiftDown())
            return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());

        pLevel.playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(),
                SoundEvents.ENDER_PEARL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F));
        if (!pLevel.isClientSide)
            summonMedusa(itemstack, pLevel, pPlayer, true);

        pPlayer.awardStat(Stats.ITEM_USED.get(this));
        pPlayer.setItemInHand(pUsedHand, ItemStack.EMPTY);

        return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int pSlotId, boolean pIsSelected)
    {
        if(level.isClientSide())
            return;

        if(isActive(stack))
            stack.shrink(1);

        if(isActive(stack) && this.getActiveTicks(stack) <= (this.getRadius(stack)*MedusaConfig.base_speed.get())+MedusaProjectile.IDLE_TIME)
        {
            this.setActiveTicks(stack, this.getActiveTicks(stack)+1);
        } else this.setActiveTicks(stack, 0);


        if(isCountdownActive(stack))
        {
            this.setDelay(stack, this.getDelay(stack)-1);
            if(this.getDelay(stack) <= 0)
            {
                this.setCountdownActive(stack, false);
                setActive(stack, true);

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

        if(isActive(stack) && !isCountdownActive(stack))
            entity.discard();

        if(isCountdownActive(stack))
        {
            this.setDelay(stack, this.getDelay(stack)-1);
            if(this.getDelay(stack) <= 0)
            {
                this.setCountdownActive(stack, false);
                setActive(stack, true);

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
                    this.getEnergy(stack), this.getRadius(stack), this.getDelay(stack), isCountdownActive(stack), isActive(stack), this.getTargetEntityType(stack), false);
            medusaProjectile.setItem(stack);
            medusaProjectile.setDelay(this.getDelay(stack));
            medusaProjectile.shootFromRotation(living, living.getXRot(), living.getYRot(), 0.0F, shoot? 1F : 0f, 0F);
            level.addFreshEntity(medusaProjectile);
        }
        else
        {
            MedusaProjectile medusaProjectile = new MedusaProjectile(level,
                    this.getEnergy(stack), this.getRadius(stack), this.getDelay(stack), isCountdownActive(stack), isActive(stack), this.getTargetEntityType(stack), false);
            medusaProjectile.setItem(stack);
            medusaProjectile.setDelay(this.getDelay(stack));
            medusaProjectile.shootFromRotation(entity, entity.getXRot(), entity.getYRot(), 0.0F, shoot ? 1F : 0F, 0F);
            medusaProjectile.setPos(entity.position().x, entity.position().y, entity.position().z);
            level.addFreshEntity(medusaProjectile);
        }

    }

    @Override
    public int getBarWidth(ItemStack pStack)
    {
        return Math.round(13.0F * (float)this.getEnergy(pStack) / (float)MedusaConfig.max_energy.get());
    }

    @Override
    public int getBarColor(ItemStack pStack)
    {
        return 5636095;
    }

    @Override
    public boolean isBarVisible(ItemStack pStack)
    {
        return this.getEnergy(pStack) != MedusaConfig.max_energy.get();
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

    public static boolean isActive(ItemStack stack)
    {
        if(stack.getTag() != null && stack.getTag().contains(IS_ACTIVE))
            return stack.getTag().getBoolean(IS_ACTIVE);
        else return false;
    }

    public static boolean isCountdownActive(ItemStack stack)
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

    public String getTargetEntityType(ItemStack stack)
    {
        if(stack.getTag() != null && stack.getTag().contains(TARGET_ENTITY_TYPE))
            return stack.getTag().getString(TARGET_ENTITY_TYPE);
        else return "";
    }

    public void setEnergy(ItemStack stack, int energy)
    {
        stack.getOrCreateTag().putInt(ENERGY, Math.min(energy, MedusaConfig.max_energy.get()));
    }

    public void setRadius(ItemStack stack, float radius)
    {
        stack.getOrCreateTag().putFloat(RADIUS, Math.min(radius, MedusaConfig.max_radius.get().floatValue()));
    }

    public void setDelay(ItemStack stack, int delay)
    {
        stack.getOrCreateTag().putInt(DELAY, delay);
    }

    public void setStartDelay(ItemStack stack, int delay)
    {
        stack.getOrCreateTag().putInt(START_DELAY, delay);
    }

    public static void setActive(ItemStack stack, boolean active)
    {
        stack.getOrCreateTag().putBoolean(IS_ACTIVE, active);
    }

    public void setCountdownActive(ItemStack stack, boolean active)
    {
        stack.getOrCreateTag().putBoolean(IS_COUNTINDOWN_ACTIVE, active);
    }

    public void setActiveTicks(ItemStack stack, int delay)
    {
        stack.getOrCreateTag().putInt(ACTIVE_COUNTER, delay);
    }

    public void setTargetEntityType(ItemStack stack, String type)
    {
        stack.getOrCreateTag().putString(TARGET_ENTITY_TYPE, type);
    }
}
