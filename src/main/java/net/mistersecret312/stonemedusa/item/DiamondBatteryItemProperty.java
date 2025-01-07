package net.mistersecret312.stonemedusa.item;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class DiamondBatteryItemProperty implements ClampedItemPropertyFunction
{
    @Override
    public float unclampedCall(ItemStack stack, @Nullable ClientLevel pLevel, @Nullable LivingEntity pEntity, int pSeed)
    {
        int energy = DiamondBatteryItem.getEnergy(stack);
        if(energy > 750000 && energy <= 1000000)
            return 1f;
        if(energy > 500000 && energy <= 750000)
            return 0.75f;
        if(energy > 250000 && energy <= 500000)
            return 0.5f;
        if(energy > 0 && energy <= 250000)
            return 0.25f;
        if(energy == 0)
            return 0f;

        return 1f;
    }
}
