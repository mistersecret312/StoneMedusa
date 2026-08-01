package net.mistersecret312.stonemedusa.menus;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.mistersecret312.stonemedusa.block_entities.EngineeringTableBlockEntity;
import net.mistersecret312.stonemedusa.init.BlockInit;
import net.mistersecret312.stonemedusa.init.MenuInit;

public class EngineeringTableMenu extends AbstractContainerMenu
{
	public final EngineeringTableBlockEntity blockEntity;
	private final Level level;

	public EngineeringTableMenu(int containerId, Inventory inv, FriendlyByteBuf buf)
	{
		this(containerId, inv, inv.player.level().getBlockEntity(buf.readBlockPos()));
	}

	public EngineeringTableMenu(int containerId, Inventory inventory, BlockEntity blockEntity)
	{
		super(MenuInit.ENGINEERING_RESEARCH.get(), containerId);
		this.blockEntity = (EngineeringTableBlockEntity) blockEntity;
		this.level = inventory.player.level();
	}

	@Override
	public ItemStack quickMoveStack(Player player, int i)
	{
		return ItemStack.EMPTY;
	}

	@Override
	public boolean stillValid(Player player)
	{
		return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
				player, BlockInit.ENGINEERING_TABLE.get());
	}


}
