package net.mistersecret312.stonemedusa.advancements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

import java.util.Optional;

public class PetrifiedCriterion extends SimpleCriterionTrigger<PetrifiedCriterion.PetrifiedTriggerInstance>
{
	@Override
	public Codec<PetrifiedTriggerInstance> codec()
	{
		return PetrifiedTriggerInstance.CODEC;
	}

	public void trigger(ServerPlayer player, ItemStack stack)
	{
		this.trigger(player, (trigger -> trigger.matches(stack)));
	}

	public static record PetrifiedTriggerInstance(Optional<ContextAwarePredicate> player,
												Optional<ItemPredicate> item) implements SimpleInstance
	{
		public static final Codec<PetrifiedTriggerInstance> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
				EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(PetrifiedTriggerInstance::player),
				ItemPredicate.CODEC.optionalFieldOf("item").forGetter(PetrifiedTriggerInstance::item)
		).apply(instance, PetrifiedTriggerInstance::new));

		public boolean matches(ItemStack stack)
		{
			if(this.item.isPresent() && !this.item.get().test(stack))
				return false;

			return true;
		}
	}
}
