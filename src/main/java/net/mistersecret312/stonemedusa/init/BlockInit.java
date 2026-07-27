package net.mistersecret312.stonemedusa.init;

import net.mistersecret312.stonemedusa.StoneMedusa;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BlockInit
{
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(StoneMedusa.MODID);

	public static void register(IEventBus bus)
	{
		BLOCKS.register(bus);
	}
}
