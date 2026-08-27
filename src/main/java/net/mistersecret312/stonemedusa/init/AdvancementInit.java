package net.mistersecret312.stonemedusa.init;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.advancements.DepetrifiedCriterion;
import net.mistersecret312.stonemedusa.advancements.DepetrifyCriterion;
import net.mistersecret312.stonemedusa.advancements.PetrifiedCriterion;
import net.mistersecret312.stonemedusa.advancements.PetrifyCriterion;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class AdvancementInit
{
	public static final DeferredRegister<CriterionTrigger<?>> TRIGGER_TYPES = DeferredRegister.create(
			Registries.TRIGGER_TYPE, StoneMedusa.MODID);

	public static final Supplier<PetrifyCriterion> PETRIFY = TRIGGER_TYPES.register(
			"petrify", PetrifyCriterion::new);
	public static final Supplier<PetrifiedCriterion> PETRIFIED = TRIGGER_TYPES.register(
			"petrified", PetrifiedCriterion::new);

	public static final Supplier<DepetrifyCriterion> DEPETRIFY = TRIGGER_TYPES.register(
			"depetrify", DepetrifyCriterion::new);
	public static final Supplier<DepetrifiedCriterion> DEPETRIFIED = TRIGGER_TYPES.register(
			"depetrified", DepetrifiedCriterion::new);
	public static void register(IEventBus eventBus)
	{
		TRIGGER_TYPES.register(eventBus);
	}
}
