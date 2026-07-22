package net.mistersecret312.stonemedusa.items.properties;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.mistersecret312.stonemedusa.items.MedusaItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MedusaActivatedProperty implements ClampedItemPropertyFunction
{
	@Override
	public float unclampedCall(@NotNull ItemStack stack, @Nullable ClientLevel clientLevel,
							   @Nullable LivingEntity livingEntity, int i)
	{
		return (MedusaItem.isActive(stack)) ? 0 : 1;
	}
}
