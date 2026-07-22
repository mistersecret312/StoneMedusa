package net.mistersecret312.stonemedusa.init;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.medusa.MedusaBeam;
import net.mistersecret312.stonemedusa.medusa.MedusaBeamType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class BeamTypeInit
{
	public static final ResourceKey<Registry<MedusaBeamType<?>>> REGISTRY_KEY =
			ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(StoneMedusa.MODID,
					"medusa_beam_type"));
	public static final Registry<MedusaBeamType<?>> REGISTRY = new RegistryBuilder<>(REGISTRY_KEY).sync(true).create();
	public static final DeferredRegister<MedusaBeamType<?>> TYPES = DeferredRegister.create(REGISTRY, StoneMedusa.MODID);

	public static final DeferredHolder<MedusaBeamType<?>, MedusaBeamType<MedusaBeam>> DEFAULT =
			TYPES.register("default", () -> new MedusaBeamType<>(MedusaBeam::deserializeNBT, MedusaBeam.STREAM_CODEC));

	public static void register(IEventBus bus)
	{
		TYPES.register(bus);
	}

}
