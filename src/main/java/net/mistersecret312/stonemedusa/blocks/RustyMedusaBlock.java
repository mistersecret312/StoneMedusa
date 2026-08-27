package net.mistersecret312.stonemedusa.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;

public class RustyMedusaBlock extends FallingBlock
{
	public static final MapCodec<RustyMedusaBlock> CODEC = simpleCodec(RustyMedusaBlock::new);

	public RustyMedusaBlock(Properties properties)
	{
		super(properties);
	}

	@Override
	public int getDustColor(BlockState state, BlockGetter level, BlockPos pos)
	{
		return 0xFCF7BE;
	}

	@Override
	protected MapCodec<? extends FallingBlock> codec()
	{
		return CODEC;
	}
}
