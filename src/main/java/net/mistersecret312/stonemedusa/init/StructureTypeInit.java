package net.mistersecret312.stonemedusa.init;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.worldgen.structures.MedusaPyramid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class StructureTypeInit
{
	public static final DeferredRegister<StructureType<?>> DEFERRED_REGISTRY_STRUCTURE =
			DeferredRegister.create(Registries.STRUCTURE_TYPE, StoneMedusa.MODID);

	public static final DeferredHolder<StructureType<?>, StructureType<?>> MEDUSA_PYRAMID =
			DEFERRED_REGISTRY_STRUCTURE.register("medusa_pyramid", () -> typeConvert(MedusaPyramid.CODEC));

	private static <S extends Structure> StructureType<S> typeConvert(MapCodec<S> codec)
	{
		return () -> codec;
	}

	public static void register(IEventBus eventBus)
	{
		DEFERRED_REGISTRY_STRUCTURE.register(eventBus);
	}

}
