package net.mistersecret312.stonemedusa.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.entity.ThrownMedusaEntity;
import net.mistersecret312.stonemedusa.entity.ThrownRevivalFluidEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EntityInit
{
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
			DeferredRegister.create(Registries.ENTITY_TYPE, StoneMedusa.MODID);

	public static final DeferredHolder<EntityType<?>, EntityType<ThrownRevivalFluidEntity>> THROWN_REVIVAL_FLUID =
			ENTITY_TYPES.register("thrown_revival_fluid",
					() -> EntityType.Builder.<ThrownRevivalFluidEntity>of(ThrownRevivalFluidEntity::new, MobCategory.MISC)
											.sized(0.25f, 0.25f)
											.build(ResourceLocation.fromNamespaceAndPath(StoneMedusa.MODID,
													"thrown_revival_fluid").toString()));

	public static final DeferredHolder<EntityType<?>, EntityType<ThrownMedusaEntity>> MEDUSA =
			ENTITY_TYPES.register("thrown_medusa",
					() -> EntityType.Builder.<ThrownMedusaEntity>of(ThrownMedusaEntity::new, MobCategory.MISC)
											.sized(0.25f, 0.25f)
											.build(ResourceLocation.fromNamespaceAndPath(StoneMedusa.MODID,
													"thrown_medusa").toString()));
	public static void register(IEventBus bus)
	{
		ENTITY_TYPES.register(bus);
	}
}
