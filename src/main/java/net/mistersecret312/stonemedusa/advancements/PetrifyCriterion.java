package net.mistersecret312.stonemedusa.advancements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;

import java.util.Optional;
import java.util.function.Predicate;

public class PetrifyCriterion extends SimpleCriterionTrigger<PetrifyCriterion.PetrifyTriggerInstance>
{
	@Override
	public Codec<PetrifyTriggerInstance> codec()
	{
		return PetrifyTriggerInstance.CODEC;
	}

	public void trigger(ServerPlayer player, Entity entity, ItemStack stack)
	{
		LootContext lootcontext = EntityPredicate.createContext(player, entity);
		this.trigger(player, (trigger -> trigger.matches(lootcontext, stack)));
	}

	public static record PetrifyTriggerInstance(Optional<ContextAwarePredicate> player,
												Optional<ContextAwarePredicate> entity,
												Optional<ItemPredicate> item) implements SimpleCriterionTrigger.SimpleInstance
	{
		public static final Codec<PetrifyTriggerInstance> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
				EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(PetrifyTriggerInstance::player),
				EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("entity").forGetter(PetrifyTriggerInstance::entity),
				ItemPredicate.CODEC.optionalFieldOf("item").forGetter(PetrifyTriggerInstance::item)
		).apply(instance, PetrifyTriggerInstance::new));

		public boolean matches(LootContext context, ItemStack stack)
		{
			if(this.entity.isPresent() && !this.entity.get().matches(context))
				return false;
			if(this.item.isPresent() && !this.item.get().test(stack))
				return false;

			return true;
		}
	}
}
