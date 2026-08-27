package net.mistersecret312.stonemedusa.advancements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

import java.util.Optional;

public class DepetrifyCriterion extends SimpleCriterionTrigger<DepetrifyCriterion.DepetrifyTriggerInstance>
{
	@Override
	public Codec<DepetrifyTriggerInstance> codec()
	{
		return DepetrifyTriggerInstance.CODEC;
	}

	public void trigger(ServerPlayer player, Entity entity)
	{
		this.trigger(player, (DepetrifyTriggerInstance::matches));
	}

	public static record DepetrifyTriggerInstance(Optional<ContextAwarePredicate> player) implements SimpleInstance
	{
		public static final Codec<DepetrifyTriggerInstance> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
				EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(DepetrifyTriggerInstance::player)
		).apply(instance, DepetrifyTriggerInstance::new));

		public boolean matches()
		{
			return true;
		}
	}
}
