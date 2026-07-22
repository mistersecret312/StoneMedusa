package net.mistersecret312.stonemedusa.init;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.medusa.MedusaBeam;
import net.mistersecret312.stonemedusa.medusa.MedusaBeamType;
import net.mistersecret312.stonemedusa.medusa.source.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class BeamSourceInit
{
	public static final ResourceKey<Registry<MedusaSourceType<?>>> REGISTRY_KEY =
			ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(StoneMedusa.MODID,
					"medusa_source_type"));
	public static final Registry<MedusaSourceType<?>> REGISTRY = new RegistryBuilder<>(REGISTRY_KEY).sync(true).create();
	public static final DeferredRegister<MedusaSourceType<?>> TYPES = DeferredRegister.create(REGISTRY, StoneMedusa.MODID);

	public static final DeferredHolder<MedusaSourceType<?>, MedusaSourceType<PlayerSource>> PLAYER =
			TYPES.register("player", () -> new MedusaSourceType<>(PlayerSource::new, PlayerSource.STREAM_CODEC));
	public static final DeferredHolder<MedusaSourceType<?>, MedusaSourceType<EntitySource>> ENTITY =
			TYPES.register("entity", () -> new MedusaSourceType<>(EntitySource::new, EntitySource.STREAM_CODEC));
	public static final DeferredHolder<MedusaSourceType<?>, MedusaSourceType<BlockSource>> BLOCK =
			TYPES.register("block", () -> new MedusaSourceType<>(BlockSource::new, BlockSource.STREAM_CODEC));
	public static final DeferredHolder<MedusaSourceType<?>, MedusaSourceType<InventorySource>> INVENTORY =
			TYPES.register("inventory", () -> new MedusaSourceType<>(InventorySource::new, InventorySource.STREAM_CODEC));

	public static void register(IEventBus bus)
	{
		TYPES.register(bus);
	}
}
