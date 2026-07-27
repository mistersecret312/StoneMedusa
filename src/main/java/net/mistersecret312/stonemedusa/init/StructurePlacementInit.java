package net.mistersecret312.stonemedusa.init;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.worldgen.UniqueStructurePlacement;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class StructurePlacementInit
{
	public static final DeferredRegister<StructurePlacementType<?>> STRUCTURE_PLACEMENT_TYPES =
			DeferredRegister.create(Registries.STRUCTURE_PLACEMENT, StoneMedusa.MODID);

	public static final DeferredHolder<StructurePlacementType<?>, StructurePlacementType<UniqueStructurePlacement>> UNIQUE_PLACEMENT = STRUCTURE_PLACEMENT_TYPES.register("unique_placement", () -> typeConvert(UniqueStructurePlacement.CODEC));
	public static final DeferredHolder<StructurePlacementType<?>, StructurePlacementType<UniqueStructurePlacement.Pyramid>> MEDUSA_PYRAMID_PLACEMENT = STRUCTURE_PLACEMENT_TYPES.register("medusa_pyramid_placement", () -> typeConvert(UniqueStructurePlacement.Pyramid.CODEC));

	private static <T extends StructurePlacement> StructurePlacementType<T> typeConvert(
			MapCodec<T> structurePlacementTypeCodec)
	{
		return () -> structurePlacementTypeCodec;
	}

	public static void register(IEventBus eventBus)
	{
		STRUCTURE_PLACEMENT_TYPES.register(eventBus);
	}

}
