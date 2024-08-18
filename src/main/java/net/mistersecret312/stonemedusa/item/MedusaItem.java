package net.mistersecret312.stonemedusa.item;

import net.minecraft.ChatFormatting;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SnowballItem;
import net.minecraft.world.level.Level;
import net.mistersecret312.stonemedusa.entity.MedusaProjectile;
import net.mistersecret312.stonemedusa.init.ItemInit;

public class MedusaItem extends Item
{
    public static final String ENERGY = "energy";
    public static final String RADIUS = "radius";
    public static final String DELAY = "delay";
    public static final String START_DELAY = "start_delay";
    public static final int maxEnergy = 1000000;
    public static final float maxRadius = 30F;

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

        return stack;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand)
    {
        ItemStack itemstack = pPlayer.getItemInHand(pUsedHand);
        pLevel.playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(),
                SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F));
        if (!pLevel.isClientSide) {
            MedusaProjectile medusaProjectile = new MedusaProjectile(pLevel, pPlayer,
                    this.getEnergy(itemstack), this.getRadius(itemstack), this.getDelay(itemstack));
            medusaProjectile.setItem(itemstack);
            medusaProjectile.setDelay(this.getDelay(itemstack));
            medusaProjectile.shootFromRotation(pPlayer, pPlayer.getXRot(), pPlayer.getYRot(), 0.0F, 1F, 0F);
            pLevel.addFreshEntity(medusaProjectile);
        }

        pPlayer.awardStat(Stats.ITEM_USED.get(this));
        if (!pPlayer.getAbilities().instabuild) {
            itemstack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected)
    {
        this.setDelay(pStack, this.getStartDelay(pStack));
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


}
