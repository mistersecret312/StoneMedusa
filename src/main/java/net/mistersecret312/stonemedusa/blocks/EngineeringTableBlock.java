package net.mistersecret312.stonemedusa.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.mistersecret312.stonemedusa.block_entities.EngineeringTableBlockEntity;
import org.jetbrains.annotations.Nullable;

public class EngineeringTableBlock extends BaseEntityBlock
{
	public static final MapCodec<EngineeringTableBlock> CODEC = simpleCodec(EngineeringTableBlock::new);

	public EngineeringTableBlock(Properties properties)
	{
		super(properties);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
											   BlockHitResult hitResult)
	{
		if(!level.isClientSide() && level.getBlockEntity(pos) instanceof EngineeringTableBlockEntity blockEntity)
		{
			player.openMenu(new SimpleMenuProvider(blockEntity, Component.literal("Engineering Table")), pos);
			return InteractionResult.SUCCESS;
		}

		return InteractionResult.PASS;
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec()
	{
		return CODEC;
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState)
	{
		return new EngineeringTableBlockEntity(blockPos, blockState);
	}
}
