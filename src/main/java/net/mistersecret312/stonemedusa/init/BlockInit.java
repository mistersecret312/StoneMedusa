package net.mistersecret312.stonemedusa.init;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.blocks.EngineeringTableBlock;
import net.mistersecret312.stonemedusa.blocks.RustyMedusaBlock;
import net.mistersecret312.stonemedusa.blocks.RustyMedusaItemBlock;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BlockInit
{
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(StoneMedusa.MODID);

	public static final DeferredBlock<Block> ENGINEERING_TABLE = registerBlock("engineering_table",
			() -> new EngineeringTableBlock(BlockBehaviour.Properties.of().noOcclusion()));
	public static final DeferredBlock<Block> RUSTY_MEDUSA_BLOCK = registerBlock("rusty_medusa_block",
			() -> new RustyMedusaBlock(BlockBehaviour.Properties.of().strength(4f).sound(SoundType.METAL).requiresCorrectToolForDrops()));
	public static final DeferredBlock<Block> RUSTY_MEDUSA = registerBlock("rusty_medusa",
			() -> new RustyMedusaItemBlock(BlockBehaviour.Properties.of().noOcclusion().noCollission().strength(0f).sound(SoundType.METAL)));

	private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block)
	{
		DeferredBlock<T> toReturn = BLOCKS.register(name, block);
		registerBlockItem(name, toReturn);
		return toReturn;
	}

	private static <T extends Block> DeferredHolder<Item, BlockItem> registerBlockItem(String name, DeferredBlock<T> block)
	{
		return ItemInit.ITEMS.register(name, () -> new BlockItem(block.get(),
				new Item.Properties()));
	}

	public static void register(IEventBus bus)
	{
		BLOCKS.register(bus);
	}
}
