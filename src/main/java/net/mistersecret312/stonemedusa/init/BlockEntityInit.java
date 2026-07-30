package net.mistersecret312.stonemedusa.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.block_entities.EngineeringTableBlockEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class BlockEntityInit
{
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
			DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, StoneMedusa.MODID);

	public static final Supplier<BlockEntityType<EngineeringTableBlockEntity>> ENGINEERING_TABLE =
			BLOCK_ENTITIES.register("engineering_table",
					() -> BlockEntityType.Builder.of(EngineeringTableBlockEntity::new,
							BlockInit.ENGINEERING_TABLE.get()).build(null));

	public static void register(IEventBus bus)
	{
		BLOCK_ENTITIES.register(bus);
	}
}
