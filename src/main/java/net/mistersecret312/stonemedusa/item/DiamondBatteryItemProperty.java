package net.mistersecret312.stonemedusa.item;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.mistersecret312.stonemedusa.config.MedusaConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DiamondBatteryItemProperty implements ClampedItemPropertyFunction
{
    @Override
    public float unclampedCall(@NotNull ItemStack stack, @Nullable ClientLevel pLevel, @Nullable LivingEntity pEntity, int pSeed)
    {
        int energy = DiamondBatteryItem.getEnergy(stack);
        int maxEnergy = MedusaConfig.max_energy.get();
        if(energy > maxEnergy*0.75f && energy <= maxEnergy)
            return 1f;
        if(energy > maxEnergy*0.5f && energy <= maxEnergy*0.75f)
            return 0.75f;
        if(energy > maxEnergy*0.25f && energy <= maxEnergy*0.5f)
            return 0.5f;
        if(energy > 0 && energy <= maxEnergy*0.25f)
            return 0.25f;
        if(energy == 0)
            return 0f;

        return 1f;
    }
}
