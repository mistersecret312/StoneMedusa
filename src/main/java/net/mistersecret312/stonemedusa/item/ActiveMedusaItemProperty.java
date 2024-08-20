package net.mistersecret312.stonemedusa.item;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ActiveMedusaItemProperty implements ClampedItemPropertyFunction
{
    @Override
    public float unclampedCall(ItemStack pStack, @Nullable ClientLevel pLevel, @Nullable LivingEntity pEntity, int pSeed)
    {
        if(pEntity != null && pStack.getItem() instanceof MedusaItem medusa)
            return medusa.isActive(pStack) ? 0 : 1;

        return 0;
    }
}
