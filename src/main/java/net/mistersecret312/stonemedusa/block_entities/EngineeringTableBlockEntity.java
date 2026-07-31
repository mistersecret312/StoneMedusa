package net.mistersecret312.stonemedusa.block_entities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.mistersecret312.stonemedusa.init.BlockEntityInit;
import net.mistersecret312.stonemedusa.menus.EngineeringTableMenu;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class EngineeringTableBlockEntity extends BlockEntity implements MenuProvider
{
	public final ItemStackHandler handler = new ItemStackHandler(9);
	protected final Lazy<IItemHandler> lazyHandler = Lazy.of(() -> handler);

	public EngineeringTableBlockEntity(BlockPos pos, BlockState blockState)
	{
		super(BlockEntityInit.ENGINEERING_TABLE.get(), pos, blockState);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
	{
		super.loadAdditional(tag, registries);
		handler.deserializeNBT(registries, tag.getCompound("inventory"));
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
	{
		tag.put("inventory", handler.serializeNBT(registries));
		super.saveAdditional(tag, registries);
	}

	@Override
	public @Nullable Packet<ClientGamePacketListener> getUpdatePacket()
	{
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries)
	{
		return saveWithoutMetadata(registries);
	}

	@Override
	public void invalidateCapabilities()
	{
		lazyHandler.invalidate();
		super.invalidateCapabilities();
	}

	public IItemHandler getItemHandler()
	{
		return lazyHandler.get();
	}

	@Nullable
	public IItemHandler getItemHandler(Direction side)
	{
		if(!side.equals(Direction.UP))
			return lazyHandler.get();
		return null;
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
