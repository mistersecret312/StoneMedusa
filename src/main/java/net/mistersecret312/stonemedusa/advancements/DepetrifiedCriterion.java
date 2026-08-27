package net.mistersecret312.stonemedusa.advancements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class DepetrifiedCriterion extends SimpleCriterionTrigger<DepetrifiedCriterion.DepetrifiedTriggerInstance>
{
	@Override
	public Codec<DepetrifiedTriggerInstance> codec()
	{
		return DepetrifiedTriggerInstance.CODEC;
	}

	public void trigger(ServerPlayer player)
	{
		this.trigger(player, (DepetrifiedTriggerInstance::matches));
	}

	public static record DepetrifiedTriggerInstance(Optional<ContextAwarePredicate> player) implements SimpleInstance
	{
		public static final Codec<DepetrifiedTriggerInstance> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
				EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(DepetrifiedTriggerInstance::player)
		).apply(instance, DepetrifiedTriggerInstance::new));

		public boolean matches()
		{
			return true;
		}
	}
}
