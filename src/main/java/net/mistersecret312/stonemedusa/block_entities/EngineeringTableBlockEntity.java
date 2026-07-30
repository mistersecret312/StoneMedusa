package net.mistersecret312.stonemedusa.block_entities;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.mistersecret312.stonemedusa.init.BlockEntityInit;
import net.mistersecret312.stonemedusa.menus.EngineeringTableMenu;
import org.jetbrains.annotations.Nullable;

public class EngineeringTableBlockEntity extends BlockEntity implements MenuProvider
{

	public EngineeringTableBlockEntity(BlockPos pos, BlockState blockState)
	{
		super(BlockEntityInit.ENGINEERING_TABLE.get(), pos, blockState);
	}

	@Override
	public Component getDisplayName()
	{
		return Component.literal("Engineering Table");
	}

	@Override
	public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player)
	{
		return new EngineeringTableMenu(i, inventory, this);
	}
}
